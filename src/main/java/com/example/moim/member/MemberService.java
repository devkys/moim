package com.example.moim.member;

import com.example.moim.config.security.CustomException;
import com.example.moim.config.security.ErrorCode;
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
    public Member login(Member member) {
        // orElseThrow를 사용하여 코드 가독성 향상
        // 해당 Member가 없다면 예외, 있다면 Return
        return memberRepository.login(member.getEmail(), member.getPw())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // 채팅방 유저 확인
    public List<Member> invitedUser(Long room_id) {
        return memberRepository.invitedUser(room_id);
    }

    public void update(String refreshToken, String email) {
        memberRepository.update(refreshToken, email);
    }

}
