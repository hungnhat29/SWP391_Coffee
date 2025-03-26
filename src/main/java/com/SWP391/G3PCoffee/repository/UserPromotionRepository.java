package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.UserPromotion;
import com.SWP391.G3PCoffee.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPromotionRepository extends JpaRepository<UserPromotion, Integer> {
    List<UserPromotion> findByUserId(Integer userId);
    List<UserPromotion> findByUserIdAndIsRedeemed(Integer userId, Boolean isRedeemed);
    Optional<UserPromotion> findByUserIdAndPromotion(Integer userId, Promotion promotion);
    List<UserPromotion> findByUserIdAndPromotionIdAndIsRedeemedFalse(Integer userId, Integer promotionId);
}