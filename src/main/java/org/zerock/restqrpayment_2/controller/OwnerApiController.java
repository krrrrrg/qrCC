package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.dto.MemberDTO;
import org.zerock.restqrpayment_2.dto.PasswordChangeDTO;
import org.zerock.restqrpayment_2.service.MemberService;
import org.zerock.restqrpayment_2.domain.MemberRole;

import java.util.Map;

@RestController
@RequestMapping("/api/owner")
@Log4j2
@RequiredArgsConstructor
public class OwnerApiController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody MemberDTO memberDTO) {
        try {
            // OWNER 권한 설정
            memberDTO.getRoles().clear();
            memberDTO.getRoles().add(MemberRole.OWNER);
            
            log.info("Owner signup request: " + memberDTO.getUserId());
            memberService.register(memberDTO);
            
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "회원가입이 완료되었습니다."
            ));
        } catch (Exception e) {
            log.error("Owner signup failed: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "회원가입에 실패했습니다: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PasswordChangeDTO passwordChangeDTO) {
        
        try {
            log.info("Password change requested for user: " + userDetails.getUsername());
            
            memberService.changePassword(
                userDetails.getUsername(),
                passwordChangeDTO.getCurrentPassword(),
                passwordChangeDTO.getNewPassword()
            );
            
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "비밀번호가 성공적으로 변경되었습니다."
            ));
        } catch (Exception e) {
            log.error("Password change failed for user: " + userDetails.getUsername(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "비밀번호 변경에 실패했습니다: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        
        try {
            String password = request.get("currentPassword");
            // 비밀번호 확인
            memberService.verifyPassword(userDetails.getUsername(), password);
            // 계정 삭제
            memberService.deleteAccount(userDetails.getUsername());
            SecurityContextHolder.clearContext(); // 로그아웃 처리
            
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "계정이 성공적으로 삭제되었습니다."
            ));
        } catch (Exception e) {
            log.error("Account deletion failed for user: " + userDetails.getUsername(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "계정 삭제에 실패했습니다: " + e.getMessage()
            ));
        }
    }
}
