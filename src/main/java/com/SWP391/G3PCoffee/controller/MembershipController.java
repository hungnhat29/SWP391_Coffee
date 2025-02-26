package com.SWP391.G3PCoffee.controller;

import com.SWP391.G3PCoffee.model.MemberShipResponse;
import com.SWP391.G3PCoffee.model.MembershipRequest;
import com.SWP391.G3PCoffee.service.member_ship.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping("/membership")
    public String getMembershipByRank(Model model,
                                      @RequestParam(required = false) String rank) {
        List<String> listRank = membershipService.getAllRank();
        List<MemberShipResponse> listMembership = new ArrayList<>();
        if (rank != null) {
            listMembership = membershipService.getDataMemberShipByRank(rank);
        }
        List<MemberShipResponse> listAllCustomer = membershipService.getAllDataMemberShip();

        model.addAttribute("listMembershipByRank", listMembership);
        model.addAttribute("listAllCustomer", listAllCustomer);
        model.addAttribute("listRank", listRank);
        return "membership-list";
    }

    @PostMapping("/save-data-membership")
    public ResponseEntity<?> saveDataMemberShip(@RequestBody MembershipRequest dataMemberShip) {
        boolean saveSuccess = membershipService.saveDataMembership(dataMemberShip);
        return ResponseEntity.ok(saveSuccess ? "Save success" : "Save membership fail");
    }
}
