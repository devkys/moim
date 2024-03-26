package com.example.moim.member;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Data
@Component
public class LoginDTO {

    private String email;
    private String nickname;
    private String access_token;
//    private String refresh_token;
}
