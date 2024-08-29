package com.example.moim.member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/*
    db 테이블과 매핑되는 유저 엔티티 도메인
 */
@Entity(name = "Member")
@Getter
@Setter
@Data
@Component
public class Member {

    @Id
    private String email;
    private String nickname;
    private String pw;
    private String refresh_token;
}
