package com.SWP391.G3PCoffee.service.member_ship;

import com.SWP391.G3PCoffee.model.MemberShipResponse;
import com.SWP391.G3PCoffee.model.Membership;
import com.SWP391.G3PCoffee.model.MembershipRequest;
import com.SWP391.G3PCoffee.model.User;
import com.SWP391.G3PCoffee.repository.MembershipRepository;
import com.SWP391.G3PCoffee.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(MembershipService.class);

    public MembershipService(MembershipRepository membershipRepository, UserService userService) {
        this.membershipRepository = membershipRepository;
        this.userService = userService;
    }

    public Membership getMemberShipByUserId(Long userId) {
        return membershipRepository.getMembershipByUserId(userId);
    }

    public void initMemberShip(User user) {
        LocalDate currentDate = LocalDate.now();
        LocalDate expiryDate = currentDate.plusMonths(6);

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setStartDate(currentDate);
        membership.setExpiryDate(expiryDate);
        membership.setPoints(0);

        membershipRepository.save(membership);
    }

    public List<String> getAllRank() {
        return membershipRepository.getAllRankOfMembership();
    }

    public List<MemberShipResponse> getDataMemberShipByRank(String rank) {
        List<User> listCustomer = userService.getAllCustomers();
        if (listCustomer.isEmpty()) {
            return null;
        }
        List<Membership> listMemberShip = membershipRepository.getListMemberShipByRank(rank);

        return convertDataMemberShip(listCustomer, listMemberShip, rank)
                .stream()
                .filter(memberShip -> memberShip.getRank() != null && !memberShip.getRank().isEmpty()) // Loại bỏ bản ghi không có rank
                .collect(Collectors.toList());
    }

    public List<MemberShipResponse> getAllDataMemberShip() {
        List<User> listCustomer = userService.getAllCustomers();
        if (listCustomer.isEmpty()) {
            return null;
        }
        List<Membership> listMemberShip = membershipRepository.findAll();

        return convertDataMemberShip(listCustomer, listMemberShip, null);
    }

    private List<MemberShipResponse> convertDataMemberShip(List<User> listCustomer, List<Membership> listMemberShip, String rank) {
        Map<Long, Membership> mapMemberShipByUserId = listMemberShip.stream()
                .collect(Collectors.toMap(membership -> membership.getUser().getId(), membership -> membership));

        List<MemberShipResponse> listAllMembershipResponse = new ArrayList<>();
        List<MemberShipResponse> listMembershipByRankResponse = new ArrayList<>();
        listCustomer.forEach(item -> {
            Membership membership = mapMemberShipByUserId.get(item.getId());

            MemberShipResponse memberShipResponse = MemberShipResponse.builder()
                    .id(membership != null ? membership.getId() : 0)
                    .userId(item.getId())
                    .name(item.getName())
                    .phone(item.getPhone())
                    .email(item.getEmail())
                    .point(membership != null ? membership.getPoints() : 0)
                    .rank(membership != null ? membership.getRank() : "")
                    .build();
            if (membership != null && rank != null) {
                listMembershipByRankResponse.add(memberShipResponse);
            }
            listAllMembershipResponse.add(memberShipResponse);
        });
        return rank != null ? listMembershipByRankResponse : listAllMembershipResponse;
    }

    @Transactional
    public boolean saveDataMembership(MembershipRequest dataMembership) {
        String rank = dataMembership.getRank();
        List<Long> listUserId = dataMembership.getListUserId();

        if (rank == null || listUserId.isEmpty()) {
            return false;
        }

        List<String> listRankInDb = membershipRepository.getAllRankOfMembership();
        boolean isNewRank = !listRankInDb.contains(rank);
        List<User> listCustomer = userService.getAllCustomerByListUserId(listUserId);
        List<Membership> listMembershipInDb = membershipRepository.getListMembershipByListUserId(listUserId);
        Map<Long, Membership> mapMembershipByUserId = listMembershipInDb.stream()
                .collect(Collectors.toMap(user -> user.getUser().getId(), membership -> membership));

        List<Membership> listMembershipNeedAdd = new ArrayList<>();
        listCustomer.forEach(user -> {
            Membership membership = mapMembershipByUserId.getOrDefault(user.getId(), null);
            if (membership == null) {
                membership = new Membership();
            }
            membership.setMembershipType("");
            membership.setRank(rank);
            membership.setStatus("active");
            membership.setPoints(membership.getPoints() != null ? membership.getPoints() : 0);
            membership.setUser(user);
            membership.setStartDate(LocalDate.now());
            membership.setExpiryDate(LocalDate.now().plusMonths(6));

            listMembershipNeedAdd.add(membership);
        });

        membershipRepository.saveAll(listMembershipNeedAdd);
        return true;
    }

}
