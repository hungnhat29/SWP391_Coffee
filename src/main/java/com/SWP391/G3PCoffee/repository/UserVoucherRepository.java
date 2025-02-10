package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.UserVouchers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVouchers, Long> {
    boolean existsByUserIdAndVoucherId(Long userId, Long voucherId);
}
