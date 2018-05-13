package com.vkdisk.konstantin.vkdisk_mobile.retrofit;

import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiListResponse;

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
    Call<ApiListResponse<Document>> getAllDocuments(@Query("folder") int folderId,
                                                    @Query("name") String filter,
                                                    @Query("sort") String sort,
                                                    @Query("reverse") String reverse);

    @GET("/api/v1/documents/?root&filter")
    Call<ApiListResponse<Document>> getRootDocuments(@Query("name") String filter,
                                                     @Query("sort") String sort,
                                                     @Query("reverse") String reverse);
}
