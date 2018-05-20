package com.vkdisk.konstantin.vkdisk_mobile.retrofit;

import com.vkdisk.konstantin.vkdisk_mobile.models.Folder;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiListResponse;

import java.util.List;

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
    Call<ApiListResponse<Folder>> getAllChats(@Query("name") String filter,
                                                   @Query("sort") String sort,
                                                   @Query("reverse") String reverse);
}
