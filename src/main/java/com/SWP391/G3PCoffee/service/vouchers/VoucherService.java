package com.SWP391.G3PCoffee.service.vouchers;

import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.model.Vouchers;
import com.SWP391.G3PCoffee.repository.VoucherRepository;
import com.SWP391.G3PCoffee.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final UserService userService;

    public VoucherService(VoucherRepository voucherRepository, UserService userService) {
        this.voucherRepository = voucherRepository;
        this.userService = userService;
    }

    public List<Vouchers> getListVoucherByEmail(String email) {
        List<Vouchers> listVoucherOfUser = new ArrayList<>();
        User user = userService.getCustomerByEmail(email);
        if (user == null) return listVoucherOfUser;
        listVoucherOfUser = voucherRepository.getListVoucherByUserId(user.getId());
        return listVoucherOfUser;
    }
}
