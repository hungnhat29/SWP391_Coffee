package com.SWP391.G3PCoffee.service.vouchers;

import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.model.UserVouchers;
import com.SWP391.G3PCoffee.model.Vouchers;
import com.SWP391.G3PCoffee.repository.UserVoucherRepository;
import com.SWP391.G3PCoffee.repository.VoucherRepository;
import com.SWP391.G3PCoffee.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final UserService userService;
    private final UserVoucherService userVoucherService;
    private final UserVoucherRepository userVoucherRepository;

    public VoucherService(VoucherRepository voucherRepository, UserService userService, UserVoucherService userVoucherService, UserVoucherRepository userVoucherRepository) {
        this.voucherRepository = voucherRepository;
        this.userService = userService;
        this.userVoucherService = userVoucherService;
        this.userVoucherRepository = userVoucherRepository;
    }

    public Page<Vouchers> getListVoucherByEmail(String email, Pageable pageable) {
        User user = userService.getCustomerByEmail(email);
        if (user == null) {
            return Page.empty();
        }
        return voucherRepository.getListVoucherByUserId(user.getId(), pageable);
    }

    public Page<Vouchers> getPagedVouchers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return voucherRepository.getPageVoucher(LocalDate.now(), pageable);
    }

    public List<Long> getListVoucherOfUser(String email) {
        User user = userService.getCustomerByEmail(email);
        if (user == null) return null;

        Long userId = user.getId();
        List<UserVouchers> listUserVoucher = userVoucherService.getListUserVoucherOfUser(userId);
        if (listUserVoucher == null) return Collections.emptyList();
        return listUserVoucher.stream().map(userVouchers -> userVouchers.getVoucher().getId())
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean claimVoucher(Long voucherId, String email) {
        User user = userService.getCustomerByEmail(email);
        if (user == null) return false;
        Long userId = user.getId();

        Optional<Vouchers> vouchers = voucherRepository.findById(voucherId);
        if (vouchers.isEmpty()) return false;

        boolean isClaimedVoucherByUser = userVoucherService.isClaimedVoucherByUser(voucherId, userId);
        if (isClaimedVoucherByUser) return false;

        UserVouchers userVouchers = new UserVouchers();
        userVouchers.setVoucher(vouchers.get());
        userVouchers.setUser(user);
        userVoucherRepository.save(userVouchers);

        Vouchers voucherUse = vouchers.get();
        int oldQuantity = voucherUse.getQuantity();
        voucherUse.setQuantity(oldQuantity - 1);
        voucherRepository.save(voucherUse);

        return true;
    }
}
