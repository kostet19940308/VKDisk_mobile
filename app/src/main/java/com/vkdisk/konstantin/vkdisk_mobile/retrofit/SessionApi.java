package com.vkdisk.konstantin.vkdisk_mobile.retrofit;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Konstantin on 28.03.2018.
 */

public interface SessionApi {
    @FormUrlEncoded
    @POST("/social/complete/vk-oauth2/")
    Call<ResponseBody> getSession(@Field("code") String code, @Field("state") String state);
}
