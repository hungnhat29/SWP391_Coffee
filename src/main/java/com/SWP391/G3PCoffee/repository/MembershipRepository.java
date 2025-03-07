package com.SWP391.G3PCoffee.repository;

import com.SWP391.G3PCoffee.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    @Query(value = "select m from Membership m " +
            "where m.user.id = ?1")
    Membership getMembershipByUserId(Long userId);
}
