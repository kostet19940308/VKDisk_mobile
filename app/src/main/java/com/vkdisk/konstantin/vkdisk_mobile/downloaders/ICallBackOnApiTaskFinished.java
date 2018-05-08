package com.vkdisk.konstantin.vkdisk_mobile.downloaders;

import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiHandlerTask;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.Response;

public interface ICallBackOnApiTaskFinished {
    public void onResponse(ApiHandlerTask task, int actionCode, Response response);

    public void onFailure(ApiHandlerTask task);
}
