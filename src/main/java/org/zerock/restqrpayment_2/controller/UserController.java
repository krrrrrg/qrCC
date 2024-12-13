package org.zerock.restqrpayment_2.controller;

import java.util.HashSet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.restqrpayment_2.domain.MemberRole;
import org.zerock.restqrpayment_2.dto.MemberDTO;
import org.zerock.restqrpayment_2.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequiredArgsConstructor
public class UserController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signup() {
        return "user/signup";
    }

    @GetMapping("/cart")
    public String cart() {
        return "user/cart";
    }

    @GetMapping("/menu/detail")
    public String menuDetail() {
        return "user/menu-detail";
    }

    @GetMapping("/order/history")
    public String orderHistory() {
        return "user/order-history";
    }

    @GetMapping("/order/status")
    public String orderStatus() {
        return "user/order-status";
    }

 @PostMapping("/signup")
    public String signupPost(MemberDTO memberDTO, RedirectAttributes redirectAttributes) {
     log.info("User signup attempt: " + memberDTO);

     try {
         // 사용자 역할 설정
         HashSet<MemberRole> roles = new HashSet<>();
         roles.add(MemberRole.USER);
         memberDTO.setRoles(roles);

         // 회원 등록
         memberService.register(memberDTO);

         return "redirect:/login";
     } catch (Exception e) {
         log.error("Error during user registration: ", e);
         redirectAttributes.addAttribute("error", "signup");
         return "redirect:/signup";
     }
 }

}