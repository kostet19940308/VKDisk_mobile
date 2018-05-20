package com.vkdisk.konstantin.vkdisk_mobile.pipline;

import retrofit2.Call;

/**
 * Created by nagai on 20.05.2018.
 */

public class ApiRetrieveHandlerTask extends ApiHandlerTask<ApiRetrieveResponse> {

    public ApiRetrieveHandlerTask(Call<ApiRetrieveResponse> call, int actionCode) {
        super(call, actionCode);
    }

    @Override
    public Response<ApiRetrieveResponse> makeResponse(ApiRetrieveResponse content) {
        return super.makeResponse(content);
    }
}
