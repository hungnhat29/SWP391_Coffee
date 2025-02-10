package com.SWP391.G3PCoffee.service.vouchers;

import com.SWP391.G3PCoffee.model.UserVouchers;
import com.SWP391.G3PCoffee.repository.UserVoucherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserVoucherService {
    private final UserVoucherRepository userVoucherRepository;

    public UserVoucherService(UserVoucherRepository userVoucherRepository) {
        this.userVoucherRepository = userVoucherRepository;
    }

    public boolean isClaimedVoucherByUser(Long voucherId, Long userId) {
        return userVoucherRepository.existsByUserIdAndVoucherId(voucherId, userId);
    }
    
    public List<UserVouchers> getListUserVoucherOfUser(Long userId){
        return userVoucherRepository.getUserVoucherByUserId(userId);
    }
}
