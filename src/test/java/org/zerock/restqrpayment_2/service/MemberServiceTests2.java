//package org.zerock.restqrpayment_2.service;
//
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.zerock.restqrpayment_2.domain.MemberRole;
//import org.zerock.restqrpayment_2.dto.MemberDTO;
//import org.zerock.restqrpayment_2.repository.MemberRepository;
//
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//@Log4j2
//public class MemberServiceTests2 {
//
//    @Autowired
//    private MemberService memberService;  // 실제 서비스 빈
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;  // 실제 PasswordEncoder 빈
//
//    @Test
//    public void testInsertUser() {
//        // Given
//        for (int i = 0; i < 100; i++) {
//
//            MemberDTO memberDTO = MemberDTO.builder()
////                    .mid("testUser" + i)
//                    .mpw("password")
//                    .roles(Set.of(MemberRole.USER))
//                    .build();
//
//            // 비밀번호를 실제 인코더로 암호화
//            String encodedPassword = passwordEncoder.encode("password");
//
//            // When
//            String mid = memberService.register(memberDTO);  // 실제 서비스의 register 메서드 호출
//
//            // Then
//            assertNotNull(mid);  // mid가 null이 아님을 확인
//        }
//    }
//
//    @Test
//    public void testInsertOwner() {
//        // Given
//        for (int i = 0; i < 10; i++) {
//
//            MemberDTO memberDTO = MemberDTO.builder()
//                    .mid("testOwner" + i)
//                    .mpw("password")
//                    .roles(Set.of(MemberRole.OWNER))
//                    .build();
//
//            // 비밀번호를 실제 인코더로 암호화
//            String encodedPassword = passwordEncoder.encode("password");
//
//            // When
//            String mid = memberService.register(memberDTO);  // 실제 서비스의 register 메서드 호출
//
//            // Then
//            assertNotNull(mid);  // mid가 null이 아님을 확인
//        }
//    }
//
//    @Test
//    public void testInsertAdmin() {
//        // Given
//        MemberDTO memberDTO = MemberDTO.builder()
//                .mid("testAdmin")
//                .mpw("password")
//                .roles(Set.of(MemberRole.ADMIN))
//                .build();
//
//        // 비밀번호를 실제 인코더로 암호화
//        String encodedPassword = passwordEncoder.encode("password");
//
//        // When
//        String mid = memberService.register(memberDTO);  // 실제 서비스의 register 메서드 호출
//
//        // Then
//        assertNotNull(mid);  // mid가 null이 아님을 확인
//    }
//}
