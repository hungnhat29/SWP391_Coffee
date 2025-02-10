package com.SWP391.G3PCoffee.controller.vouchers;

import com.SWP391.G3PCoffee.model.Vouchers;
import com.SWP391.G3PCoffee.service.vouchers.UserVoucherService;
import com.SWP391.G3PCoffee.service.vouchers.VoucherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class VoucherController {

    private final VoucherService voucherService;
    private final UserVoucherService userVoucherService;

    public VoucherController(VoucherService voucherService, UserVoucherService userVoucherService) {
        this.voucherService = voucherService;
        this.userVoucherService = userVoucherService;
    }

    @GetMapping("/my-voucher")
    public String getVoucherOfUser(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam(defaultValue = "0") int page,
                                   Model model) {
        if (userDetails == null) return "redirect:/auth/login";

        String email = userDetails.getUsername();
        Pageable pageable = PageRequest.of(page, 8);
        Page<Vouchers> pageVoucher = voucherService.getListVoucherByEmail(email, pageable);

        model.addAttribute("listVoucher", pageVoucher.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageVoucher.getTotalPages());

        return "my_voucher";
    }

    @GetMapping("/voucher")
    public String getAllVouchers(@AuthenticationPrincipal UserDetails userDetails, Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size) {
        if (userDetails == null) return "redirect:/auth/login";
        String email = userDetails.getUsername();
        List<Long> listVoucherIdOfUser = voucherService.getListVoucherOfUser(email);
        Page<Vouchers> vouchersPage = voucherService.getPagedVouchers(page, size);
        model.addAttribute("vouchersPage", vouchersPage);
        model.addAttribute("listIdUse", listVoucherIdOfUser);
        return "voucher";
    }

    @PostMapping("/claim-voucher")
    public ResponseEntity<?> claimVoucher(@RequestBody Map<String, Long> request,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        Long voucherId = request.get("voucherId");
        if (userDetails == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Voucher claim failed!"));
        String email = userDetails.getUsername();
        boolean success = voucherService.claimVoucher(voucherId, email);

        if (success) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Voucher claimed successfully!"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Voucher claim failed!"));
        }
    }

}
