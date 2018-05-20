package com.vkdisk.konstantin.vkdisk_mobile.retrofit;

import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.models.Folder;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiListResponse;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiRetrieveResponse;
import com.vkdisk.konstantin.vkdisk_mobile.requests.UpdateOrCreateRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FolderApi {
    @GET("/api/v1/folders/?large")
    Call<ApiListResponse<Folder>> getFolders(@Query("folder") int folderId);

    @GET("/api/v1/folders/?large&transfer")
    Call<ApiListResponse<Folder>> getRootFolders();

    @PUT("/api/v1/folders/{id}/")
    Call<Folder> updateFolder(@Body UpdateOrCreateRequest body, @Path("id") int id);

    @POST("/api/v1/folders/?root")
    Call<Folder> createFolderRoot(@Body UpdateOrCreateRequest body);

    @POST("/api/v1/folders/")
    Call<Folder> createFolder(@Body UpdateOrCreateRequest body, @Query("folder") int folderId);

    @DELETE("/api/v1/folders/{id}/")
    Call<Folder> deleteFolder(@Path("id") int id);
}
