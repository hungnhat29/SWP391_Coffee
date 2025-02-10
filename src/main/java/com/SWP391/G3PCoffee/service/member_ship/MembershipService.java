package com.SWP391.G3PCoffee.service.member_ship;

import com.SWP391.G3PCoffee.constant.MembershipRank;
import com.SWP391.G3PCoffee.model.Membership;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.repository.MembershipRepository;
import com.SWP391.G3PCoffee.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final UserService userService;

    public MembershipService(MembershipRepository membershipRepository, UserService userService) {
        this.membershipRepository = membershipRepository;
        this.userService = userService;
    }

    public Membership getMemberShipByEmail(String email) {
        User user = userService.getCustomerByEmail(email);
        if (user == null) return null;

        return membershipRepository.getMembershipByUserId(user.getId());
    }

    public void initMemberShip(User user) {
        LocalDate currentDate = LocalDate.now();
        LocalDate expiryDate = currentDate.plusMonths(6);

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setStartDate(currentDate);
        membership.setExpiryDate(expiryDate);
        membership.setRank(MembershipRank.SILVER.name());
        membership.setPoints(0);

        membershipRepository.save(membership);
    }

}
