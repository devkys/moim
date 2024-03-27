package com.example.moim.member;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/*
    로그인에 필수적으로 필요한
    멤버 변수만 있는 로그인 도메인
 */
@Getter
@Setter
@Data
@Component
public class LoginDTO {

    private String email;
    private String nickname;
    private String access_token;

}
