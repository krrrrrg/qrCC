package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.dto.PasswordChangeDTO;
import org.zerock.restqrpayment_2.service.UserService;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @RequestBody PasswordChangeDTO passwordChangeDTO,
            Authentication authentication) {
        
        if (authentication == null) {
            return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
        }
        
        String phone = authentication.getName(); // 인증된 사용자의 폰번호
        try {
            userService.changePassword(phone, passwordChangeDTO.getCurrentPassword(), 
                                     passwordChangeDTO.getNewPassword());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/account")
    public ResponseEntity<String> deleteAccount(
            @RequestParam String password,
            Authentication authentication) {
        
        if (authentication == null) {
            return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
        }
        
        String phone = authentication.getName(); // 인증된 사용자의 폰번호
        try {
            userService.deleteAccount(phone, password);
            return ResponseEntity.ok("계정이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
