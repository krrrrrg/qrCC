package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.restqrpayment_2.dto.MemberDTO;
import org.zerock.restqrpayment_2.service.MemberService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.restqrpayment_2.domain.MemberRole;
import org.springframework.ui.Model;

@Controller
@Log4j2
@RequiredArgsConstructor
public class CommonController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "common/index";
    }

    @GetMapping("/login")
    public String loginGET(@RequestParam(value = "error", required = false) String error, 
                           @RequestParam(value = "logout", required = false) String logout, Model model) {
        log.info("login get.....");
        log.info("logout: " + logout);

        if(logout != null) {
            model.addAttribute("msg", "로그아웃 되었습니다.");
        }

        if(error != null) {
            model.addAttribute("error", "로그인 에러입니다. 계정을 확인하세요");
        }

        return "common/login";
    }

    @GetMapping("/find/id")
    public String findId() {
        return "common/find-id";
    }

    @GetMapping("/find/password")
    public String findPassword() {
        return "common/find-password";
    }

    @PostMapping("/login")
    public String loginPost(MemberDTO memberDTO, RedirectAttributes redirectAttributes) {
        log.info("Login attempt for user: " + memberDTO.getUserId());
        
        try {
            // 사용자 정보 조회
            MemberDTO existingMember = memberService.read(memberDTO.getUserId());
            
            // 비밀번호 확인
            if (passwordEncoder.matches(memberDTO.getPassword(), existingMember.getPassword())) {
                // USER 권한 확인
                if (existingMember.getRoles().contains(MemberRole.USER)) {
                    log.info("Login successful for user: " + memberDTO.getUserId());
                    return "redirect:/";
                } else {
                    log.warn("Account is not a user account: " + memberDTO.getUserId());
                    redirectAttributes.addAttribute("error", "unauthorized");
                    return "redirect:/login";
                }
            } else {
                log.warn("Invalid password for user: " + memberDTO.getUserId());
                redirectAttributes.addAttribute("error", "invalid");
                return "redirect:/login";
            }
        } catch (IllegalArgumentException e) {
            log.warn("User not found: " + memberDTO.getUserId());
            redirectAttributes.addAttribute("error", "notfound");
            return "redirect:/login";
        }
    }
}