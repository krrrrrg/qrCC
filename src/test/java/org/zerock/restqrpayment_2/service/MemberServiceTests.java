//package org.zerock.restqrpayment_2.service;
//
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.zerock.restqrpayment_2.domain.Member;
//import org.zerock.restqrpayment_2.domain.MemberRole;
//import org.zerock.restqrpayment_2.dto.MemberDTO;
//import org.zerock.restqrpayment_2.repository.MemberRepository;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@Log4j2
//public class MemberServiceTests {
//
//    @InjectMocks
//    private MemberServiceImpl memberService;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testRegister() {
//        // Given
//        MemberDTO memberDTO = MemberDTO.builder()
//                .mid("testUser")
//                .mpw("password")
//                .roles(Set.of(MemberRole.USER))
//                .build();
//
//
//
//        when(passwordEncoder.encode("password")).thenReturn("encryptedPassword");
//        when(memberRepository.save(any(Member.class))).thenReturn(Member.builder().mid("testUser").build());
//
//        // When
//        String mid = memberService.register(memberDTO);
//
//        // Then
//        assertThat(mid).isEqualTo("testUser");
//        verify(memberRepository, times(1)).save(any(Member.class));
//        verify(passwordEncoder, times(1)).encode("password");
//    }
//
//    @Test
//    void testRead() {
//        // Given
//        Member member = Member.builder()
//                .mid("testUser")
//                .roleSet(Set.of(MemberRole.USER, MemberRole.ADMIN))
//                .build();
//
//        when(memberRepository.findById("testUser")).thenReturn(Optional.of(member));
//
//        // When
//        MemberDTO memberDTO = memberService.read("testUser");
//
//        // Then
//        assertThat(memberDTO.getMid()).isEqualTo("testUser");
//        assertThat(memberDTO.getRoles()).containsExactlyInAnyOrder(MemberRole.USER, MemberRole.ADMIN);
//        verify(memberRepository, times(1)).findById("testUser");
//    }
//
//    @Test
//    void testModify() {
//        // Given
//        Member member = Member.builder()
//                .mid("testUser")
//                .mpw("oldPassword")
//                .roleSet(new HashSet<>(Set.of(MemberRole.USER))) // 역할 초기화
//                .build();
//
//        MemberDTO memberDTO = MemberDTO.builder()
//                .mid("testUser")
//                .mpw("newPassword")
//                .roles(Set.of(MemberRole.ADMIN)) // 변경된 역할
//                .build();
//
//        when(memberRepository.findById("testUser")).thenReturn(Optional.of(member));
//        when(passwordEncoder.encode("newPassword")).thenReturn("encryptedPassword");
//
//        // When
//        memberService.modify(memberDTO);
//
//        // Then
//        assertThat(member.getMpw()).isEqualTo("encryptedPassword");
//        assertThat(member.getRoleSet()).containsExactly(MemberRole.ADMIN); // 역할 검증
//        verify(memberRepository, times(1)).findById("testUser");
//        verify(memberRepository, times(1)).save(member);
//    }
//
//    @Test
//    void testRemove() {
//        // Given
//        String mid = "testUser";
//
//        // When
//        memberService.remove(mid);
//
//        // Then
//        verify(memberRepository, times(1)).deleteById(mid);
//    }
//
//    @Test
//    public void testGetMemberList() {
//        // Given: 이미 setup에서 데이터가 삽입됨
//
//        // When
//        List<MemberDTO> members = memberService.getMemberList();
//
//        // 각 회원 정보를 출력
//        members.forEach(member -> {
//            log.info("Member: {}", member);
//        });
//    }
//}
