package com.vkdisk.konstantin.vkdisk_mobile.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ClickRecyclerAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ItemRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, getArguments().getString("data"));
        if (savedInstanceState != null) {
            currentVisiblePosition = savedInstanceState.getInt(VISIBLE_POSITION);
        }
        try {
            jsonObject = new JSONObject(getArguments().getString("data"));
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
//        Toast.makeText(this, ItemRecyclerAdapter.jsonArray.getJSONObject(position).getString("title"), Toast.LENGTH_SHORT).show();
    }
}
