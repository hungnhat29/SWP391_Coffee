package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.Vouchers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Vouchers, Long> {
    Vouchers findByVoucherCode(String voucherCode);
}
