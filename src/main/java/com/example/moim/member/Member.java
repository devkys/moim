package com.example.moim.member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.NotFound;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/*
    db 테이블과 매핑되는 유저 엔티티 도메인
 */
@Entity(name = "Member")
@Getter
@Setter
@Data
@Component
public class Member implements UserDetails {

    @Id
    private String email;
    private String name;
    private String nickname;
    private String pw;
    private String refresh_token;

    // 사용자에게 부여된 권한 반환, null x
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    // 인증된 사용자가 사용한 패스워드 반환
    @Override
    public String getPassword() {
        return this.pw;
    }

    // 인증된 사용자가 사용한 유저네임 반환, null x
    @Override
    public String getUsername() {
        return this.name;
    }

    // 사용자 계정 만료 여부, 만료된 계정은 인증될 수 없음.
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    // 유저가 잠금 or 해제 여부, 잠긴 유저는 인증될 수 없음.
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    // 유저의 신임(패스워드) 만료 여부, 만료된 유효는 인증을 막음.
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    // 유저가 활성화 여부, 불가능한 사용자는 인증될 수 없음.
    @Override
    public boolean isEnabled() {
        return false;
    }
}
