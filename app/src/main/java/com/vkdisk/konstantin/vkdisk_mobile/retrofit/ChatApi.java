package com.vkdisk.konstantin.vkdisk_mobile.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by Konstantin on 01.04.2018.
 */

public interface ChatApi {
    @GET("/api/v1/folders/?type=chat&large&filter")
    Call<ResponseBody> getAllChats(@Query("name") String filter,
                                   @Query("sort") String sort,
                                   @Query("reverse") String reverse);
}
