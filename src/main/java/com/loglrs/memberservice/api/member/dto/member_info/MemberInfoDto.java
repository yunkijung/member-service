package com.loglrs.memberservice.api.member.dto.member_info;


import com.loglrs.memberservice.domain.member.entity.Member;
import com.loglrs.memberservice.domain.role.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class MemberInfoDto {
    private String email;
    private String name;
    private Long memberId;
    private List<String> roles = new ArrayList<>();

    public MemberInfoDto(Member member) {
        this.email = member.getEmail();
        this.name = member.getName();
        this.memberId = member.getId();

        Set<Role> roles = member.getRoles();
        for (Role role : roles) {
            addRole(role.getName());
        }
    }

    public void addRole(String role){
        roles.add(role);
    }
}

