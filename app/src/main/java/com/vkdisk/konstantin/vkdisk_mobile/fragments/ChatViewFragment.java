package com.vkdisk.konstantin.vkdisk_mobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vkdisk.konstantin.vkdisk_mobile.ListActivity;
import com.vkdisk.konstantin.vkdisk_mobile.R;
import com.vkdisk.konstantin.vkdisk_mobile.Storage;
import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.models.Folder;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiListHandlerTask;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiListResponse;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.Response;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.folders.ClickFolderAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.ChatApi;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.DocumentApi;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.FolderApi;

import java.util.ArrayList;

import static com.vkdisk.konstantin.vkdisk_mobile.fragments.FolderViewFragment.FOLDER_ID_BUNDLE_KEY;

/**
 * Created by nagai on 15.05.2018.
 */

public class ChatViewFragment extends Fragment implements Storage.DataSubscriber, ClickFolderAdapter.OnItemClickListener {

    public static final int CHAT_LOAD_TASK_KEY = 3;
    private Storage mStorage;

    private ClickFolderAdapter folderItemRecyclerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View view = inflater.inflate(R.layout.folder_view_page, container, false);
        folderItemRecyclerAdapter = new ClickFolderAdapter(getActivity().getLayoutInflater(), new ArrayList<>(), this);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.folder_view_folder_list);
        recyclerView.setAdapter(folderItemRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt(FOLDER_ID_BUNDLE_KEY, folderItemRecyclerAdapter.getFolderId(position));
        FolderViewFragment folderViewFragment = new FolderViewFragment();
        ((ListActivity)getActivity()).setFolderList(folderViewFragment);
        ((ListActivity)getActivity()).setFragmentName(getString(R.string.document_list));
        folderViewFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment, folderViewFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onDataLoaded(int type, Response response) {
        switch (type) {
            case CHAT_LOAD_TASK_KEY:
                getActivity().runOnUiThread(() -> {
                    Response<ApiListResponse<Folder>> castedResponse = (Response<ApiListResponse<Folder>>)response;
                    folderItemRecyclerAdapter.setNewData(castedResponse.content.getResults());
                });
                break;
        }
    }

    @Override
    public void onDataLoadFailed() {

    }

    public void sendRequestForData() {
        ChatApi chatApi = mStorage.getRetrofit().create(ChatApi.class);
        ApiListHandlerTask<Folder> task;
        ApiListHandlerTask<Document> docTask;
        String sort = ((ListActivity)getActivity()).getSort();
        String filter = ((ListActivity)getActivity()).getFilter();
        String reverse = ((ListActivity)getActivity()).getReverse();
        task = new ApiListHandlerTask<Folder>(chatApi.getAllChats(filter, sort, reverse), CHAT_LOAD_TASK_KEY);
        mStorage.addApiHandlerTask(task, this);

    }

    @Override
    public void onDestroy() {
        mStorage.unsubscribe(this);
        super.onDestroy();
    }
}
