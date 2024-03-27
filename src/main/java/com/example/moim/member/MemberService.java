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
    public void validateDuplicateEmail(Member member) {
        if(memberRepository.existsById(member.getEmail())) {
           throw new CustomException(ErrorCode.HAS_EMAIL);
        }
    }

    // 회원가입
    // 회원 가입 전 이메일 중복확인 및 패스워드 암호화
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

    // 허가된 사용자인 경우 토큰이 발급됨
    // 발급된 토근을 사용자 db에 저장 후 업데이트
    public void update(String refreshToken, String email) {
        memberRepository.update(refreshToken, email);
    }

}
