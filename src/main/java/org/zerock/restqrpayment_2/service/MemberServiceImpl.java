package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.restqrpayment_2.domain.Member;
import org.zerock.restqrpayment_2.domain.MemberRole;
import org.zerock.restqrpayment_2.dto.MemberDTO;
import org.zerock.restqrpayment_2.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String register(MemberDTO memberDTO) {
        Member member = dtoToEntity(memberDTO);
        memberRepository.save(member);
        return member.getUserId();
    }

    @Override
    public MemberDTO read(String userId) {
        Optional<Member> result = memberRepository.findById(userId);
        Member member = result.orElseThrow(() -> 
            new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        return entityToDTO(member);
    }

    @Override
    public void modify(MemberDTO memberDTO) {
        Optional<Member> result = memberRepository.findById(memberDTO.getUserId());
        Member member = result.orElseThrow(() -> 
            new IllegalArgumentException("사용자를 찾을 수 없습니다: " + memberDTO.getUserId()));
        
        member.changePassword(passwordEncoder.encode(memberDTO.getPassword()));
        member.setName(memberDTO.getName());
        member.setPhone(memberDTO.getPhone());
        
        memberRepository.save(member);
    }

    @Override
    public void remove(String userId) {
        memberRepository.deleteById(userId);
    }

    @Override
    public List<MemberDTO> getMemberList() {
        return memberRepository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void changePassword(String userId, String currentPassword, String newPassword) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        
        member.changePassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    @Override
    public void verifyPassword(String userId, String password) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Override
    public void deleteAccount(String userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        // 연관된 데이터 삭제 로직 추가
        memberRepository.delete(member);
    }

    @Override
    public boolean authenticate(String userId, String password) {
        Optional<Member> result = memberRepository.findById(userId);
        if (result.isEmpty()) {
            return false;
        }
        Member member = result.get();
        return passwordEncoder.matches(password, member.getPassword());
    }

    private Member dtoToEntity(MemberDTO memberDTO) {
        Member member = Member.builder()
                .userId(memberDTO.getUserId())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .name(memberDTO.getName())
                .phone(memberDTO.getPhone())
                .build();
        
        // roles가 비어있으면 USER 권한 부여, 아니면 DTO의 roles 사용
        if (memberDTO.getRoles() == null || memberDTO.getRoles().isEmpty()) {
            member.addRole(MemberRole.USER);
            log.info("Adding default USER role");
        } else {
            memberDTO.getRoles().forEach(role -> {
                member.addRole(role);
                log.info("Adding role: " + role);
            });
        }
        
        return member;
    }

    private MemberDTO entityToDTO(Member member) {
        return MemberDTO.builder()
                .userId(member.getUserId())
                .name(member.getName())
                .phone(member.getPhone())
                .build();
    }
}
