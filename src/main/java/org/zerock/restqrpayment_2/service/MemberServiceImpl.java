package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.restqrpayment_2.domain.Member;
import org.zerock.restqrpayment_2.domain.MemberRole;
import org.zerock.restqrpayment_2.dto.MemberDTO;
import org.zerock.restqrpayment_2.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public String register(MemberDTO memberDTO) {
        Member member = Member.builder()
                .mid(memberDTO.getMid())
                .mpw(passwordEncoder.encode(memberDTO.getMpw())) // 암호화
                .roleSet(memberDTO.getRoles())
                .build();

        memberRepository.save(member);
        return member.getMid();
    }

    @Override
    public MemberDTO read(String mid) {
        Optional<Member> memberOptional = memberRepository.findById(mid);
        Member member = memberOptional.orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return MemberDTO.builder()
                .mid(member.getMid())
                .mpw(member.getMpw())
                .roles(member.getRoleSet())
                .build();
    }

    @Override
    public void modify(MemberDTO memberDTO) {
        Member member = memberRepository.findById(memberDTO.getMid())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (memberDTO.getMpw() != null) {
            member.changePassword(passwordEncoder.encode(memberDTO.getMpw()));
        }

        if (memberDTO.getRoles() != null) {
            member.clearRoles();
            memberDTO.getRoles().forEach(member::addRole);
        }

        memberRepository.save(member);
    }

    @Override
    public void remove(String mid) {
        memberRepository.deleteById(mid);
    }

    @Override
    public List<MemberDTO> getMemberList() {
        // MemberEntity를 MemberDTO로 변환하여 반환
        return memberRepository.findAll().stream()
                .map(Member::toDTO)
                .toList();
    }
}
