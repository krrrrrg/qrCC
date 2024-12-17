package org.zerock.restqrpayment_2.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.restqrpayment_2.domain.Member;
import org.zerock.restqrpayment_2.repository.MemberRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user details for username: " + username);

        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Set<SimpleGrantedAuthority> authorities = member.getRoleSet().stream()
                .map(role -> {
                    String authority = "ROLE_" + role.name();
                    log.info("Granting authority: " + authority);
                    return new SimpleGrantedAuthority(authority);
                })
                .collect(Collectors.toSet());

        log.info("User found with " + authorities.size() + " authorities");
        
        return new User(
                member.getUserId(),
                member.getPassword(),
                authorities
        );
    }
}
