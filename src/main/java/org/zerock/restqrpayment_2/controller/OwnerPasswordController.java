package org.zerock.restqrpayment_2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.restqrpayment_2.dto.PasswordChangeDTO;
import org.zerock.restqrpayment_2.service.MemberService;

@RestController
@RequestMapping("/api/owner")
@Log4j2
@RequiredArgsConstructor
public class OwnerPasswordController {

    private final MemberService memberService;

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PasswordChangeDTO passwordChangeDTO) {
        
        log.info("Password change requested for user: " + userDetails.getUsername());
        
        memberService.changePassword(
            userDetails.getUsername(),
            passwordChangeDTO.getCurrentPassword(),
            passwordChangeDTO.getNewPassword()
        );
        
        return ResponseEntity.ok("Password changed successfully");
    }
}
