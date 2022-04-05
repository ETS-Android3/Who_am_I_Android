package com.lhs.project.model;

public class UserRes {
    private String result;
    private String access_token;

    public UserRes(String result, String access_token) {
        this.result = result;
        this.access_token = access_token;
    }

    public UserRes(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}

