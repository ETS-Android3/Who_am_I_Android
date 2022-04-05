package com.lhs.project.api;

import com.lhs.project.model.ItemList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ItemApi {
    // 검색기록 가져오기
    @GET("v3/search")
    Call<ItemList> getItemList(@Query("part") String part,
                               @Query("q") String q,
                               @Query("key") String key,
                               @Query("safeSearch") String safeSearch,
                               @Query("pageToken") String pageToken);
}