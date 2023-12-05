package com.loglrs.memberservice.domain.member.service;

import com.loglrs.memberservice.domain.member.entity.Member;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberService {
    public Member findByEmail(String email);
    public Member save(Member member);

    public Member findById(Long id);

    public Optional<Member> checkByName(String name);

    public Optional<Member> checkByEmail(String email);

}
