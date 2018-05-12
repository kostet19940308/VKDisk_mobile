package com.vkdisk.konstantin.vkdisk_mobile.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vkdisk.konstantin.vkdisk_mobile.R;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ClickDocumentRecycleAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.ClickRecyclerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class DocumentLisFragment extends Fragment implements ClickDocumentRecycleAdapter.OnItemClickListener, ClickDocumentRecycleAdapter.OnItemLongClickListener{

    private RecyclerView recyclerView;
    private ClickDocumentRecycleAdapter recyclerAdapter;
    JSONObject jsonObject;
    int currentVisiblePosition = 0;
    private static final String VISIBLE_POSITION = "position";
    private static final String ID_FOLDER = "idFile";

    String cookies;
    String csrf;


    public DocumentLisFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, getArguments().getString("data"));
        if (savedInstanceState != null) {
            currentVisiblePosition = savedInstanceState.getInt(VISIBLE_POSITION);
        }
        try {
            // Эту хрень надо из базы данных выгружать
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
            recyclerAdapter = new ClickDocumentRecycleAdapter(getActivity().getLayoutInflater(), this, this, jsonObject.getJSONArray("results"));
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

    }

    @Override
    public void onItemLongClick(View view, int position) throws JSONException {
        Toast.makeText(this.getContext(),
                jsonObject.getJSONArray("results").getJSONObject(position).getString("title"),
        Toast.LENGTH_SHORT).show();

        // тут надо создать массив выделенных элементов, куда передаем то, что выделяем.
        // и куда-то передавать, чтобы мы получили как-то это количесто из ListActivity
    }
}
