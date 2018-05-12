package com.vkdisk.konstantin.vkdisk_mobile.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by nagai on 17.04.2018.
 */

public interface DocumentRootFilterApi {
    @GET("/api/v1/documents/?root&filter")
    Call<ResponseBody> getAllFilterDocuments(@Query("name") String filter,
                                             @Query("sort") String sort,
                                             @Query("reverse") String reverse);
}
