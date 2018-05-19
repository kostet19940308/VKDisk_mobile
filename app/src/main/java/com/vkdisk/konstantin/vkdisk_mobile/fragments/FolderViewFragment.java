package com.vkdisk.konstantin.vkdisk_mobile.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vkdisk.konstantin.vkdisk_mobile.ListActivity;
import com.vkdisk.konstantin.vkdisk_mobile.R;
import com.vkdisk.konstantin.vkdisk_mobile.Storage;
import com.vkdisk.konstantin.vkdisk_mobile.downloaders.LoadFile;
import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.models.Folder;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiListHandlerTask;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.ApiListResponse;
import com.vkdisk.konstantin.vkdisk_mobile.pipline.Response;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.folders.ClickDocumentAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.folders.ClickFolderAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.recycleview.folders.DocumentItemRecyclerAdapter;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.DocumentApi;
import com.vkdisk.konstantin.vkdisk_mobile.retrofit.FolderApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.RvJoiner;

import static android.provider.Settings.AUTHORITY;

public class FolderViewFragment extends Fragment implements Storage.DataSubscriber,
        ClickFolderAdapter.OnItemClickListener,
        ClickDocumentAdapter.OnItemLongClickListener,
        ClickDocumentAdapter.OnDocumentClickListener{

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
        documentItemRecyclerAdapter = new ClickDocumentAdapter(getActivity().getLayoutInflater(), new ArrayList<>(), this, this);
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

    @Override
    public void onItemLongClick(View view, int position) {
        documentItemRecyclerAdapter.setChecked(position);
        ((ListActivity)getActivity()).setCheckCount(documentItemRecyclerAdapter.getCheckedCount());
    }

    public void deleteFiles() {
        documentItemRecyclerAdapter.deleteCheckedFiles();
    }

    public void setUnChecked() {
        documentItemRecyclerAdapter.setUnChecked();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onDocumentClick(View view, int position) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        AsyncTask<String, Integer, File> get = new AsyncTask<String, Integer, File>() {
            private Exception m_error = null;

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Downloading ...");
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                progressDialog.show();
            }

            @Override
            protected File doInBackground(String... params) {
                URL url;
                HttpURLConnection urlConnection;
                InputStream inputStream;
                int totalSize;
                int downloadedSize;
                byte[] buffer;
                int bufferLength;

                File file = null;
                FileOutputStream fos = null;

                try {
                    url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();
                    final File[] appsDir = ContextCompat.getExternalFilesDirs(getActivity(), null);
                    file = new File(ContextCompat.getExternalFilesDirs(getActivity(), null)[1], documentItemRecyclerAdapter.getTitle(position));
                    fos = new FileOutputStream(file);
                    inputStream = urlConnection.getInputStream();

                    totalSize = urlConnection.getContentLength();
                    downloadedSize = 0;

                    buffer = new byte[1024];
                    bufferLength = 0;

                    // читаем со входа и пишем в выход,
                    // с каждой итерацией публикуем прогресс
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        publishProgress(downloadedSize, totalSize);
                    }

                    fos.close();
                    inputStream.close();

                    return file;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    m_error = e;
                } catch (IOException e) {
                    e.printStackTrace();
                    m_error = e;
                }

                return null;
            }

            protected void onProgressUpdate(Integer... values) {
                progressDialog
                        .setProgress((int) ((values[0] / (float) values[1]) * 100));
            }

            ;

            @Override
            protected void onPostExecute(File file) {
                // отображаем сообщение, если возникла ошибка
                if (m_error != null) {
                    m_error.printStackTrace();
                    return;
                }
                // закрываем прогресс и удаляем временный файл
                progressDialog.hide();
//                Intent i = new Intent(Intent.ACTION_VIEW, FileProvider.getUriForFile(getActivity().getApplicationContext(), AUTHORITY, file));
//                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
//                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                Uri contentUri = FileProvider.getUriForFile(getActivity().getApplicationContext(), AUTHORITY, file);
//                intent.setDataAndType(contentUri, "application/pdf");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(getActivity().getApplicationContext(), "com.vkdisk.konstantin.vkdisk_mobile.fileprovider", file);
                    intent.setDataAndType(contentUri, "application/pdf");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                }
                getActivity().getApplicationContext().startActivity(intent);
            }

        }.execute(documentItemRecyclerAdapter.getVkUrl(position));

//            File dest = new File(getActivity().getApplicationContext().getFilesDir(), documentItemRecyclerAdapter.getTitle(position));
//        new LoadFile(documentItemRecyclerAdapter.getVkUrl(position), dest).start();
    }
}
