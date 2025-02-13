package com.SWP391.G3PCoffee.service.vouchers;

import com.SWP391.G3PCoffee.model.Membership;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.model.UserVouchers;
import com.SWP391.G3PCoffee.model.Vouchers;
import com.SWP391.G3PCoffee.repository.MembershipRepository;
import com.SWP391.G3PCoffee.repository.UserVoucherRepository;
import com.SWP391.G3PCoffee.repository.VoucherRepository;
import com.SWP391.G3PCoffee.service.UserService;
import com.SWP391.G3PCoffee.service.member_ship.MembershipService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final UserService userService;
    private final UserVoucherService userVoucherService;
    private final UserVoucherRepository userVoucherRepository;
    private final MembershipService membershipService;
    private final MembershipRepository membershipRepository;

    public VoucherService(VoucherRepository voucherRepository, UserService userService, UserVoucherService userVoucherService, UserVoucherRepository userVoucherRepository, MembershipService membershipService, MembershipRepository membershipRepository) {
        this.voucherRepository = voucherRepository;
        this.userService = userService;
        this.userVoucherService = userVoucherService;
        this.userVoucherRepository = userVoucherRepository;
        this.membershipService = membershipService;
        this.membershipRepository = membershipRepository;
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
    public Map<String, String> claimVoucher(Long voucherId, String email) {
        Map<String, String> response = new HashMap<>();
        User user = userService.getCustomerByEmail(email);
        Optional<Vouchers> vouchers = voucherRepository.findById(voucherId);
        if (vouchers.isEmpty() || user == null) {
            response.put("message", "Nhận voucher thất bại!");
            response.put("messageType", "error");
            return response;
        }

        Long userId = Objects.requireNonNull(user).getId();
        boolean isClaimedVoucherByUser = userVoucherService.isClaimedVoucherByUser(voucherId, userId);
        if (isClaimedVoucherByUser) {
            response.put("message", "Bạn đã nhận voucher, không thể nhận lại!");
            response.put("messageType", "error");
            return response;
        }

        Vouchers voucherUse = vouchers.get();
        Membership membership = membershipService.getMemberShipByUserId(userId);
        int userPoint = membership.getPoints();
        int pointNeedClaim = voucherUse.getMinPoints();
        if (userPoint < pointNeedClaim) {
            response.put("message", "Bạn không đủ point để nhận voucher!");
            response.put("messageType", "error");
            return response;
        }

        UserVouchers userVouchers = new UserVouchers();
        userVouchers.setVoucher(vouchers.get());
        userVouchers.setUser(user);
        userVoucherRepository.save(userVouchers);

        int oldQuantity = voucherUse.getQuantity();
        voucherUse.setQuantity(oldQuantity - 1);
        voucherRepository.save(voucherUse);

        int newPointUser = userPoint - pointNeedClaim;
        membership.setPoints(newPointUser);
        membershipRepository.save(membership);

        response.put("message", "Nhận voucher thành công!");
        response.put("messageType", "success");
        response.put("quantity", voucherUse.getQuantity().toString());
        return response;
    }
}
