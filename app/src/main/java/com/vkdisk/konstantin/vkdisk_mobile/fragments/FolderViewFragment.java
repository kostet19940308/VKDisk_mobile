package com.vkdisk.konstantin.vkdisk_mobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vkdisk.konstantin.vkdisk_mobile.ListActivity;
import com.vkdisk.konstantin.vkdisk_mobile.R;
import com.vkdisk.konstantin.vkdisk_mobile.Storage;
import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.models.Folder;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiHandlerTask;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiListHandlerTask;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiListResponse;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.Response;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.folders.ClickFolderAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.folders.DocumentItemRecyclerAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.folders.FolderItemRecyclerAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.DocumentApi;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.FolderApi;

import java.util.ArrayList;
import java.util.List;

import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;

public class FolderViewFragment extends Fragment implements Storage.DataSubscriber, ClickFolderAdapter.OnItemClickListener {

    private static final String LOGGER_KEY = FolderViewFragment.class.getSimpleName();

    public static final String FOLDER_ID_BUNDLE_KEY = "folder_id";

    public static final int FOLDER_LOAD_TASK_KEY = 1;
    public static final int DOCUMENT_LOAD_TASK_KEY = 2;

    private int folderId;
    private Storage mStorage;

    private ClickFolderAdapter folderItemRecyclerAdapter;
    private DocumentItemRecyclerAdapter documentItemRecyclerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            folderId = savedInstanceState.getInt(FOLDER_ID_BUNDLE_KEY);
        } else {
            folderId = getArguments().getInt(FOLDER_ID_BUNDLE_KEY);
        }
        mStorage = Storage.getOrCreateInstance(getActivity().getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        sendRequestForData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.folder_view_page, container, false);;

        folderItemRecyclerAdapter = new ClickFolderAdapter(getActivity().getLayoutInflater(), new ArrayList<>(), this);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        documentItemRecyclerAdapter = new DocumentItemRecyclerAdapter(getActivity().getLayoutInflater(), new ArrayList<>());
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.folder_view_folder_list);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        RvJoiner rvJoiner = new RvJoiner();
        rvJoiner.add(new JoinableAdapter(folderItemRecyclerAdapter));
        rvJoiner.add(new JoinableAdapter(documentItemRecyclerAdapter));

        rv.setAdapter(rvJoiner.getAdapter());
        rv.addItemDecoration(itemDecoration);
        return view;
    }

    public void sendRequestForData() {
        FolderApi folderApi = mStorage.getRetrofit().create(FolderApi.class);
        DocumentApi documentApi = mStorage.getRetrofit().create(DocumentApi.class);
        ApiListHandlerTask<Folder> task;
        ApiListHandlerTask<Document> docTask;
        String sort = ((ListActivity)getActivity()).getSort();
        String filter = ((ListActivity)getActivity()).getFilter();
        String reverse = ((ListActivity)getActivity()).getReverse();
        if (folderId > 0) {
            task = new ApiListHandlerTask<Folder>(folderApi.getFolders(folderId), FOLDER_LOAD_TASK_KEY);
            docTask = new ApiListHandlerTask<>(documentApi.getAllDocuments(folderId, filter, sort, reverse), DOCUMENT_LOAD_TASK_KEY);
        } else {
            task = new ApiListHandlerTask<Folder>(folderApi.getRootFolders(), FOLDER_LOAD_TASK_KEY);
            docTask = new ApiListHandlerTask<>(documentApi.getRootDocuments(filter, sort, reverse), DOCUMENT_LOAD_TASK_KEY);
        }
        mStorage.addApiHandlerTask(task, this);
        mStorage.addApiHandlerTask(docTask, this);

    }

    @Override
    public void onDestroy() {
        mStorage.unsubscribe(this);
        super.onDestroy();
    }

    @Override
    public void onDataLoaded(int type, Response response) {
        switch (type) {
            case FOLDER_LOAD_TASK_KEY:
                getActivity().runOnUiThread(() -> {
                    Response<ApiListResponse<Folder>> castedResponse = (Response<ApiListResponse<Folder>>)response;
                    folderItemRecyclerAdapter.setNewData(castedResponse.content.getResults());
                });
                break;
            case DOCUMENT_LOAD_TASK_KEY:
                getActivity().runOnUiThread(() -> {
                    Response<ApiListResponse<Document>> castedResponse = (Response<ApiListResponse<Document>>)response;
                    documentItemRecyclerAdapter.setNewData(castedResponse.content.getResults());
                });
                break;
        }
    }

    @Override
    public void onDataLoadFailed() {
        Log.d(LOGGER_KEY, "Data not loaded");
    }

    @Override
    public void onItemClick(View view, int position) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt(FOLDER_ID_BUNDLE_KEY, folderItemRecyclerAdapter.getFolderId(position));
        FolderViewFragment folderViewFragment = new FolderViewFragment();
        ((ListActivity)getActivity()).setFolderList(folderViewFragment);
        folderViewFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment, folderViewFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(FOLDER_ID_BUNDLE_KEY, folderId);
        super.onSaveInstanceState(outState);
    }
}
