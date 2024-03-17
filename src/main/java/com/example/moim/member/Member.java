package com.example.moim.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "member")
@Data
@Getter
@Setter
public class MemberDTO {

    @Id
    @GeneratedValue
    private Long seq;
    private String email;
    private String nickname;
    private String pw;
}
