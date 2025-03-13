package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.constant.TypeOrder;
import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Page<Order> findByUserIdOrderByOrderDateDesc(Integer userId, Pageable pageable);

    Page<Order> findBySessionIdOrderByOrderDateDesc(String sessionId, Pageable pageable);

    Order findFirstBySessionIdOrderByOrderDateDesc(String sessionId);

    Page<Order> findByUserIdAndId(Long userId, Long id, Pageable pageable);

    Page<Order> findByUserIdAndIdAndOrderDateAfter(Long userId, Long id, LocalDateTime startDate, Pageable pageable);

    // Methods for findByUserAndStatus
    Page<Order> findByUserIdAndStatusContainingIgnoreCase(Long userId, String status, Pageable pageable);

    Page<Order> findByUserIdAndStatusContainingIgnoreCaseAndOrderDateAfter(Long userId, String status, LocalDateTime startDate, Pageable pageable);

    // Methods for findByUserAndDate
    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByUserIdAndOrderDateAfter(Long userId, LocalDateTime startDate, Pageable pageable);

    @Query("select o from Order o " +
            "where o.typeOrder = ?1 " +
            "and o.userId = ?2")
    Page<Order> getPageOrderUser(TypeOrder typeOrder, Long userId, Pageable pageable);

    @Query("select o from Order o " +
            "where o.userId = ?1")
    Page<Order> getAllOrder(Long userId, Pageable pageable);

    @Query("select o from Order o " +
            "where o.typeOrder = ?1")
    Page<Order> getPageOrderByTypeAdmin(TypeOrder typeOrder, Pageable pageable);

    @Query("select o from Order o")
    Page<Order> getAllOrderAdmin(Pageable pageable);
}