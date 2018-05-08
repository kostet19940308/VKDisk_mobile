package com.vkdisk.konstantin.vkdisk_mobile.downloaders;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiHandlerTask;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.Response;

public class ApiDataDownloader extends HandlerThread {

    private final static String LOG_TAG = ApiDataDownloader.class.getSimpleName();

    private Handler downloadHandler;
    private ICallBackOnApiTaskFinished callbackOnTask;

    public ApiDataDownloader(String name, ICallBackOnApiTaskFinished callbackOnTask) {
        super(name);
        this.callbackOnTask = callbackOnTask;
    }

    public void prepareHandler() {
        downloadHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                ApiHandlerTask task = (ApiHandlerTask)msg.obj;
                Response response = handleTask(task);
                notifySubscribers(task, response);
            }
        };
    }

    private Response handleTask(ApiHandlerTask task) {

        Response responseObject;
        Log.d(LOG_TAG, "loadInBackground started");
        try {
            retrofit2.Response response = task.getCall().execute();
            Log.d(LOG_TAG, "response status:" + response.isSuccessful());
            if (response.isSuccessful()) {
                responseObject = task.makeResponse(response.body());
            } else {
                responseObject = null;
            }
        } catch (IOException | NullPointerException e) {
            Log.e(LOG_TAG, e.toString());
            responseObject = null;
        }
        return responseObject;
    }

    private void notifySubscribers(ApiHandlerTask apiTask, Response response) {
        if (callbackOnTask != null) {
            if (response == null) {
                callbackOnTask.onFailure(apiTask);
            } else {
                callbackOnTask.onResponse(apiTask, response.type, response);
            }
        }
    }

    public void addTask(ApiHandlerTask apiTask) {
        downloadHandler.obtainMessage(apiTask.getActionCode(), apiTask).sendToTarget();
    }

}
