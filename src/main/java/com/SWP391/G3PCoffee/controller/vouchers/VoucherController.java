package com.SWP391.G3PCoffee.controller.vouchers;

import com.SWP391.G3PCoffee.model.Vouchers;
import com.SWP391.G3PCoffee.service.vouchers.UserVoucherService;
import com.SWP391.G3PCoffee.service.vouchers.VoucherService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class VoucherController {

    private final VoucherService voucherService;
    private final UserVoucherService userVoucherService;

    public VoucherController(VoucherService voucherService, UserVoucherService userVoucherService) {
        this.voucherService = voucherService;
        this.userVoucherService = userVoucherService;
    }

    @GetMapping("/my_voucher")
    public String getVoucherOfUser(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        String email = userDetails.getUsername();
        List<Vouchers> listVoucherOfUser = voucherService.getListVoucherByEmail(email);
        model.addAttribute("listVoucher", listVoucherOfUser);
        return "my_voucher";
    }

    @GetMapping("/voucher")
    public String getAllVouchers(@AuthenticationPrincipal UserDetails userDetails, Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size) {
        Page<Vouchers> vouchersPage = voucherService.getPagedVouchers(page, size);
        model.addAttribute("vouchersPage", vouchersPage);
        return "voucher";
    }
}
