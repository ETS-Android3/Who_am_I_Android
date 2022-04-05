package com.lhs.project.api;

import com.lhs.project.model.UserReq;
import com.lhs.project.model.UserRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserApi {

    // 로그인
    @POST("/dev/v1/user/login")
    Call<UserRes> userLogin(@Body UserReq userReq);

    // 로그아웃
    @POST("/dev/v1/user/logout")
    Call<UserRes> userLogout(@Header("Authorization") String access_Token);

    // 회원가입
    @POST("/dev/v1/user/register")
    Call<UserRes> userSignUp(@Body UserReq userReq);

}
