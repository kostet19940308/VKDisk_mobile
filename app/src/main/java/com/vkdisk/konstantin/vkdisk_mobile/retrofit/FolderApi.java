package com.vkdisk.konstantin.vkdisk_mobile.retrofit;

import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.models.Folder;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiListResponse;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiRetrieveResponse;
import com.vkdisk.konstantin.vkdisk_mobile.requests.UpdateOrCreateRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface FolderApi {
    @GET("/api/v1/folders/?large")
    Call<ApiListResponse<Folder>> getFolders(@Query("folder") int folderId);

    @GET("/api/v1/folders/?large&transfer")
    Call<ApiListResponse<Folder>> getRootFolders();

    @PUT("/api/v1/folders/{folderId}/")
    Call<ApiRetrieveResponse> updateFolder(@Body UpdateOrCreateRequest body);

    @POST("/api/v1/folders/?root")
    Call<ApiRetrieveResponse> createFolderRoot(@Body UpdateOrCreateRequest body);

    @POST("/api/v1/folders/")
    Call<ApiRetrieveResponse> createFolder(@Body UpdateOrCreateRequest body, @Query("folder") int folderId);
}
