package com.example.moim.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
@RequestMapping("api/users-mgmt")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MemberController {

    public static final String login_url = "http://192.168.0.122:5173/login";
    public static final String redirect_url = "http://192.168.0.122:5173/main";

    @PostMapping("signup")
    @ResponseBody
    public MemberDTO signup(@RequestBody MemberDTO member){
        return member;
    }
    @PostMapping("signin")
//    @ResponseBody
    public void signin(@RequestBody MemberDTO member) {
        WebClient webClient = WebClient.builder().build();
        String auth_url = "https://dev-api.cyber-i.com/svc/moim/auth?USER_ID=firstClient&login_url=" + login_url +
                "&client_id=firstClient&redircet_uri=" + redirect_url +  "&response_type=code";
        System.out.println(auth_url);
        String response = webClient.get().uri(auth_url).retrieve().bodyToMono(String.class).block();
        System.out.println(response);

    }

}
