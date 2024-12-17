package org.zerock.restqrpayment_2.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Override
    public String register(MemberDTO memberDTO) {
        log.info("Registering new member: " + memberDTO.getUserId());
        log.info("Original password length: " + memberDTO.getPassword().length());
        
        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        log.info("Encoded password length: " + encodedPassword.length());
        
        Member member = Member.builder()
                .userId(memberDTO.getUserId())
                .password(encodedPassword)
                .roleSet(memberDTO.getRoles())
                .name(memberDTO.getName())
                .phone(memberDTO.getPhone())
                .build();

        memberRepository.save(member);
        log.info("Member saved successfully with roles: " + member.getRoleSet());
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

    @Override
    public void changePassword(String userId, String currentPassword, String newPassword) {
        log.info("Changing password for user: " + userId);
        
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
                
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        
        log.info("Password changed successfully for user: " + userId);
    }

    @Override
    public void deleteAccount(String userId, String password) {
        log.info("Deleting account for user: " + userId);
        
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
                
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("Password is incorrect");
        }
        
        memberRepository.delete(member);
        log.info("Account deleted successfully for user: " + userId);
    }
    
    @Override
    public boolean authenticate(String userId, String password) {
        log.info("Authenticating user: " + userId);
        
        Optional<Member> memberOptional = memberRepository.findById(userId);
        if (memberOptional.isEmpty()) {
            log.warn("User not found: " + userId);
            return false;
        }
        
        Member member = memberOptional.get();
        boolean matches = passwordEncoder.matches(password, member.getPassword());
        
        if (matches) {
            log.info("Authentication successful for user: " + userId);
        } else {
            log.warn("Authentication failed for user: " + userId);
        }
        
        return matches;
    }
}
