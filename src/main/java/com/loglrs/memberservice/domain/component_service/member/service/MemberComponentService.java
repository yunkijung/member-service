package com.loglrs.memberservice.domain.component_service.member.service;


import com.loglrs.memberservice.domain.member.entity.Member;
import com.loglrs.memberservice.domain.member.service.MemberService;
import com.loglrs.memberservice.domain.member.service.MemberServiceImpl;
import com.loglrs.memberservice.domain.role.entity.Role;
import com.loglrs.memberservice.domain.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberComponentService {

    private final MemberService memberService;

    private final RoleService roleService;


    @Transactional
    public Member signUp(Member member) {

        Role role = roleService.findByName("ROLE_USER");
        member.addRole(role);

        return memberService.save(member);

    }

}
