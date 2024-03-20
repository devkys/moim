package com.example.moim.member;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public boolean validateDuplicateEmail(Member member) {
        return memberRepository.existsById(member.getEmail());
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public Member login(Member member) {
        return memberRepository.login(member.getEmail(), member.getPw());
    }

}
