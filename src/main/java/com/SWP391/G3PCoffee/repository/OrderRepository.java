package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.Order;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.constant.TypeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

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
    List<Order> getAllOrderAdmin();

    Page<Order> findByUserIdAndStatusNotInOrderByOrderDateDesc(Integer userId, List<String> excludedStatuses, Pageable pageable);

    Page<Order> findByUserIdAndIdAndStatusNotInAndOrderDateAfter(Long userId, Long orderId, List<String> excludedStatuses, LocalDateTime startDate, Pageable pageable);
    Page<Order> findByUserIdAndIdAndStatusNotIn(Long userId, Long orderId, List<String> excludedStatuses, Pageable pageable);

    Page<Order> findByUserIdAndStatusContainingIgnoreCaseAndStatusNotInAndOrderDateAfter(Long userId, String status, List<String> excludedStatuses, LocalDateTime startDate, Pageable pageable);
    Page<Order> findByUserIdAndStatusContainingIgnoreCaseAndStatusNotIn(Long userId, String status, List<String> excludedStatuses, Pageable pageable);

    Page<Order> findByUserIdAndStatusNotInAndOrderDateAfter(Long userId, List<String> excludedStatuses, LocalDateTime startDate, Pageable pageable);
    Page<Order> findByUserIdAndStatusNotIn(Long userId, List<String> excludedStatuses, Pageable pageable);

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(String status);
    List<Order> findAll();
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    long countByUserId(Integer userId);
}