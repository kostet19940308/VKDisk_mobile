package com.vkdisk.konstantin.vkdisk_mobile.pipline;

import java.util.List;

import retrofit2.Call;

public class ApiListHandlerTask<T> extends ApiHandlerTask<ApiListResponse<T>> {

public ApiListHandlerTask(Call<ApiListResponse<T>> call, int actionCode) {
        super(call, actionCode);
        }

@Override
public Response<ApiListResponse<T>> makeResponse(ApiListResponse<T> content) {
        return super.makeResponse(content);
        }
        }
