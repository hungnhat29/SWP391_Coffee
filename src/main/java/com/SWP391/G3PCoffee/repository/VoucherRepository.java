package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.Vouchers;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Vouchers, Long> {
    Vouchers findByVoucherCode(String voucherCode);

    @Query(value = "SELECT v from Vouchers v " +
            "join UserVouchers uv on v.id = uv.voucher.id " +
            "where uv.user.id = ?1")
    List<Vouchers> getListVoucherByUserId(Long userId);

    @Query("SELECT v FROM Vouchers v WHERE v.expiryDate > :currentDate")
    Page<Vouchers> getPageVoucher(@Param("currentDate") LocalDate currentDate, Pageable pageable);
}
