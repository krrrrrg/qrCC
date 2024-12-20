package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.restqrpayment_2.dto.MemberDTO;
import org.zerock.restqrpayment_2.service.MemberService;
import org.zerock.restqrpayment_2.domain.MemberRole;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/find/id")
    public String findOwnerId() {
        return "owner/find-id";
    }

    @GetMapping("/find/password")
    public String findOwnerPassword() {
        return "owner/find-password";
    }

    @PostMapping("/find/id")
    public String findOwnerIdPost(@RequestParam String name, 
                                @RequestParam String phone, 
                                Model model) {
        try {
            List<String> foundIds = memberService.findIdsByNameAndPhone(name, phone);
            // 점주 회원만 필터링
            List<String> ownerIds = foundIds.stream()
                .filter(memberService::isOwner)
                .collect(Collectors.toList());
            
            if (ownerIds.isEmpty()) {
                model.addAttribute("error", "일치하는 점주 회원 정보를 찾을 수 없습니다.");
            } else {
                model.addAttribute("foundIds", ownerIds);
            }
            return "owner/find-id";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "owner/find-id";
        }
    }

    @PostMapping("/find/password")
    public String findOwnerPasswordPost(@RequestParam String userId,
                                      @RequestParam String phone,
                                      Model model) {
        try {
            memberService.resetPasswordAndSendToPhone(userId, phone);
            model.addAttribute("success", "임시 비밀번호가 전송되었습니다.");
            return "owner/find-password";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "일치하는 점주 정보를 찾을 수 없습니다.");
            return "owner/find-password";
        }
    }
}