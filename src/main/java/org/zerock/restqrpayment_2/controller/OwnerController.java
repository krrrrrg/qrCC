package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.restqrpayment_2.dto.MemberDTO;
import org.zerock.restqrpayment_2.service.MemberService;
import org.zerock.restqrpayment_2.domain.MemberRole;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

import java.util.HashSet;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/owner")
public class OwnerController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String ownerLogin() {
        return "owner/owner-login";
    }

    @GetMapping("/dashboard")
    public String ownerDashboard() {
        return "owner/owner-dashboard";
    }

    @GetMapping("/signup")
    public String ownerSignup() {
        return "owner/owner-signup";
    }

    @PostMapping("/signup")
    public String ownerSignupPost(MemberDTO memberDTO) {
        // 기존 roles를 clear하고 OWNER 권한만 설정
        memberDTO.getRoles().clear();
        memberDTO.getRoles().add(MemberRole.OWNER);
        
        log.info("Owner signup with roles: " + memberDTO.getRoles());
        memberService.register(memberDTO);
        return "redirect:/owner/login";
    }

    @PostMapping("/login")
    public String ownerLoginPost(MemberDTO memberDTO, RedirectAttributes redirectAttributes) {
        log.info("Login attempt for user: " + memberDTO.getUserId());
        
        try {
            // 사용자 정보 조회
            MemberDTO existingMember = memberService.read(memberDTO.getUserId());
            
            // 비밀번호 확인
            if (memberService.authenticate(memberDTO.getUserId(), memberDTO.getPassword())) {
                // 점주 권한 확인
                if (existingMember.getRoles().contains(MemberRole.OWNER)) {
                    log.info("Login successful for owner: " + memberDTO.getUserId());
                    return "redirect:/owner/dashboard";
                } else {
                    log.warn("User " + memberDTO.getUserId() + " does not have owner privileges");
                    redirectAttributes.addAttribute("error", "unauthorized");
                    return "redirect:/owner/login";
                }
            } else {
                log.warn("Invalid password for user: " + memberDTO.getUserId());
                redirectAttributes.addAttribute("error", "invalid");
                return "redirect:/owner/login";
            }
        } catch (IllegalArgumentException e) {
            log.warn("User not found: " + memberDTO.getUserId());
            redirectAttributes.addAttribute("error", "notfound");
            return "redirect:/owner/login";
        }
    }
}