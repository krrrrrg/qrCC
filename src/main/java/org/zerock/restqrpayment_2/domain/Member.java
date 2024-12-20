package org.zerock.restqrpayment_2.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.*;
import org.zerock.restqrpayment_2.dto.MemberDTO;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roleSet")
public class Member extends BaseEntity {

    @Id
    private String userId;

    private String password;

    private String name;
    private String phone;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private Set<MemberRole> roleSet = new HashSet<>();

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void addRole(MemberRole role) {
        if (this.roleSet == null) {
            this.roleSet = new HashSet<>();
        }
        this.roleSet.add(role);
        System.out.println("Added role " + role + " to user " + this.userId);
    }

    public void clearRoles() {
        this.roleSet.clear();
    }

    // MemberEntity를 MemberDTO로 변환
    public MemberDTO toDTO() {
        return MemberDTO.builder()
                .userId(this.userId)
                .password(this.password)
                .roles(this.roleSet)
                .name(this.name)
                .phone(this.phone)
                .build();
    }
}
