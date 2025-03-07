package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.UserVouchers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVouchers, Long> {
    boolean existsByUserIdAndVoucherId(Long userId, Long voucherId);

    @Query(value = "select uv from UserVouchers uv " +
            "where uv.user.id = ?1")
    List<UserVouchers> getUserVoucherByUserId(Long userId);
}
