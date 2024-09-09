package com.example.moim.member;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*
    유저 컨트롤러
    회원가입, 이메일 중복화인, 로그인, 채팅방에 초대된 유저 확인
 */
@Controller
@RequestMapping("api/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    //    code formatting : command option L

    private final MemberService memberService;

    private final LoginDTO loginDTO;

    // 회원가입
    @PostMapping("signup")
    @ResponseBody
    public void signup(@RequestBody Member member) {

        log.info("[LOG INFO] ==== {MemberController : 회원가입 메서드} ========================");
        // 이메일 중복확인
        memberService.validateDuplicateEmail(member);

        // 회원가입 처리
        memberService.save(member);

    }

    // 로그인
    @PostMapping("login")
    @ResponseBody
    public LoginDTO signin(@RequestBody Member member, HttpServletRequest request) {

        Member login_member =  memberService.login(member);

        log.info("[LOG INFO] === { MemberController : 로그인 메서드 } ========================");
        log.info("[LOG INFO] === { LOGIN USER INFO: " + login_member + " } ==========================");

        // 해당 request로 세션 생성된 것이 있는지 확인
//        HttpSession session = request.getSession();

//        System.out.println("로그인 후의 스케쥴 아이디 : " + session.getAttribute("sch_id"));

        // 발급 받은 refresh token db에 저장
//        memberService.update(refresh_token, login_member.getEmail());
//
        // 앞으로 넘어갈 실제 유저 정보 데이터
        loginDTO.setEmail(login_member.getEmail());
        loginDTO.setNickname(login_member.getNickname());
//        loginDTO.setCodeValue(codeValue);
//        loginDTO.setExpires_in(expires_in);
//        loginDTO.setAccess_token(access_token);
        log.info("[LOG INFO] === { LOGIN_DTO INFO: " + loginDTO + " } ==========================");

        return loginDTO;
    }

    // 채팅방에 초대된 유저 체크
    @GetMapping("/invited-user")
    @ResponseBody
    public List<Member> invitedUser(@RequestParam("roomId") Long room_id) {
        return memberService.invitedUser(room_id);
    }
}
