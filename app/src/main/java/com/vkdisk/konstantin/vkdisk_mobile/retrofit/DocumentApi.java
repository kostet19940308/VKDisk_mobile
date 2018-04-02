package com.vkdisk.konstantin.vkdisk_mobile.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by Konstantin on 01.04.2018.
 */

public interface DocumentApi {
    @GET("api/v1/documents/?root")
    Call<ResponseBody> getAllDocument(@Header("Cookie") String cookie, @Header("X-CSRFtoken") String csrf);
    //Call<ResponseBody> getAllDocument();
}
