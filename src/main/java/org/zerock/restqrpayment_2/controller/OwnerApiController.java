package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.service.MemberService;

import java.util.Map;

@RestController
@RequestMapping("/api/owner")
@Log4j2
@RequiredArgsConstructor
public class OwnerApiController {

    private final MemberService memberService;

    @DeleteMapping("/account")
    public ResponseEntity<String> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        
        try {
            String password = request.get("password");
            memberService.deleteAccount(userDetails.getUsername(), password);
            SecurityContextHolder.clearContext(); // 로그아웃 처리
            return ResponseEntity.ok("계정이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
