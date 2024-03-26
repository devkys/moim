package com.example.moim.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 이메일 중복 확인
    public boolean validateDuplicateEmail(Member member) {
        return memberRepository.existsById(member.getEmail());
    }

    // 회원가입
    public Member save(Member member) {
        return memberRepository.save(member);
    }

    // 로그인
    public Optional<Member> login(Member member) {
        return memberRepository.login(member.getEmail(), member.getPw());
    }

    // 채팅방 유저 확인
    public List<Member> invitedUser(Long room_id) {
        return memberRepository.invitedUser(room_id);
    }

    public void update(String refreshToken, String email) {
        memberRepository.update(refreshToken, email);
    }


}
