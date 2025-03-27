package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.*;
import com.SWP391.G3PCoffee.repository.PromotionRepository;
import com.SWP391.G3PCoffee.service.PromotionService;
import com.SWP391.G3PCoffee.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private UserService userService;

    @Autowired
    private PromotionRepository promotionRepository;

    // =============== VIEW CONTROLLERS ===============

    /**
     * Display all promotions page
     */
    @GetMapping("")
    public String viewAllPromotions(Model model) {
        List<PromotionDTO> promotions = promotionService.getAllPromotions();
        model.addAttribute("promotions", promotions);
        return "promotions/list";
    }

    /**
     * Display promotion creation form
     */
    @GetMapping("/create")
    public String createPromotionForm(Model model) {
        model.addAttribute("promotionRequest", new PromotionRequest());
        return "promotions/create";
    }

    /**
     * Display promotion details page
     */
    @GetMapping("/{id}")
    public String viewPromotionDetails(@PathVariable Integer id, Model model) {
        PromotionDTO promotion = promotionService.getPromotionById(id);
        if (promotion == null) {
            return "redirect:/promotions?error=Promotion not found";
        }
        model.addAttribute("promotion", promotion);
        return "promotions/details";
    }

    /**
     * Display promotion edit form
     */
    @GetMapping("/{id}/edit")
    public String editPromotionForm(@PathVariable Integer id, Model model) {
        PromotionDTO promotion = promotionService.getPromotionById(id);
        if (promotion == null) {
            return "redirect:/promotions?error=Promotion not found";
        }

        // Convert DTO to request object for form
        PromotionRequest request = new PromotionRequest();
        request.setName(promotion.getName());
        request.setDescription(promotion.getDescription());
        request.setStartDate(promotion.getStartDate());
        request.setEndDate(promotion.getEndDate());
        request.setActive(promotion.getActive());
        request.setPromotionType(promotion.getPromotionType());
        request.setPriority(promotion.getPriority());
        request.setStackable(promotion.getStackable());
        request.setUsageLimit(promotion.getUsageLimit());
        request.setRules(promotion.getRules());
        request.setActions(promotion.getActions());
        request.setCouponCodes(promotion.getCouponCodes());

        model.addAttribute("promotionId", id);
        model.addAttribute("promotionRequest", request);
        return "promotions/details";
    }

    /**
     * Display user promotions page
     */
    @GetMapping("/user/{userId}")
    public String viewUserPromotions(@PathVariable Integer userId, Model model) {
        List<UserPromotionDTO> userPromotions = promotionService.getUserPromotions(userId);
        model.addAttribute("userPromotions", userPromotions);
        model.addAttribute("userId", userId);
        return "promotions/user-promotions";
    }

    // =============== API ENDPOINTS ===============

    /**
     * Create a new promotion
     */
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<PromotionDTO> createPromotion(@RequestBody PromotionRequest request) {
        PromotionDTO createdPromotion = promotionService.createPromotion(request);
        return new ResponseEntity<>(createdPromotion, HttpStatus.CREATED);
    }

    /**
     * Apply promotions to cart
     */
    @PostMapping("/api/apply-to-cart")
    @ResponseBody
    public ResponseEntity<CartDTO> applyPromotionsToCart(
            @RequestBody Map<String, Object> requestBody,
            HttpSession session,
            Authentication authentication) {

        // Extract values from request body
        String couponCode = requestBody.get("couponCode") != null ? requestBody.get("couponCode").toString() : null;
        Integer promotionId = requestBody.get("promotionId") != null ? Integer.valueOf(requestBody.get("promotionId").toString()) : null;

        // Get user ID and session ID using the same logic as the cart controller
        Integer userId = null;
        String sessionId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userService.getCustomerByEmail(email);

            if (user != null) {
                // User is authenticated, use userId
                userId = user.getId().intValue();
            } else {
                // Authentication exists but user not found
                sessionId = getOrCreateSessionId(session);
            }
        } else {
            // User is not authenticated, use session-based cart
            sessionId = getOrCreateSessionId(session);
        }

        // Now apply promotions with the correct user ID and session ID
        CartDTO cartWithPromotions = promotionService.applyPromotionsToCart(userId, sessionId, couponCode, promotionId);
        return ResponseEntity.ok(cartWithPromotions);
    }

    // Helper method to get or create session ID (copied from cart controller)
    private String getOrCreateSessionId(HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
            session.setAttribute("sessionId", sessionId);
        }
        return sessionId;
    }

    /**
     * Get all promotions (API)
     */
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<PromotionDTO>> getAllPromotions() {
        List<PromotionDTO> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(promotions);
    }

    /**
     * Get active promotions
     */
    @GetMapping("/api/active")
    @ResponseBody
    public ResponseEntity<List<PromotionDTO>> getActivePromotions() {
        List<Promotion> activePromotions = promotionService.getActivePromotions();
        List<PromotionDTO> promotionDTOs = activePromotions.stream()
                .map(promotion -> promotionService.getPromotionById(promotion.getId()))
                .toList();
        return ResponseEntity.ok(promotionDTOs);
    }

    /**
     * Get promotion by ID (API)
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable Integer id) {
        PromotionDTO promotion = promotionService.getPromotionById(id);
        if (promotion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(promotion);
    }

    /**
     * Update a promotion
     */
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<PromotionDTO> updatePromotion(
            @PathVariable Integer id,
            @RequestBody PromotionRequest request) {

        PromotionDTO updatedPromotion = promotionService.updatePromotion(id, request);
        if (updatedPromotion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPromotion);
    }

    /**
     * Delete a promotion
     */

    /**
     * Delete a promotion (API)
     */
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<String> deletePromotionAPI(@PathVariable Integer id) {
        try {
            boolean deleted = promotionService.deletePromotion(id);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Promotion with ID " + id + " not found.");
            }
            return ResponseEntity.ok("Promotion deleted successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete promotion: " + e.getMessage());
        }
    }

    /**
     * Toggle promotion active status
     */
    @PatchMapping("/api/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<PromotionDTO> togglePromotionStatus(@PathVariable Integer id) {
        PromotionDTO updatedPromotion = promotionService.togglePromotionStatus(id);
        if (updatedPromotion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPromotion);
    }

    /**
     * Validate coupon code
     */
    @GetMapping("/api/validate-coupon")
    @ResponseBody
    public ResponseEntity<Boolean> validateCouponCode(@RequestParam String couponCode) {
        boolean isValid = promotionService.validateCouponCode(couponCode);
        return ResponseEntity.ok(isValid);
    }

    /**
     * Record promotion usage
     */
    @PostMapping("/api/record-usage")
    @ResponseBody
    public ResponseEntity<Void> recordPromotionUsage(
            @RequestParam Integer orderId,
            @RequestBody List<AppliedPromotionDTO> appliedPromotions) {

        promotionService.recordPromotionUsage(orderId, appliedPromotions);
        return ResponseEntity.ok().build();
    }

    /**
     * Assign promotion to user
     */
    @PostMapping("/api/assign-to-user")
    @ResponseBody
    public ResponseEntity<UserPromotionDTO> assignPromotionToUser(
            @RequestParam Integer userId,
            @RequestParam Integer promotionId) {

        UserPromotionDTO userPromotion = promotionService.assignPromotionToUser(userId, promotionId);
        if (userPromotion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userPromotion);
    }

    /**
     * Get user's promotions (API)
     */
    @GetMapping("/api/user/{userId}")
    @ResponseBody
    public ResponseEntity<List<UserPromotionDTO>> getUserPromotions(@PathVariable Integer userId) {
        List<UserPromotionDTO> userPromotions = promotionService.getUserPromotions(userId);
        return ResponseEntity.ok(userPromotions);
    }

    /**
     * Get user's available promotions
     */
    @GetMapping("/api/user/{userId}/available")
    @ResponseBody
    public ResponseEntity<List<UserPromotionDTO>> getUserAvailablePromotions(@PathVariable Integer userId) {
        List<UserPromotionDTO> availablePromotions = promotionService.getUserAvailablePromotions(userId);
        return ResponseEntity.ok(availablePromotions);
    }

    /**
     * Redeem user promotion
     */
    @PostMapping("/api/redeem/{userPromotionId}")
    @ResponseBody
    public ResponseEntity<Boolean> redeemUserPromotion(@PathVariable Integer userPromotionId) {
        boolean redeemed = promotionService.redeemUserPromotion(userPromotionId);
        if (!redeemed) {
            return ResponseEntity.badRequest().body(false);
        }
        return ResponseEntity.ok(true);
    }

    /**
     * Get promotions by type
     */
    @GetMapping("/api/by-type/{promotionType}")
    @ResponseBody
    public ResponseEntity<List<PromotionDTO>> getPromotionsByType(@PathVariable String promotionType) {
        List<PromotionDTO> promotions = promotionService.getPromotionsByType(promotionType);
        return ResponseEntity.ok(promotions);
    }

    /**
     * Get promotions by rule type
     */
    @GetMapping("/api/by-rule-type/{ruleType}")
    @ResponseBody
    public ResponseEntity<List<PromotionDTO>> getPromotionsByRuleType(@PathVariable String ruleType) {
        List<PromotionDTO> promotions = promotionService.getPromotionsByRuleType(ruleType);
        return ResponseEntity.ok(promotions);
    }

    // =============== FORM SUBMISSION HANDLERS ===============

    /**
     * Handle promotion creation form submission
     */
    @PostMapping("/create")
    public String createPromotion(@ModelAttribute PromotionRequest request, Model model) {
        try {
            PromotionDTO createdPromotion = promotionService.createPromotion(request);
            return "redirect:/promotions/" + createdPromotion.getId() + "?success=Promotion created successfully";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to create promotion: " + e.getMessage());
            model.addAttribute("promotionRequest", request);
            return "promotions/create";
        }
    }

    /**
     * Handle promotion update form submission
     */
    @PostMapping("/{id}/edit")
    public String updatePromotion(
            @PathVariable Integer id,
            @ModelAttribute PromotionRequest request,
            Model model) {

        try {
            PromotionDTO updatedPromotion = promotionService.updatePromotion(id, request);
            if (updatedPromotion == null) {
                model.addAttribute("error", "Promotion not found");
                return "promotions/details";
            }
            return "redirect:/promotions/" + id + "?success=Promotion updated successfully";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update promotion: " + e.getMessage());
            model.addAttribute("promotionId", id);
            model.addAttribute("promotionRequest", request);
            return "promotions/details";
        }
    }

    /**
     * Handle promotion deletion
     */
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Map<String, Object>> deletePromotion(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean deleted = promotionService.deletePromotion(id);
            if (deleted) {
                response.put("success", true);
                response.put("message", "Promotion deleted successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("success", false);
                response.put("message", "Promotion not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.put("success", false);
            // Include the exception type and stack trace for better debugging
            String errorMessage = "Error deleting promotion: " + e.getClass().getSimpleName();
            if (e.getMessage() != null) {
                errorMessage += " - " + e.getMessage();
            }
            response.put("message", errorMessage);
            // Optionally, include the stack trace in the response (for debugging purposes)
            response.put("errorDetails", e.getStackTrace()[0].toString());
            // Log the exception for server-side debugging
            e.printStackTrace();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Handle promotion status toggle
     */
    @PostMapping("/{id}/toggle")
    public String toggleStatus(@PathVariable Integer id) {
        PromotionDTO promotion = promotionService.togglePromotionStatus(id);
        if (promotion == null) {
            return "redirect:/promotions?error=Promotion not found";
        }
        String status = promotion.getActive() ? "activated" : "deactivated";
        return "redirect:/promotions/" + id + "?success=Promotion " + status + " successfully";
    }

    /**
     * Handle assigning promotion to user
     */
    @PostMapping("/assign-to-user")
    public String assignPromotionToUser(
            @RequestParam Integer userId,
            @RequestParam Integer promotionId,
            Model model) {

        UserPromotionDTO userPromotion = promotionService.assignPromotionToUser(userId, promotionId);
        if (userPromotion == null) {
            return "redirect:/promotions/user/" + userId + "?error=Failed to assign promotion";
        }
        return "redirect:/promotions/user/" + userId + "?success=Promotion assigned successfully";
    }

    /**
     * Handle redeeming user promotion
     */
    @PostMapping("/redeem/{userPromotionId}")
    public String redeemUserPromotion(
            @PathVariable Integer userPromotionId,
            @RequestParam Integer userId) {

        boolean redeemed = promotionService.redeemUserPromotion(userPromotionId);
        if (!redeemed) {
            return "redirect:/promotions/user/" + userId + "?error=Failed to redeem promotion";
        }
        return "redirect:/promotions/user/" + userId + "?success=Promotion redeemed successfully";
    }
}