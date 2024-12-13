package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.restqrpayment_2.domain.Member;
import org.zerock.restqrpayment_2.dto.MemberDTO;
import org.zerock.restqrpayment_2.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public String register(MemberDTO memberDTO) {
        Member member = Member.builder()
                .userId(memberDTO.getUserId())
                .password(passwordEncoder.encode(memberDTO.getPassword())) // 암호화
                .roleSet(memberDTO.getRoles())
                .name(memberDTO.getName())
                .phone(memberDTO.getPhone())
                .build();

        memberRepository.save(member);
        return member.getUserId();
    }

    @Override
    public MemberDTO read(String userId) {
        Optional<Member> memberOptional = memberRepository.findById(userId);
        Member member = memberOptional.orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return MemberDTO.builder()
                .userId(member.getUserId())
                .password(member.getPassword())
                .roles(member.getRoleSet())
                .name(member.getName())
                .phone(member.getPhone())
                .build();
    }

    @Override
    public void modify(MemberDTO memberDTO) {
        Member member = memberRepository.findById(memberDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if (memberDTO.getPassword() != null) {
            member.changePassword(passwordEncoder.encode(memberDTO.getPassword()));
        }

        if (memberDTO.getRoles() != null) {
            member.clearRoles();
            memberDTO.getRoles().forEach(member::addRole);
        }

        memberRepository.save(member);
    }

    @Override
    public void remove(String userId) {
        memberRepository.deleteById(userId);
    }

    @Override
    public List<MemberDTO> getMemberList() {
        // MemberEntity를 MemberDTO로 변환하여 반환
        return memberRepository.findAll().stream()
                .map(Member::toDTO)
                .toList();
    }
}
