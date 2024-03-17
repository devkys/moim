package com.example.moim.member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity(name="MEMBER")
@Getter
@Setter
@Data
public class Member {

    @Id
    private String email;
    private String nickname;
    private String pw;
    private String refresh_token;
}
