package com.vkdisk.konstantin.vkdisk_mobile;


import android.content.Context;
import android.provider.ContactsContract;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.vkdisk.konstantin.vkdisk_mobile.downloaders.ApiDataDownloader;
import com.vkdisk.konstantin.vkdisk_mobile.downloaders.ICallBackOnApiTaskFinished;
import com.vkdisk.konstantin.vkdisk_mobile.interceptors.AddCookiesInterceptor;
import com.vkdisk.konstantin.vkdisk_mobile.interceptors.ReceivedCookiesInterceptor;
import com.vkdisk.konstantin.vkdisk_mobile.libs.PersistentCookieStore;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiHandlerTask;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.BaseHandlerTask;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.Response;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.ChatApi;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class Storage implements ICallBackOnApiTaskFinished {

    public interface DataSubscriber {
        void onDataLoaded(int type, Response response);

        void onDataLoadFailed();
    }


    private static Storage _instance;

    public static synchronized Storage getOrCreateInstance(Context context) {
        if (_instance == null) {
            _instance = new Storage(context);
        }
        return _instance;
    }

    private CookieManager cookieManager;
    private Retrofit retrofit;

    private ChatApi chatApi;

    private ApiDataDownloader apiDataDownloader;

    private Map<BaseHandlerTask, List<DataSubscriber>> commandSubscriberMap;

    private Storage(Context context) {
        String basicUrl = context.getString(R.string.basic_url);
        initRetrofit(basicUrl, context);
        initRetrofitApies();
        initDownloaders();

        commandSubscriberMap = new HashMap<>();
    }

    private void initRetrofit(String basicUrl, Context ctx) {
//        cookieManager = new CookieManager(
//                new PersistentCookieStore(ctx), CookiePolicy.ACCEPT_ALL
//        );
//        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

//        ClearableCookieJar cookieJar =
//                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(ctx));

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
//                .cookieJar(new JavaNetCookieJar(cookieManager))
//                .cookieJar(cookieJar)
                .addNetworkInterceptor(interceptor);

        client.interceptors().add(new AddCookiesInterceptor(ctx));
        client.interceptors().add(new ReceivedCookiesInterceptor(ctx));

        retrofit = new Retrofit.Builder()
                .baseUrl(basicUrl)
                .client(client.build())
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    private void initRetrofitApies() {
        chatApi = retrofit.create(ChatApi.class);
    }

    private void initDownloaders() {
        apiDataDownloader = new ApiDataDownloader("APIDataDownloader", new ICallBackOnApiTaskFinished() {
            @Override
            public void onResponse(ApiHandlerTask task, int actionCode, Response response) {

            }

            @Override
            public void onFailure(ApiHandlerTask task) {

            }
        });
        apiDataDownloader.start();
        apiDataDownloader.prepareHandler();
    }

    private void addSubscriber(BaseHandlerTask task, DataSubscriber subscriber) {
        if (!commandSubscriberMap.containsKey(task)) {
            commandSubscriberMap.put(task, new ArrayList<DataSubscriber>());
        }
        List<DataSubscriber> subscribers = commandSubscriberMap.get(task);
        if(!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        }
    }

    private void moveSubscribers(BaseHandlerTask from, BaseHandlerTask to) {
        List<DataSubscriber> subscribers = commandSubscriberMap.get(from);
        commandSubscriberMap.put(to, subscribers);
    }

    public void unsubscribe(DataSubscriber subscriber) {
        for (Map.Entry<BaseHandlerTask, List<DataSubscriber>> entry : commandSubscriberMap.entrySet()) {
            BaseHandlerTask task = entry.getKey();
            List<DataSubscriber> subsList = entry.getValue();
            subsList.remove(subscriber);
        }
    }

    private void notifySubscribers(BaseHandlerTask task, int type, Response response) {
        for (DataSubscriber subscriber : commandSubscriberMap.get(task)) {
            subscriber.onDataLoaded(type, response);
        }
    }

    @Override
    public void onResponse(ApiHandlerTask task, int actionCode, Response response) {
        if (response == null) {
            return;
        }
        boolean dataUpdated = false;
        dataUpdated = true; // TODO: вставить добавлениие в БД
        if (dataUpdated) {
            notifySubscribers(task, actionCode, response);
        }
    }

    @Override
    public void onFailure(ApiHandlerTask task) {
        for (DataSubscriber subscriber : commandSubscriberMap.get(task)) {
            subscriber.onDataLoadFailed();
        }
    }

    public void addCookiesFromString(URI uri, String cookieString) {
        CookieStore cookieStore = cookieManager.getCookieStore();
        List<HttpCookie> cookies = HttpCookie.parse(cookieString);
        for (HttpCookie cookie : cookies) {
            cookieStore.add(uri, cookie);
        }
    }

    public void addApiHandlerTask(ApiHandlerTask task, DataSubscriber subscriber) {
        addSubscriber(task, subscriber);
        apiDataDownloader.addTask(task);
    }

    public void getAllChats(DataSubscriber subscriber) {

    }

}
