package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.restqrpayment_2.domain.MemberRole;
import org.zerock.restqrpayment_2.dto.MemberDTO;
import org.zerock.restqrpayment_2.service.MemberService;

import java.util.HashSet;

@Controller
@Log4j2
@RequiredArgsConstructor
public class UserController {

    private final MemberService memberService;

    // 뷰 반환 메서드들
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
            HashSet<MemberRole> roleSet = new HashSet<>();
            roleSet.add(MemberRole.USER);
            memberDTO.setRoles(roleSet);

            String userId = memberService.register(memberDTO);
            redirectAttributes.addFlashAttribute("msg", "회원가입이 완료되었습니다.");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Error during signup: ", e);
            redirectAttributes.addFlashAttribute("error", "회원가입 중 오류가 발생했습니다.");
            return "redirect:/signup";
        }
    }
}