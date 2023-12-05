package com.loglrs.memberservice.domain.member.service;

import com.loglrs.memberservice.domain.member.entity.Member;
import com.loglrs.memberservice.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;


    @Transactional(readOnly = true)
    public Member findByEmail(String email){
        return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
    }

    @Transactional
    public Member save(Member member) {
        Member saveMember = memberRepository.save(member);
        return saveMember;
    }

    @Transactional(readOnly = true)
    public Member findById(Long id){
        return memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
    }

    @Transactional
    public Optional<Member> checkByName(String name) {
        return memberRepository.findByName(name);
    }

    @Transactional
    public Optional<Member> checkByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

//    @Transactional(readOnly = true)
//    public Optional<Member> getMember(String email){
//        return memberRepository.findByEmail(email);
//    }
}
