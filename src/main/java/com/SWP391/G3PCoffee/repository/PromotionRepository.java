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
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    @Query("SELECT p FROM Promotion p WHERE p.active = true AND p.startDate <= :now AND p.endDate >= :now ORDER BY p.priority DESC")
    List<Promotion> findAllActivePromotions(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Promotion p WHERE p.active = true AND p.startDate <= :now AND p.endDate >= :now AND p.promotionType = :promotionType ORDER BY p.priority DESC")
    List<Promotion> findActivePromotionsByType(@Param("now") LocalDateTime now, @Param("promotionType") String promotionType);

    @Query("SELECT p FROM Promotion p JOIN p.rules r WHERE p.active = true AND p.startDate <= :now AND p.endDate >= :now AND r.ruleType = :ruleType ORDER BY p.priority DESC")
    List<Promotion> findActivePromotionsByRuleType(@Param("now") LocalDateTime now, @Param("ruleType") String ruleType);

//    @Query(value = """
//    SELECT
//        p.id AS promotion_id,
//        p.name AS promotion_name,
//        p.description AS promotion_description,
//        p.promotion_type AS promotion_type,
//        op.discount_amount AS discount_amount
//    FROM
//        OrderPromotion op
//    JOIN
//        Promotion p ON op.promotion_id = p.id
//    JOIN
//        Orders o ON op.order_id = o.id
//    WHERE
//        o.user_id = :userId
//""", nativeQuery = true)
//    List<Object[]> findAppliedPromotionsForUser(@Param("userId") Integer userId);
//
//    @Query(value = """
//    SELECT
//        p.id AS promotion_id,
//        p.name AS promotion_name,
//        p.description AS promotion_description,
//        p.promotion_type AS promotion_type,
//        op.discount_amount AS discount_amount
//    FROM
//        OrderPromotion op
//    JOIN
//        Promotion p ON op.promotion_id = p.id
//    JOIN
//        Orders o ON op.order_id = o.id
//    WHERE
//        o.session_id = :sessionId
//""", nativeQuery = true)
//    List<Object[]> findAppliedPromotionsForSession(@Param("sessionId") String sessionId);
}