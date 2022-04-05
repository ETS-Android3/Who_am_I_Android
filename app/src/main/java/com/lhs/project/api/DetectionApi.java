package com.lhs.project.api;

import com.lhs.project.model.DetectionRes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DetectionApi {
    // 사진디텍션
    @GET("/dev/v1/labling")
    Call<DetectionRes> LableResource(@Query("img_url") String img_url);
}
