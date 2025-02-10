package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.Vouchers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Vouchers, Long> {
    Vouchers findByVoucherCode(String voucherCode);

    @Query(value = "SELECT v from Vouchers v " +
            "join UserVouchers uv on v.id = uv.voucher.id " +
            "where uv.user.id = ?1")
    List<Vouchers> getListVoucherByUserId(Long userId);
}
