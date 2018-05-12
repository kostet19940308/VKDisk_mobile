package com.vkdisk.konstantin.vkdisk_mobile.fragments;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vkdisk.konstantin.vkdisk_mobile.R;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ClickRecyclerAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ItemRecyclerAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.ChatApi;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.DocumentApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.ContentValues.TAG;

/**
 * Created by nagai on 06.04.2018.
 */

public class FolderListFragment extends Fragment implements
        ClickRecyclerAdapter.OnItemClickListener{

    private RecyclerView recyclerView;
    private ClickRecyclerAdapter recyclerAdapter;
    JSONObject jsonObject;
    int currentVisiblePosition = 0;
    private static final String VISIBLE_POSITION = "position";
    private static final String ID_FOLDER = "idFolder";
    DocumentLisFragment documentLisFragment = new DocumentLisFragment();
    FragmentTransaction fragmentTransaction;



    String cookies;
    String csrf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, getArguments().getString("data"));
        if (savedInstanceState != null) {
            currentVisiblePosition = savedInstanceState.getInt(VISIBLE_POSITION);
        }
        try {
            // Надо выгружать из БД
            jsonObject = new JSONObject(getArguments().getString("data"));
            SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this.getContext());
            cookies = pref.getString(getString(R.string.cookie), "");
            csrf = pref.getString(getString(R.string.csrf), "");
            Log.d(TAG, String.valueOf(jsonObject));
        } catch (JSONException e) {
            try {
                jsonObject = new JSONObject("{results: []}");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        currentVisiblePosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        Log.d(TAG, Integer.toString(currentVisiblePosition));
        super.onSaveInstanceState(outState);
        outState.putInt(VISIBLE_POSITION, currentVisiblePosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = new RecyclerView(getActivity());
        try {
            recyclerAdapter = new ClickRecyclerAdapter(getActivity().getLayoutInflater(), this, jsonObject.getJSONArray("results"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        return recyclerView;
    }

    @Override
    public void onResume(){
        super.onResume();
        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPosition(currentVisiblePosition);
    }

    @Override
    public void onItemClick(View view, int position) throws JSONException {
        // Тоже в scheduler
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getActivity().getString(R.string.basic_url))
                .client(client)
                .build();
        final DocumentApi documentApi = retrofit.create(DocumentApi.class);
//        documentApi.getAllDocuments(Integer.parseInt(ItemRecyclerAdapter.jsonArray.getJSONObject(position).getString("id")), cookies, csrf.substring(csrf.indexOf("=") + 1, csrf.indexOf(";"))).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.d(TAG, String.valueOf(response.code()));
//                try {
//                    JSONObject jsonObject = new JSONObject(response.body().string());
//                    Log.d(TAG, String.valueOf(jsonObject));
//                    Bundle bundle = new Bundle();
//                    bundle.putString("data", String.valueOf(jsonObject));
//                    documentLisFragment.setArguments(bundle);
//                    fragmentTransaction = getFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.fragment, documentLisFragment);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.d(TAG, t.getMessage());
//            }
//        });
    }
}
