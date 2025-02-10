package com.SWP391.G3PCoffee.controller.vouchers;

import com.SWP391.G3PCoffee.model.Vouchers;
import com.SWP391.G3PCoffee.service.vouchers.UserVoucherService;
import com.SWP391.G3PCoffee.service.vouchers.VoucherService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class VoucherController {

    private final VoucherService voucherService;
    private final UserVoucherService userVoucherService;

    public VoucherController(VoucherService voucherService, UserVoucherService userVoucherService) {
        this.voucherService = voucherService;
        this.userVoucherService = userVoucherService;
    }

    @GetMapping("/voucher")
    public String getVoucherOfUser(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/auth/login";
        String email = userDetails.getUsername();
        List<Vouchers> listVoucherOfUser = voucherService.getListVoucherByEmail(email);
        model.addAttribute("listVoucher", listVoucherOfUser);
        return "my_voucher";
    }
}
