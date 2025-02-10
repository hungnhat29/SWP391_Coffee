package com.SWP391.G3PCoffee.service.vouchers;

import com.SWP391.G3PCoffee.repository.UserVoucherRepository;
import org.springframework.stereotype.Service;

@Service
public class UserVoucherService {
    private final UserVoucherRepository userVoucherRepository;

    public UserVoucherService(UserVoucherRepository userVoucherRepository) {
        this.userVoucherRepository = userVoucherRepository;
    }
}
