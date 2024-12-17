package org.zerock.restqrpayment_2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.restqrpayment_2.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByPhone(String phone);
}