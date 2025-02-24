package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    @Query(value = "select m from Membership m " +
            "where m.user.id = ?1")
    Membership getMembershipByUserId(Long userId);

    @Query(value = "select distinct m.rank from Membership m")
    List<String> getAllRankOfMembership();

    @Query(value = "select m from Membership m " +
            "where m.rank = ?1")
    List<Membership> getListMemberShipByRank(String rank);

    @Query(value = "select m from Membership m " +
            "where m.user.id In ?1")
    List<Membership> getListMembershipByListUserId(List<Long> listUserId);
}
