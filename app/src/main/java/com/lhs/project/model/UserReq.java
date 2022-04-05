package com.lhs.project.model;

public class UserReq {

    private String email;
    private String password;
    private String username;

    // 회원가입 시
    public UserReq(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    // 로그인 시
    public UserReq(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
