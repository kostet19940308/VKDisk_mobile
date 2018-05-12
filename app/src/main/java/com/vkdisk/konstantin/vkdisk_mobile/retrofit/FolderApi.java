package com.vkdisk.konstantin.vkdisk_mobile.retrofit;

import com.vkdisk.konstantin.vkdisk_mobile.models.Folder;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FolderApi {
    @GET("/api/v1/folders/?large")
    Call<ApiListResponse<Folder>> getFolders(@Query("folder") int folderId);

    @GET("/api/v1/folders/?large&root")
    Call<ApiListResponse<Folder>> getRootFolders();
}
