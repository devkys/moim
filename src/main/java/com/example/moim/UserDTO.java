package com.example.moim;

public class UserDTO {
    private String email;
    private String nickname;
    private String pw;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", pw='" + pw + '\'' +
                '}';
    }


}
