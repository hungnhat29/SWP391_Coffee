package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.*;
import com.SWP391.G3PCoffee.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionRuleRepository ruleRepository;

    @Autowired
    private PromotionActionRepository actionRepository;

    @Autowired
    private PromotionCouponRepository couponRepository;

    @Autowired
    private OrderPromotionRepository orderPromotionRepository;

    @Autowired
    private UserPromotionRepository userPromotionRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartRepository cartRepository;

    private static final String BUY_ONE_GET_ONE = "BUY_ONE_GET_ONE";
    private static final String BUY_X_GET_DISCOUNT = "BUY_X_GET_DISCOUNT";
    private static final String FLASH_SALE_ALL = "FLASH_SALE_ALL";
    private static final String FLASH_SALE_CATEGORY = "FLASH_SALE_CATEGORY";
    private static final String AMOUNT_THRESHOLD_DISCOUNT = "AMOUNT_THRESHOLD_DISCOUNT";
    private static final String ORDER_DISCOUNT = "ORDER_DISCOUNT";

    private static final String RULE_PRODUCT = "PRODUCT";
    private static final String RULE_CATEGORY = "CATEGORY";
    private static final String RULE_CART_QUANTITY = "CART_QUANTITY";
    private static final String RULE_CART_AMOUNT = "CART_AMOUNT";

    private static final String ACTION_FREE_PRODUCT = "FREE_PRODUCT";
    private static final String ACTION_PERCENT_DISCOUNT = "PERCENT_DISCOUNT";
    private static final String ACTION_FIXED_DISCOUNT = "FIXED_DISCOUNT";


    // 1. Create a new promotion
    @Transactional
    public PromotionDTO createPromotion(PromotionRequest request) {
        ObjectMapper mapper = new ObjectMapper();

        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setActive(request.getActive());
        promotion.setPromotionType(request.getPromotionType());
        promotion.setPriority(request.getPriority());
        promotion.setStackable(request.getStackable());
        promotion.setUsageLimit(request.getUsageLimit());
        promotion.setUsageCount(0);

        Promotion savedPromotion = promotionRepository.save(promotion);

        // Add rules
        for (PromotionRuleDTO ruleDTO : request.getRules()) {
            PromotionRule rule = new PromotionRule();
            rule.setPromotion(savedPromotion);
            rule.setRuleType(ruleDTO.getRuleType());
            try {
                rule.setRuleData(mapper.writeValueAsString(ruleDTO.getRuleData()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error processing rule data", e);
            }
            ruleRepository.save(rule);
        }

        // Add actions
        for (PromotionActionDTO actionDTO : request.getActions()) {
            PromotionAction action = new PromotionAction();
            action.setPromotion(savedPromotion);
            action.setActionType(actionDTO.getActionType());
            try {
                action.setActionData(mapper.writeValueAsString(actionDTO.getActionData()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error processing action data", e);
            }
            actionRepository.save(action);
        }

        // Add coupons if any
        for (String code : request.getCouponCodes()) {
            PromotionCoupon coupon = new PromotionCoupon();
            coupon.setPromotion(savedPromotion);
            coupon.setCode(code);
            coupon.setUsageLimit(request.getUsageLimit());
            coupon.setUsageCount(0);
            couponRepository.save(coupon);
        }

        return convertToDTO(savedPromotion);
    }

    // 2. Apply promotions to cart
    @Transactional
    public CartDTO applyPromotionsToCart(Integer userId, String sessionId, String couponCode, Integer promotionId) {
        System.out.println("============Applying promotions with userId: " + userId + ", promotionId: " + promotionId);

        // Create the cart DTO first
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUserId(userId);
        cartDTO.setSessionId(sessionId);
        cartDTO.setCouponCode(couponCode);

        // Get cart items
        List<Cart> cartItems = cartService.getCartItems(userId, sessionId);
        System.out.println("============Found " + cartItems.size() + " cart items");

        List<CartItemDTO> cartItemDTOs = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Cart item : cartItems) {
            CartItemDTO itemDTO = new CartItemDTO();
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getProduct().getBasePrice());
            itemDTO.setSubtotal(item.getSubTotal());
            itemDTO.setSizeInfo(item.getSizeInfo());
            itemDTO.setToppingsInfo(item.getToppingsInfo());
            itemDTO.setCategoryId(item.getProduct().getCategoryId());

            cartItemDTOs.add(itemDTO);
            subtotal = subtotal.add(item.getSubTotal());
        }

        cartDTO.setItems(cartItemDTOs);
        cartDTO.setSubtotal(subtotal);
        cartDTO.setTotal(subtotal);  // Initially, total equals subtotal
        System.out.println("============Cart subtotal: " + subtotal);

        // Apply promotions based on the scenario
        List<Promotion> promotionsToApply = new ArrayList<>();

        // Case 1: Specific promotion ID was provided
        if (promotionId != null) {
            Optional<Promotion> promotionOpt = promotionRepository.findById(promotionId);
            if (!promotionOpt.isPresent() || !promotionOpt.get().isActive()) {
                System.out.println("=========Promotion ID " + promotionId + " not found in database or inactive");
                throw new IllegalArgumentException("Invalid or inactive promotion");
            }
            Promotion promotion = promotionOpt.get();
            System.out.println("============Found promotion: " + promotion.getName() + " (Type: " + promotion.getPromotionType() + ")");
            promotionsToApply.add(promotion);
        } else {
            // Case 2: Apply all active promotions if no specific ID is provided
            promotionsToApply = getActivePromotions();
            System.out.println("============Applying all active promotions: " + promotionsToApply.size() + " found");
        }

        // Sort promotions by priority (higher priority first)
        promotionsToApply.sort(Comparator.comparingInt(Promotion::getPriority).reversed());

        // Apply promotions
        List<AppliedPromotionDTO> appliedPromotions = new ArrayList<>();
        BigDecimal totalDiscount = BigDecimal.ZERO;
        boolean hasNonStackableApplied = false;

        for (Promotion promotion : promotionsToApply) {
            System.out.println("Attempting to apply promotion: " + promotion.getName() +
                    " (Stackable: " + promotion.getStackable() + ", Priority: " + promotion.getPriority() + ")");

            // Skip if a non-stackable promotion has already been applied and this one isn't stackable
            if (hasNonStackableApplied && !promotion.getStackable()) {
                System.out.println("Skipping non-stackable promotion " + promotion.getName() +
                        " as a non-stackable promotion is already applied");
                continue;
            }

            // Check usage limit before applying
            if (promotion.getUsageLimit() != null && promotion.getUsageCount() >= promotion.getUsageLimit()) {
                System.out.println("Skipping promotion " + promotion.getName() + " as it has reached its usage limit");
                continue;
            }

            AppliedPromotionDTO appliedPromotion = applyPromotion(promotion, cartDTO);
            if (appliedPromotion != null) {
                System.out.println("Successfully applied promotion: " + promotion.getName() +
                        " with discount: " + appliedPromotion.getDiscountAmount());
                appliedPromotions.add(appliedPromotion);
                totalDiscount = totalDiscount.add(appliedPromotion.getDiscountAmount());

                // Increment usage count immediately upon successful application
                promotion.setUsageCount(promotion.getUsageCount() + 1);
                if (promotion.getUsageLimit() != null && promotion.getUsageCount() >= promotion.getUsageLimit()) {
                    promotion.setActive(false); // Deactivate if usage limit is reached
                }
                promotionRepository.save(promotion);
                System.out.println("Updated usage count for promotion " + promotion.getName() + ": " + promotion.getUsageCount());

                // Mark if a non-stackable promotion was applied
                if (!promotion.getStackable()) {
                    hasNonStackableApplied = true;
                }
            } else {
                System.out.println("Failed to apply promotion: " + promotion.getName() +
                        " (Type: " + promotion.getPromotionType() + ")");
            }
        }

        cartDTO.setAppliedPromotions(appliedPromotions);
        cartDTO.setTotal(subtotal.subtract(totalDiscount).max(BigDecimal.ZERO));
        System.out.println("Final cart total after promotions: " + cartDTO.getTotal());
        System.out.println("Applied promotions count: " + appliedPromotions.size());

        return cartDTO;
    }

    // 3. Apply specific promotion to cart
    private AppliedPromotionDTO applyPromotion(Promotion promotion, CartDTO cart) {
        String promotionType = promotion.getPromotionType();

        switch (promotionType) {
            case BUY_ONE_GET_ONE:
                return applyBuyOneGetOne(promotion, cart);
            case BUY_X_GET_DISCOUNT:
                return applyBuyXGetDiscount(promotion, cart);
            case FLASH_SALE_ALL:
                return applyFlashSaleAll(promotion, cart);
            case FLASH_SALE_CATEGORY:
                return applyFlashSaleCategory(promotion, cart);
            case AMOUNT_THRESHOLD_DISCOUNT:
                return applyAmountThresholdDiscount(promotion, cart);
            case ORDER_DISCOUNT:
                return applyOrderDiscount(promotion, cart);
            default:
                return null;
        }
    }

    // 4. Buy One Get One
    private AppliedPromotionDTO applyBuyOneGetOne(Promotion promotion, CartDTO cart) {
        try {
            List<PromotionRule> rules = ruleRepository.findByPromotionAndRuleType(promotion, RULE_PRODUCT);
            System.out.println("BOGO Debug: Found " + rules.size() + " product rules for promotion " + promotion.getId());

            if (rules.isEmpty()) {
                System.out.println("BOGO Debug: No product rules found for promotion " + promotion.getId());
                return null;
            }

            // Get the action rules for the free product
            List<PromotionAction> actions = actionRepository.findByPromotionAndActionType(promotion, ACTION_FREE_PRODUCT);
            System.out.println("BOGO Debug: Found " + actions.size() + " action rules for promotion " + promotion.getId());

            if (actions.isEmpty()) {
                System.out.println("BOGO Debug: No action rules found for promotion " + promotion.getId());
                return null;
            }

            // Get qualifying product ID
            PromotionRule rule = rules.get(0);
            System.out.println("BOGO Debug: Rule data: " + rule.getRuleData());

            Map<String, Object> ruleData = objectMapper.readValue(rule.getRuleData(), new TypeReference<Map<String, Object>>() {});
            Integer qualifyingProductId = (Integer) ruleData.get("productId");
            System.out.println("BOGO Debug: Qualifying product ID: " + qualifyingProductId);

            // Get free product ID from actions
            PromotionAction action = actions.get(0);
            System.out.println("BOGO Debug: Action data: " + action.getActionData());

            Map<String, Object> actionData = objectMapper.readValue(action.getActionData(), new TypeReference<Map<String, Object>>() {});
            Integer freeProductId = (Integer) actionData.get("productId");
            Integer freeProductQty = actionData.containsKey("quantity") ? (Integer) actionData.get("quantity") : 1;
            System.out.println("BOGO Debug: Free product ID: " + freeProductId + ", quantity: " + freeProductQty);

            // Debug cart contents
            System.out.println("BOGO Debug: Cart contains " + cart.getItems().size() + " items:");
            for (CartItemDTO item : cart.getItems()) {
                System.out.println("BOGO Debug: Product ID: " + item.getProductId() + ", Quantity: " + item.getQuantity());
            }

            // Count how many of the qualifying product in the cart
            int qualifyingCount = 0;
            for (CartItemDTO item : cart.getItems()) {
                if (item.getProductId().equals(qualifyingProductId)) {
                    qualifyingCount += item.getQuantity();
                }
            }
            System.out.println("BOGO Debug: Found " + qualifyingCount + " qualifying products in cart");

            // If no qualifying products, not eligible
            if (qualifyingCount < 1) {
                System.out.println("BOGO Debug: Not enough qualifying products");
                return null;
            }

            // Calculate the number of free items
            int eligiblePairs = qualifyingCount / 2;
            int totalFreeItems = eligiblePairs * freeProductQty;
            System.out.println("BOGO Debug: Eligible pairs: " + eligiblePairs + ", Total free items: " + totalFreeItems);

            if (totalFreeItems == 0) {
                System.out.println("BOGO Debug: No free items calculated");
                return null;
            }

            // Find the free product price
            Optional<Product> freeProduct = productRepository.findById(freeProductId);
            if (!freeProduct.isPresent()) {
                return null;
            }

            BigDecimal freeProductPrice = freeProduct.get().getBasePrice();
            BigDecimal discountAmount = freeProductPrice.multiply(BigDecimal.valueOf(totalFreeItems));

            // Create a special cart item for the free product
            CartItemDTO freeItemDTO = new CartItemDTO();
            freeItemDTO.setProductId(freeProductId);
            freeItemDTO.setProductName(freeProduct.get().getName() + " (Free)");
            freeItemDTO.setQuantity(totalFreeItems);
            freeItemDTO.setPrice(BigDecimal.ZERO); // Price is zero since it's free
            freeItemDTO.setSubtotal(BigDecimal.ZERO);
            freeItemDTO.setCategoryId(freeProduct.get().getCategoryId());
            freeItemDTO.setIsPromotionalItem(true); // Mark as promotional item
            freeItemDTO.setRelatedPromotionId(promotion.getId());

            // Add the free item to the cart
            cart.getItems().add(freeItemDTO);

            // Create the applied promotion record
            AppliedPromotionDTO result = new AppliedPromotionDTO();
            result.setPromotionId(promotion.getId());
            result.setPromotionName(promotion.getName());
            result.setPromotionType(promotion.getPromotionType());
            result.setDescription("Buy One Get One: " + totalFreeItems + " free " + freeProduct.get().getName());
            result.setDiscountAmount(discountAmount);
            result.setFreeProductId(freeProductId);
            result.setFreeProductQuantity(totalFreeItems);

            return result;
        } catch (Exception e) {
            System.err.println("Error applying Buy One Get One promotion: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 5. Buy X Get Discount - Updated to apply discount to next order
    private AppliedPromotionDTO applyBuyXGetDiscount(Promotion promotion, CartDTO cart) {
        try {
            // Get the cart quantity rule
            List<PromotionRule> quantityRules = ruleRepository.findByPromotionAndRuleType(promotion, RULE_CART_QUANTITY);
            if (quantityRules.isEmpty()) {
                return null;
            }

            PromotionRule quantityRule = quantityRules.get(0);
            Map<String, Object> quantityRuleData = objectMapper.readValue(quantityRule.getRuleData(),
                    new TypeReference<Map<String, Object>>() {
                    });

            Integer minQuantity = (Integer) quantityRuleData.get("minQuantity");

            // Count total items in cart
            int totalItems = cart.getItems().stream()
                    .mapToInt(CartItemDTO::getQuantity)
                    .sum();

            // Get the percent discount action
            List<PromotionAction> actions = actionRepository.findByPromotionAndActionType(promotion, ACTION_PERCENT_DISCOUNT);
            if (actions.isEmpty()) {
                return null;
            }

            PromotionAction action = actions.get(0);
            Map<String, Object> actionData = objectMapper.readValue(action.getActionData(),
                    new TypeReference<Map<String, Object>>() {
                    });

            BigDecimal percentDiscount = new BigDecimal(actionData.get("percentDiscount").toString());

            // If the cart meets quantity requirement, save for next order
            if (totalItems >= minQuantity && cart.getUserId() != null) {
                // Save eligibility for next order
                saveUserPromotionForNextOrder(cart.getUserId(), promotion, percentDiscount);

                // Return information about the earned promotion without applying discount now
                AppliedPromotionDTO result = new AppliedPromotionDTO();
                result.setPromotionId(promotion.getId());
                result.setPromotionName(promotion.getName());
                result.setPromotionType(promotion.getPromotionType());
                result.setDescription("You've earned " + percentDiscount + "% off your next order!");
                result.setDiscountAmount(BigDecimal.ZERO); // No discount on current order

                return result;
            } else {
                // Check if the user has an eligible promotion to apply to this order
                if (cart.getUserId() != null) {
                    UserPromotion userPromotion = findEligibleUserPromotion(cart.getUserId(), promotion.getId());

                    if (userPromotion != null && !userPromotion.getIsRedeemed()) {
                        // Calculate discount amount (apply to entire order)
                        BigDecimal orderTotal = cart.getItems().stream()
                                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal discountAmount = orderTotal
                                .multiply(userPromotion.getPercentDiscount())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                        // Mark the promotion as redeemed
                        userPromotion.setIsRedeemed(true);
                        userPromotion.setUsageCount(userPromotion.getUsageCount() + 1);
                        userPromotion.setStatus("USED");
                        userPromotionRepository.save(userPromotion);

                        // Apply the discount
                        AppliedPromotionDTO result = new AppliedPromotionDTO();
                        result.setPromotionId(promotion.getId());
                        result.setPromotionName(promotion.getName());
                        result.setPromotionType(promotion.getPromotionType());
                        result.setDescription("Applied " + userPromotion.getPercentDiscount() + "% discount from your previous order");
                        result.setDiscountAmount(discountAmount);

                        return result;
                    }
                }

                return null; // No eligible promotion to apply
            }
        } catch (Exception e) {
            System.err.println("Error applying Buy X Get Discount promotion: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to save user promotion for next order
    private void saveUserPromotionForNextOrder(Integer userId, Promotion promotion, BigDecimal percentDiscount) {
        // Check if the user already has an unused promotion of this type
        UserPromotion existingPromotion = findEligibleUserPromotion(userId, promotion.getId());

        if (existingPromotion != null && !existingPromotion.getIsRedeemed()) {
            // User already has an unredeemed promotion, no need to create a new one
            return;
        }

        // Create a new user promotion
        UserPromotion userPromotion = new UserPromotion();
        userPromotion.setUserId(userId);
        userPromotion.setPromotion(promotion);
        userPromotion.setUsageCount(0);
        userPromotion.setIsRedeemed(false);
        userPromotion.setPercentDiscount(percentDiscount);
        userPromotion.setStatus("ELIGIBLE");

        // Set expiry date (e.g., 30 days from now)
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(30);
        userPromotion.setExpiryDate(expiryDate);

        // Save the user promotion
        userPromotionRepository.save(userPromotion);
    }

    // Helper method to find eligible user promotion
    private UserPromotion findEligibleUserPromotion(Integer userId, Integer promotionId) {
        List<UserPromotion> userPromotions = userPromotionRepository.findByUserIdAndPromotionIdAndIsRedeemedFalse(
                userId, promotionId);

        if (userPromotions.isEmpty()) {
            return null;
        }

        // Find the first non-expired promotion
        LocalDateTime now = LocalDateTime.now();
        return userPromotions.stream()
                .filter(up -> up.getExpiryDate() == null || up.getExpiryDate().isAfter(now))
                .findFirst()
                .orElse(null);
    }

    // 6. Flash Sale All Products (Modified)
    private AppliedPromotionDTO applyFlashSaleAll(Promotion promotion, CartDTO cart) {
        try {
            // Get the percent discount action
            List<PromotionAction> actions = actionRepository.findByPromotionAndActionType(promotion, ACTION_PERCENT_DISCOUNT);
            if (actions.isEmpty()) {
                return null;
            }

            PromotionAction action = actions.get(0);
            Map<String, Object> actionData = objectMapper.readValue(action.getActionData(),
                    new TypeReference<Map<String, Object>>() {
                    });

            BigDecimal percentDiscount = new BigDecimal(actionData.get("percentDiscount").toString());

            // Calculate discount on base price of each product (limited to 1 quantity per product)
            BigDecimal discountAmount = cart.getItems().stream()
                    .map(item -> {
                        // Only apply discount to 1 quantity of each product
                        return item.getPrice().multiply(percentDiscount)
                                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            AppliedPromotionDTO result = new AppliedPromotionDTO();
            result.setPromotionId(promotion.getId());
            result.setPromotionName(promotion.getName());
            result.setPromotionType(promotion.getPromotionType());
            result.setDescription("Flash Sale: " + percentDiscount + "% off all products (max 1 per product)");
            result.setDiscountAmount(discountAmount);

            return result;
        } catch (Exception e) {
            System.err.println("Error applying Flash Sale All promotion: " + e.getMessage());
            return null;
        }
    }

    // 7. Flash Sale Category (Modified)
    private AppliedPromotionDTO applyFlashSaleCategory(Promotion promotion, CartDTO cart) {
        try {
            // Get the category rule
            List<PromotionRule> categoryRules = ruleRepository.findByPromotionAndRuleType(promotion, RULE_CATEGORY);
            if (categoryRules.isEmpty()) {
                return null;
            }

            PromotionRule categoryRule = categoryRules.get(0);
            Map<String, Object> categoryRuleData = objectMapper.readValue(categoryRule.getRuleData(),
                    new TypeReference<Map<String, Object>>() {
                    });

            Integer categoryId = (Integer) categoryRuleData.get("categoryId");

            // Get the percent discount action
            List<PromotionAction> actions = actionRepository.findByPromotionAndActionType(promotion, ACTION_PERCENT_DISCOUNT);
            if (actions.isEmpty()) {
                return null;
            }

            PromotionAction action = actions.get(0);
            Map<String, Object> actionData = objectMapper.readValue(action.getActionData(),
                    new TypeReference<Map<String, Object>>() {
                    });

            BigDecimal percentDiscount = new BigDecimal(actionData.get("percentDiscount").toString());

            // Calculate discount on base price of each product in the category (limited to 1 quantity per product)
            BigDecimal discountAmount = cart.getItems().stream()
                    .filter(item -> item.getCategoryId().equals(categoryId))
                    .map(item -> {
                        // Only apply discount to 1 quantity of each product
                        return item.getPrice().multiply(percentDiscount)
                                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return null;
            }

            AppliedPromotionDTO result = new AppliedPromotionDTO();
            result.setPromotionId(promotion.getId());
            result.setPromotionName(promotion.getName());
            result.setPromotionType(promotion.getPromotionType());
            result.setDescription("Flash Sale: " + percentDiscount + "% off category items (max 1 per product)");
            result.setDiscountAmount(discountAmount);

            return result;
        } catch (Exception e) {
            System.err.println("Error applying Flash Sale Category promotion: " + e.getMessage());
            return null;
        }
    }

    // 8. Amount Threshold Discount
    private AppliedPromotionDTO applyAmountThresholdDiscount(Promotion promotion, CartDTO cart) {
        try {
            // Get the cart amount rule
            List<PromotionRule> amountRules = ruleRepository.findByPromotionAndRuleType(promotion, RULE_CART_AMOUNT);
            if (amountRules.isEmpty()) {
                return null;
            }

            PromotionRule amountRule = amountRules.get(0);
            Map<String, Object> amountRuleData = objectMapper.readValue(amountRule.getRuleData(),
                    new TypeReference<Map<String, Object>>() {
                    });

            BigDecimal minAmount = new BigDecimal(amountRuleData.get("minAmount").toString());

            // Check if cart subtotal meets the threshold
            if (cart.getSubtotal().compareTo(minAmount) < 0) {
                return null;
            }

            // Find discount action (could be percentage or fixed)
            List<PromotionAction> percentActions = actionRepository.findByPromotionAndActionType(promotion, ACTION_PERCENT_DISCOUNT);
            List<PromotionAction> fixedActions = actionRepository.findByPromotionAndActionType(promotion, ACTION_FIXED_DISCOUNT);

            BigDecimal discountAmount;
            String description;

            if (!percentActions.isEmpty()) {
                PromotionAction action = percentActions.get(0);
                Map<String, Object> actionData = objectMapper.readValue(action.getActionData(),
                        new TypeReference<Map<String, Object>>() {
                        });

                BigDecimal percentDiscount = new BigDecimal(actionData.get("percentDiscount").toString());

                discountAmount = cart.getSubtotal()
                        .multiply(percentDiscount)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                description = "Spend " + minAmount + " or more, get " + percentDiscount + "% off";
            } else if (!fixedActions.isEmpty()) {
                PromotionAction action = fixedActions.get(0);
                Map<String, Object> actionData = objectMapper.readValue(action.getActionData(),
                        new TypeReference<Map<String, Object>>() {
                        });

                discountAmount = new BigDecimal(actionData.get("fixedDiscount").toString());
                description = "Spend " + minAmount + " or more, get " + discountAmount + " off";
            } else {
                return null;
            }

            AppliedPromotionDTO result = new AppliedPromotionDTO();
            result.setPromotionId(promotion.getId());
            result.setPromotionName(promotion.getName());
            result.setPromotionType(promotion.getPromotionType());
            result.setDescription(description);
            result.setDiscountAmount(discountAmount);

            return result;
        } catch (Exception e) {
            System.err.println("Error applying Amount Threshold Discount promotion: " + e.getMessage());
            return null;
        }
    }

    // 9. Order Discount
    private AppliedPromotionDTO applyOrderDiscount(Promotion promotion, CartDTO cart) {
        try {
            // For order discount, we need either fixed or percent discount
            List<PromotionAction> percentActions = actionRepository.findByPromotionAndActionType(promotion, ACTION_PERCENT_DISCOUNT);
            List<PromotionAction> fixedActions = actionRepository.findByPromotionAndActionType(promotion, ACTION_FIXED_DISCOUNT);

            BigDecimal discountAmount;
            String description;

            if (!percentActions.isEmpty()) {
                PromotionAction action = percentActions.get(0);
                Map<String, Object> actionData = objectMapper.readValue(action.getActionData(),
                        new TypeReference<Map<String, Object>>() {
                        });

                BigDecimal percentDiscount = new BigDecimal(actionData.get("percentDiscount").toString());

                discountAmount = cart.getSubtotal()
                        .multiply(percentDiscount)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                description = percentDiscount + "% off your order";
            } else if (!fixedActions.isEmpty()) {
                PromotionAction action = fixedActions.get(0);
                Map<String, Object> actionData = objectMapper.readValue(action.getActionData(),
                        new TypeReference<Map<String, Object>>() {
                        });

                discountAmount = new BigDecimal(actionData.get("fixedDiscount").toString());

                // Make sure discount doesn't exceed order total
                discountAmount = discountAmount.min(cart.getSubtotal());

                description = discountAmount + " off your order";
            } else {
                return null;
            }

            AppliedPromotionDTO result = new AppliedPromotionDTO();
            result.setPromotionId(promotion.getId());
            result.setPromotionName(promotion.getName());
            result.setPromotionType(promotion.getPromotionType());
            result.setDescription(description);
            result.setDiscountAmount(discountAmount);

            return result;
        } catch (Exception e) {
            System.err.println("Error applying Order Discount promotion: " + e.getMessage());
            return null;
        }
    }

    // 10. Get all promotions
    public List<PromotionDTO> getAllPromotions() {
        List<Promotion> promotions = promotionRepository.findAll();
        return promotions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 11. Get active promotions
    public List<Promotion> getActivePromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findAllActivePromotions(now);
    }

    // 12. Get promotion by ID
    public PromotionDTO getPromotionById(Integer id) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(id);
        return promotionOpt.map(this::convertToDTO).orElse(null);
    }

    // 13. Update promotion
    @Transactional
    public PromotionDTO updatePromotion(Integer id, PromotionRequest request) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(id);
        if (!promotionOpt.isPresent()) {
            return null;
        }

        Promotion promotion = promotionOpt.get();
        promotion.setName(request.getName());
        promotion.setDescription(request.getDescription());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setActive(request.getActive());
        promotion.setPromotionType(request.getPromotionType());
        promotion.setPriority(request.getPriority());
        promotion.setStackable(request.getStackable());
        promotion.setUsageLimit(request.getUsageLimit());

        promotionRepository.save(promotion);

        // Delete existing rules and actions
        List<PromotionRule> existingRules = ruleRepository.findByPromotion(promotion);
        ruleRepository.deleteAll(existingRules);

        List<PromotionAction> existingActions = actionRepository.findByPromotion(promotion);
        actionRepository.deleteAll(existingActions);

        // Add new rules
        for (PromotionRuleDTO ruleDTO : request.getRules()) {
            PromotionRule rule = new PromotionRule();
            rule.setPromotion(promotion);
            rule.setRuleType(ruleDTO.getRuleType());
            try {
                rule.setRuleData(objectMapper.writeValueAsString(ruleDTO.getRuleData()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error processing rule data", e);
            }
            ruleRepository.save(rule);
        }

        // Add new actions
        for (PromotionActionDTO actionDTO : request.getActions()) {
            PromotionAction action = new PromotionAction();
            action.setPromotion(promotion);
            action.setActionType(actionDTO.getActionType());
            try {
                action.setActionData(objectMapper.writeValueAsString(actionDTO.getActionData()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error processing action data", e);
            }
            actionRepository.save(action);
        }

        // Update coupons if needed
        if (request.getCouponCodes() != null && !request.getCouponCodes().isEmpty()) {
            // Delete existing coupons
            List<PromotionCoupon> existingCoupons = couponRepository.findByPromotion(promotion);
            couponRepository.deleteAll(existingCoupons);

            // Add new coupons
            for (String code : request.getCouponCodes()) {
                PromotionCoupon coupon = new PromotionCoupon();
                coupon.setPromotion(promotion);
                coupon.setCode(code);
                coupon.setUsageLimit(request.getUsageLimit());
                coupon.setUsageCount(0);
                couponRepository.save(coupon);
            }
        }

        return convertToDTO(promotion);
    }

    // 14. Delete promotion

    @Transactional
    public boolean deletePromotion(Integer id) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(id);
        if (!promotionOpt.isPresent()) {
            return false;
        }
        promotionRepository.deleteById(id); // Simply delete the promotion by ID
        return true;
    }

    // 15. Toggle promotion active status
    @Transactional
    public PromotionDTO togglePromotionStatus(Integer id) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(id);
        if (!promotionOpt.isPresent()) {
            return null;
        }

        Promotion promotion = promotionOpt.get();
        promotion.setActive(!promotion.isActive());
        promotionRepository.save(promotion);

        return convertToDTO(promotion);
    }

    // 16. Validate coupon code
    public boolean validateCouponCode(String couponCode) {
        Optional<PromotionCoupon> couponOpt = couponRepository.findByCode(couponCode);
        if (!couponOpt.isPresent()) {
            return false;
        }

        PromotionCoupon coupon = couponOpt.get();
        Promotion promotion = coupon.getPromotion();

        // Check if promotion is active and within date range
        boolean isValid = promotion.isActive()
                && promotion.getStartDate().isBefore(LocalDateTime.now())
                && promotion.getEndDate().isAfter(LocalDateTime.now());

        // Check usage limits
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            isValid = false;
        }

        if (promotion.getUsageLimit() != null && promotion.getUsageCount() >= promotion.getUsageLimit()) {
            isValid = false;
        }

        return isValid;
    }

    // 17. Record promotion usage
    @Transactional
    public void recordPromotionUsage(Integer orderId, List<AppliedPromotionDTO> appliedPromotions) {
        for (AppliedPromotionDTO appliedPromotion : appliedPromotions) {
            Integer promotionId = appliedPromotion.getPromotionId();
            Optional<Promotion> promotionOpt = promotionRepository.findById(promotionId);

            if (promotionOpt.isPresent()) {
                Promotion promotion = promotionOpt.get();

                // Increment promotion usage count
                promotion.setUsageCount(promotion.getUsageCount() + 1);
                promotionRepository.save(promotion);

                // Create OrderPromotion record
                OrderPromotion orderPromotion = new OrderPromotion();
                orderPromotion.setOrderId(orderId);
                orderPromotion.setPromotion(promotion);
                orderPromotion.setDiscountAmount(appliedPromotion.getDiscountAmount());
                orderPromotion.setDescription(appliedPromotion.getDescription());

                // Save OrderPromotion
                // Assuming you have OrderPromotionRepository
                orderPromotionRepository.save(orderPromotion);
            }
        }
    }

    // 18. Assign promotion to user
    @Transactional
    public UserPromotionDTO assignPromotionToUser(Integer userId, Integer promotionId) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(promotionId);
        if (!promotionOpt.isPresent()) {
            return null;
        }

        Promotion promotion = promotionOpt.get();

        // Check if user already has this promotion
        Optional<UserPromotion> existingUserPromotionOpt = userPromotionRepository.findByUserIdAndPromotion(userId, promotion);
        if (existingUserPromotionOpt.isPresent()) {
            // Already assigned
            return convertToUserPromotionDTO(existingUserPromotionOpt.get());
        }

        // Create new user promotion
        UserPromotion userPromotion = new UserPromotion();
        userPromotion.setUserId(userId);
        userPromotion.setPromotion(promotion);
        userPromotion.setUsageCount(0);
        userPromotion.setIsRedeemed(false);
        userPromotion.setExpiryDate(promotion.getEndDate());

        UserPromotion savedUserPromotion = userPromotionRepository.save(userPromotion);

        return convertToUserPromotionDTO(savedUserPromotion);
    }

    // 19. Get user's promotions
    public List<UserPromotionDTO> getUserPromotions(Integer userId) {
        List<UserPromotion> userPromotions = userPromotionRepository.findByUserId(userId);
        return userPromotions.stream()
                .map(this::convertToUserPromotionDTO)
                .collect(Collectors.toList());
    }

    // 20. Get user's available (not redeemed) promotions
    public List<UserPromotionDTO> getUserAvailablePromotions(Integer userId) {
        List<UserPromotion> userPromotions = userPromotionRepository.findByUserIdAndIsRedeemed(userId, false);
        return userPromotions.stream()
                .map(this::convertToUserPromotionDTO)
                .collect(Collectors.toList());
    }

    // 21. Redeem user promotion
    @Transactional
    public boolean redeemUserPromotion(Integer userPromotionId) {
        Optional<UserPromotion> userPromotionOpt = userPromotionRepository.findById(userPromotionId);
        if (!userPromotionOpt.isPresent()) {
            return false;
        }

        UserPromotion userPromotion = userPromotionOpt.get();

        // Check if already redeemed
        if (userPromotion.getIsRedeemed()) {
            return false;
        }

        // Check if expired
        if (userPromotion.getExpiryDate() != null &&
                userPromotion.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Mark as redeemed
        userPromotion.setIsRedeemed(true);
        userPromotion.setUsageCount(userPromotion.getUsageCount() + 1);
        userPromotionRepository.save(userPromotion);

        return true;
    }

    // 22. Convert entity to DTO
    private PromotionDTO convertToDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setId(promotion.getId());
        dto.setName(promotion.getName());
        dto.setDescription(promotion.getDescription());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setActive(promotion.isActive());
        dto.setPromotionType(promotion.getPromotionType());
        dto.setPriority(promotion.getPriority());
        dto.setStackable(promotion.getStackable());
        dto.setUsageLimit(promotion.getUsageLimit());
        dto.setUsageCount(promotion.getUsageCount());

        // Get rules
        List<PromotionRule> rules = ruleRepository.findByPromotion(promotion);
        List<PromotionRuleDTO> ruleDTOs = new ArrayList<>();

        for (PromotionRule rule : rules) {
            PromotionRuleDTO ruleDTO = new PromotionRuleDTO();
            ruleDTO.setId(rule.getId());
            ruleDTO.setRuleType(rule.getRuleType());

            try {
                Map<String, Object> ruleData = objectMapper.readValue(rule.getRuleData(),
                        new TypeReference<Map<String, Object>>() {
                        });
                ruleDTO.setRuleData(ruleData);
            } catch (Exception e) {
                ruleDTO.setRuleData(new HashMap<>());
            }

            ruleDTOs.add(ruleDTO);
        }

        dto.setRules(ruleDTOs);

        // Get actions
        List<PromotionAction> actions = actionRepository.findByPromotion(promotion);
        List<PromotionActionDTO> actionDTOs = new ArrayList<>();

        for (PromotionAction action : actions) {
            PromotionActionDTO actionDTO = new PromotionActionDTO();
            actionDTO.setId(action.getId());
            actionDTO.setActionType(action.getActionType());

            try {
                Map<String, Object> actionData = objectMapper.readValue(action.getActionData(),
                        new TypeReference<Map<String, Object>>() {
                        });
                actionDTO.setActionData(actionData);
            } catch (Exception e) {
                actionDTO.setActionData(new HashMap<>());
            }

            actionDTOs.add(actionDTO);
        }

        dto.setActions(actionDTOs);

        // Get coupons
        List<PromotionCoupon> coupons = couponRepository.findByPromotion(promotion);
        List<String> couponCodes = coupons.stream()
                .map(PromotionCoupon::getCode)
                .collect(Collectors.toList());

        dto.setCouponCodes(couponCodes);

        return dto;
    }

    // 23. Convert UserPromotion entity to DTO
    private UserPromotionDTO convertToUserPromotionDTO(UserPromotion userPromotion) {
        UserPromotionDTO dto = new UserPromotionDTO();
        dto.setId(userPromotion.getId());
        dto.setUserId(userPromotion.getUserId());
        dto.setPromotionId(userPromotion.getPromotion().getId());
        dto.setPromotionName(userPromotion.getPromotion().getName());
        dto.setPromotionDescription(userPromotion.getPromotion().getDescription());
        dto.setPromotionType(userPromotion.getPromotion().getPromotionType());
        dto.setUsageCount(userPromotion.getUsageCount());
        dto.setIsRedeemed(userPromotion.getIsRedeemed());
        dto.setExpiryDate(userPromotion.getExpiryDate());

        return dto;
    }

    // 24. Get promotions by type
    public List<PromotionDTO> getPromotionsByType(String promotionType) {
        LocalDateTime now = LocalDateTime.now();
        List<Promotion> promotions = promotionRepository.findActivePromotionsByType(now, promotionType);
        return promotions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 25. Get promotions by rule type
    public List<PromotionDTO> getPromotionsByRuleType(String ruleType) {
        LocalDateTime now = LocalDateTime.now();
        List<Promotion> promotions = promotionRepository.findActivePromotionsByRuleType(now, ruleType);
        return promotions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

//    public CartWithPromotionsDTO getCartWithAppliedPromotions(Integer userId, String sessionId) {
//        // Create the DTO
//        CartWithPromotionsDTO cartDTO = new CartWithPromotionsDTO();
//
//        // Get cart items
//        List<Cart> cartItems;
//        if (userId != null) {
//            cartItems = cartRepository.findByUserId(userId);
//        } else {
//            cartItems = cartRepository.findBySessionId(sessionId);
//        }
//        cartDTO.setItems(cartItems);
//
//        // Calculate subtotal from cart items
//        BigDecimal subtotal = cartItems.stream()
//                .map(Cart::getSubTotal)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        cartDTO.setSubtotal(subtotal);
//
//        // Get applied promotions
//        List<AppliedPromotionDTO> appliedPromotions;
//        if (userId != null) {
//            appliedPromotions = getAppliedPromotionsForUser(userId);
//        } else {
//            appliedPromotions = getAppliedPromotionsForSession(sessionId);
//        }
//        cartDTO.setAppliedPromotions(appliedPromotions);
//
//        // Get applied coupon (if any)
////        String couponCode = getAppliedCouponCode(userId, sessionId);
////        cartDTO.setCouponCode(couponCode);
//
//        // Calculate total with discounts
//        cartDTO.calculateTotal();
//
//        return cartDTO;
//    }
//
//    private List<AppliedPromotionDTO> getAppliedPromotionsForUser(Integer userId) {
//        // This would need to be implemented based on your database structure
//        // Here's a placeholder implementation
//        List<Object[]> results = promotionRepository.findAppliedPromotionsForUser(userId);
//
//        return results.stream().map(row -> {
//            AppliedPromotionDTO dto = new AppliedPromotionDTO();
//            dto.setPromotionId((Integer) row[0]);
//            dto.setPromotionName((String) row[1]);
//            dto.setDescription((String) row[2]);
//            dto.setPromotionType((String) row[3]);
//            dto.setDiscountAmount((BigDecimal) row[4]);
////            dto.setCouponCode((String) row[5]);
//            return dto;
//        }).collect(Collectors.toList());
//    }
//
//    /**
//     * Get applied promotions for a session
//     */
//    private List<AppliedPromotionDTO> getAppliedPromotionsForSession(String sessionId) {
//        // Similar to the user version, but for session-based carts
//        List<Object[]> results = promotionRepository.findAppliedPromotionsForSession(sessionId);
//
//        return results.stream().map(row -> {
//            AppliedPromotionDTO dto = new AppliedPromotionDTO();
//            dto.setPromotionId((Integer) row[0]);
//            dto.setPromotionName((String) row[1]);
//            dto.setDescription((String) row[2]);
//            dto.setPromotionType((String) row[3]);
//            dto.setDiscountAmount((BigDecimal) row[4]);
////            dto.setCouponCode((String) row[5]);
//            return dto;
//        }).collect(Collectors.toList());
//    }
}