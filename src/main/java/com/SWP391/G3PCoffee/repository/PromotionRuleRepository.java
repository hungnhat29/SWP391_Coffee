package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.Promotion;
import com.SWP391.G3PCoffee.model.PromotionAction;
import com.SWP391.G3PCoffee.model.PromotionCoupon;
import com.SWP391.G3PCoffee.model.PromotionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRuleRepository extends JpaRepository<PromotionRule, Integer> {
    List<PromotionRule> findByPromotion(Promotion promotion);
    List<PromotionRule> findByPromotionAndRuleType(Promotion promotion, String ruleType);
}