package com.SWP391.G3PCoffee.service.vouchers;

import com.SWP391.G3PCoffee.repository.VoucherRepository;
import org.springframework.stereotype.Service;

@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }
}
