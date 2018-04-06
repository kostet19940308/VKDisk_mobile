package com.vkdisk.konstantin.vkdisk_mobile.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by Konstantin on 01.04.2018.
 */

public interface ChatApi {
    @GET("/api/v1/folders/?type=chat&large")
    Call<ResponseBody> getAllChats(@Header("Cookie") String cookie, @Header("X-CSRFtoken") String csrf);
}
