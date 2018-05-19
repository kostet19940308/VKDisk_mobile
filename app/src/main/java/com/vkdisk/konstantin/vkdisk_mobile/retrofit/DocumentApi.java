package com.vkdisk.konstantin.vkdisk_mobile.retrofit;

import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiListResponse;
import com.vkdisk.konstantin.vkdisk_mobile.requests.DocumentDeleteRequest;
import com.vkdisk.konstantin.vkdisk_mobile.requests.UpdateOrCreateRequest;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by nagai on 06.04.2018.
 */

public interface DocumentApi {
    @GET("/api/v1/documents/?large")
    Call<ApiListResponse<Document>> getAllDocuments(@Query("folder") int folderId,
                                                    @Query("name") String filter,
                                                    @Query("sort") String sort,
                                                    @Query("reverse") String reverse);

    @GET("/api/v1/documents/?root&large&filter")
    Call<ApiListResponse<Document>> getRootDocuments(@Query("name") String filter,
                                                     @Query("sort") String sort,
                                                     @Query("reverse") String reverse);
    @POST("/api/v1/documents/?bulk_delete")
    Call<ApiListResponse<Document>> deleteDocument(@Body DocumentDeleteRequest body);

    @PUT("/api/v1/documents/{documentId}/")
    Call<ApiListResponse<Document>> updateDocument(@Body UpdateOrCreateRequest body);
}
