package com.vkdisk.konstantin.vkdisk_mobile.pipline;

import retrofit2.Call;

public class ApiHandlerTask<T> extends BaseHandlerTask {
    private Call<T> mCall;
    private int mActionCode;

    public ApiHandlerTask(Call<T> call, int actionCode) {
        mCall = call;
        mActionCode = actionCode;
    }

    public Call<T> getCall() {
        return mCall;
    }

    public int getActionCode() {
        return mActionCode;
    }

    public Response<T> makeResponse(T content) {
        return new Response<>(mActionCode, content);
    }
}
