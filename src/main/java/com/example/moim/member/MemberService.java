package com.example.moim.member;

import com.example.moim.config.security.CustomException;
import com.example.moim.config.security.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final MemberRepository memberRepository;

    // 이메일 중복 확인
    public void validateDuplicateEmail(Member member) {
        // 이메일이 중복될 시 custom 예외 발생
        if(memberRepository.existsById(member.getEmail())) {
            log.info("=== LOG MESSAGE === [MemberService : 이메일 중복]");
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
        // repository class의 login method return 값은 optional
        return memberRepository.login(member.getEmail(), member.getPw())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // 채팅방 유저 확인 -> 서브 쿼리 존재
    public List<Member> invitedUser(Long room_id) {
        return memberRepository.invitedUser(room_id);
    }

    // 허가된 사용자인 경우 OAuth2.0 토큰이 발급됨
    // 발급된 refresh token을 사용자 db에 저장 후 업데이트
    public void update(String refreshToken, String email) {
        memberRepository.update(refreshToken, email);
    }

    // access token 만료시
    // 토큰 재발급을 위해 사용자의 refreshToken을 가져옴
    public String getRefreshTkn(String email){
        return memberRepository.getFreshToken(email);
    }

}
