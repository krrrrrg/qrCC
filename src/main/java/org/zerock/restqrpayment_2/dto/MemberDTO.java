package org.zerock.restqrpayment_2.dto;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import lombok.*;
import org.zerock.restqrpayment_2.domain.MemberRole;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private String userId; // Member ID
    private String password; // Password
    private String name;
    private String phone;

    @Builder.Default
    private Set<MemberRole> roles = new HashSet<>(); // USER, OWNER, ADMIN
}
