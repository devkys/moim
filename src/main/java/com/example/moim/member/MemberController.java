package com.example.moim.member;

import com.example.moim.config.security.CustomException;
import com.example.moim.config.security.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("api/users-mgmt")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class MemberController {

    //    code formatting : command option L
    private final MemberService memberService;

    private final LoginDTO loginDTO;
    public static final String login_url = "http://192.168.0.123:5173/login";
    public static final String redirect_url = "http://192.168.0.123:5173/main";
    public static final String client_secret = "BEA593123F504846B43FE11E0E0762720130A845";

    // 회원가입
    @PostMapping("signup")
    @ResponseBody
    public void signup(@RequestBody Member member) {
        memberService.validateDuplicateEmail(member);
        memberService.save(member);
    }

    // 로그인
    @PostMapping("login")
    @ResponseBody
    public LoginDTO signin(@RequestBody Member member, HttpServletRequest request) throws JsonProcessingException {
        Member login_member =  memberService.login(member);

        System.out.println("login_member:" + login_member);
        System.out.println("-------------------------");
        System.out.println("login member nickname" + login_member.getNickname());
        // 해당 request로 세션 생성된 것이 있는지 확인
        HttpSession session = request.getSession();

        System.out.println("로그인 후의 스케쥴 아이디 : " + session.getAttribute("sch_id"));

        // 회원이 존재한다면 DBridge Oauth2.0 API 요청
//         Authorization Code 를 발급 받기 위한 uri ( DBridge oauth2.0 api 요청 uri)
        String auth_url = "https://dev-api.cyber-i.com/svc/moim/auth?USER_ID=firstClient&login_url=" + login_url +
                "&client_id=firstClient&redircet_uri=" + redirect_url + "&response_type=code";

        // WebClient 생성
        WebClient authClient = WebClient
                .builder()
                .build();

        // authorization code 생성
        String response = authClient
                .get()
                .uri(auth_url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Jackson ObjectMapper를 사용하여 JSON 문자열을 파싱
        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(response);

        // "response" 배열의 첫 번째 요소
        JsonNode responseArray = rootNode.get("response").get(0);

        // "code" 값을 가져옴
        String codeValue = responseArray.get("code").asText();

        // "authorization code 값 출력
        System.out.println("Code 값: " + codeValue);

            /*
                authorization code를 이용해 access token 발급 요청
             */

        // access token을 발급 받기 위한 uri (DBridge oauth2.0 api 요청 uri)
        String token_url = "https://dev-api.cyber-i.com/svc/moim/token?code=" + codeValue + "&client_id=firstClient&client_secret=" + client_secret +
                "&grant_type=authorization_code&redirect_uri=" + redirect_url;

        // WebClient 생성
        WebClient tokenClient = WebClient
                .builder()
                .build();

        String token_res = tokenClient
                .get()
                .uri(token_url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode rootNode2 = mapper.readTree(token_res);

        String access_token = rootNode2.get("access_token").asText();
        String refresh_token = rootNode2.get("refresh_token").asText();

        System.out.println("access_token" + access_token);

        // 발급 받은 refresh token db에 저장
        memberService.update(refresh_token, login_member.getEmail());

        // 유저 정보 email, nickname, accessToken dto 반환
        loginDTO.setEmail(login_member.getEmail());
        loginDTO.setNickname(login_member.getNickname());
        loginDTO.setAccess_token(access_token);

        return loginDTO;
    }

    // 채팅방에 초대된 유저 체크
    @GetMapping("/invited-user")
    @ResponseBody
    public List<Member> invitedUser(@RequestParam("roomId") Long room_id) {
        return memberService.invitedUser(room_id);
    }
}
