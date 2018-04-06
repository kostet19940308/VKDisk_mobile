package com.vkdisk.konstantin.vkdisk_mobile.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by nagai on 06.04.2018.
 */

public interface DocumentApi {
    @GET("/api/v1/documents/")
    Call<ResponseBody> getAllDocuments(@Query("folder") int folderId, @Header("Cookie") String cookie, @Header("X-CSRFtoken") String csrf);
}
