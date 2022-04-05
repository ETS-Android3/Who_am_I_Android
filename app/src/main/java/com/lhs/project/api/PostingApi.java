package com.lhs.project.api;

import com.lhs.project.model.PostingList;
import com.lhs.project.model.PostingRes;
import com.lhs.project.model.nuPostingRes;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostingApi {
    // 포스팅리스트 가져오기
    @GET("/dev/v1/posting")
    Call<PostingList> getPostingMemoList(@Query("offset") int offset,
                                         @Query("limit") int limit);

    // 유저 포스팅 업로드 (Response 값이 nuPostingRes와 같기 때문에 그 클래스를 같이 씁니다.
    @Multipart
    @POST("/dev/v1/posting")
    Call<nuPostingRes> PostingUpload(@Header("Authorization") String access_Token,
                                     @Part MultipartBody.Part image,
                                     @PartMap Map<String, RequestBody> params);

    // 비유저포스팅업로드
    @Multipart
    @POST("/dev/v1/nuposting")
    Call<nuPostingRes> nuPostingUpload(@Part MultipartBody.Part image);


    // 유저포스팅리스트
    @GET("/dev/v1/myposting")
    Call<PostingList> myPostingList(@Header("Authorization") String access_Token,
                                    @Query("offset") int offset,
                                    @Query("limit") int limit);

    // 포스트 삭제
    @DELETE("/dev/v1/posting/{posting_id}")
    Call<PostingRes> myPostingDelete(@Header("Authorization") String access_Token,
                                     @Path("posting_id") int posting_id);
}
