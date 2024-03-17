package com.example.moim.member;

import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public boolean validateDuplicateEmail(Member member) {
        return memberRepository.existsById(member.getEmail());
    }

    public Member login(Member member) {

        return memberRepository.login(member.getEmail(), member.getPw());
    }
}
