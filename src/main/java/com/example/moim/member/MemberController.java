package com.example.moim.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
@RequestMapping("api/users-mgmt")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MemberController {

//    code formatting : command option L
    private final MemberService memberService;
    public static final String login_url = "http://192.168.0.11:5173/login";
    public static final String redirect_url = "http://192.168.0.11:5173/main";
    public static final String client_secret = "B1D861B2F08744EE88956D6BAC24ECDB14B761E7";

    String access_token;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("signup")
    @ResponseBody
    public Member signup(@RequestBody Member member) {
        return member;
    }

    @PostMapping("signin")
    @ResponseBody
    public Member signin(@RequestBody Member member) throws JsonProcessingException {
        Member login_member = memberService.login(member);

        WebClient webClient = WebClient.builder().build();
        String auth_url = "https://dev-api.cyber-i.com/svc/moim/auth?USER_ID=testClient2&login_url=" + login_url +
                "&client_id=testClient2&redircet_uri=" + redirect_url + "&response_type=code";
        // auth code 받기
        String response = webClient.get().uri(auth_url).retrieve().bodyToMono(String.class).block();

        // Jackson ObjectMapper를 사용하여 JSON 문자열을 파싱합니다.
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);

        // "response" 배열의 첫 번째 요소를 가져옵니다.
        JsonNode responseArray = rootNode.get("response").get(0);

        // "code" 값을 가져옵니다.
        String codeValue = responseArray.get("code").asText();

        // 결과를 출력합니다.
        System.out.println("Code 값: " + codeValue);

        // access token 발급
        WebClient webclient2 = WebClient.builder().build();
        String token_url = "https://dev-api.cyber-i.com/svc/moim/token?code=" + codeValue + "&client_id=testClient2&client_secret=" + client_secret +
                "&grant_type=authorization_code&redirect_uri=" + redirect_url;

        String response2 = webclient2.get().uri(token_url).retrieve().bodyToMono(String.class).block();
        JsonNode rootNode2 = objectMapper.readTree(response2);
        access_token = rootNode2.get("access_token").asText();
        String refresh_token = rootNode2.get("refresh_token").asText();

        System.out.println("access_token" + access_token);

        return login_member;

    }

}
