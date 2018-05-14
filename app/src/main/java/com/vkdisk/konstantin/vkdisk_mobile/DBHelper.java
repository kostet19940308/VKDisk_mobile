package com.vkdisk.konstantin.vkdisk_mobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

import com.vkdisk.konstantin.vkdisk_mobile.models.Document;
import com.vkdisk.konstantin.vkdisk_mobile.models.Folder;

import java.util.ArrayList;

/**
 * Created by Konstantin on 14.05.2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    public final static String DB_NAME = "vkdisk";
    public final static int DB_VER = 1;
    String[] document_column = new String[]{"id", "title", "author", "folder", "vk_url"};
    String[] folder_column = new String[]{"id", "author", "title", "root", "type"};
    private final static String DOCUMENTS = "CREATE TABLE documents (id INTEGER PRIMARY KEY, title TEXT, author INTEGER, " +
            "folder TEXT, vk_url TEXT);";
    private final static String FOLDERS = "CREATE TABLE folders (id INTEGER PRIMARY KEY, author INTEGER, title TEXT, " +
            "root INTEGER, type TEXT, \n" +
            "FOREIGN KEY (root) REFERENCES folders (id));";

    public final String LOG_TAG = this.getClass().getSimpleName();

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private static final class Factory implements SQLiteDatabase.CursorFactory {
        @Override
        public Cursor newCursor(SQLiteDatabase sqLiteDatabase, SQLiteCursorDriver sqLiteCursorDriver, String s, SQLiteQuery sqLiteQuery) {
            return new SQLiteCursor(sqLiteCursorDriver, s, sqLiteQuery);
        }
    }

    public DBHelper(Context context){
        this(context, DB_NAME, new Factory(), DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DOCUMENTS);
        db.execSQL(FOLDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addOrUpdateDocument(Document document){
        SQLiteDatabase db = getWritableDatabase();
        if(db != null){
            ContentValues values = new ContentValues();
            values.put("id", document.getId());
            values.put("title", document.getTitle());
            values.put("author", document.getAuthor());
            values.put("folder", document.getFolder());
            values.put("vk_url", document.getVkUrl());

            long conflict =  db.insertWithOnConflict("documents", null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if (conflict == -1){
                db.update("documents", values, "id=?", new String[]{String.valueOf(document.getId())});
            }
            db.close();
        }
    }

    public Document getDocumentById(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = null;
        Document document = null;
        if(db != null){
            try {
                cr = db.query("documents", document_column, "id=?" , new String[]{String.valueOf(id)}, null, null, null);
                document = documentsFromCursor(cr).get(0);
            } catch (Exception e){
                Log.d(LOG_TAG,e.getMessage());
            }finally {
                if (cr != null) cr.close();
            }
            db.close();
        }
        return document;
    }

    public Document getDocumentByTitle(String title){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = null;
        Document document = null;
        if(db != null){
            try {
                cr = db.query("documents", document_column, "title=?" , new String[]{title}, null, null, null);
                document = documentsFromCursor(cr).get(0);
            } catch (Exception e){
                Log.d(LOG_TAG,e.getMessage());
            }finally {
                if (cr != null) cr.close();
            }
            db.close();
        }
        return document;
    }

    private ArrayList<Document> documentsFromCursor(Cursor cursor) throws Exception{
        ArrayList<Document> documents = new ArrayList<>();
        cursor.moveToFirst();
        Document document;
        while (!cursor.isAfterLast()){
            document = new Document();

            document.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            document.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            document.setAuthor(cursor.getInt(cursor.getColumnIndexOrThrow("author")));
            document.setFolder(cursor.getString(cursor.getColumnIndexOrThrow("folder")));
            document.setVkUrl(cursor.getString(cursor.getColumnIndexOrThrow("vk_url")));

            documents.add(document);
            cursor.moveToNext();
        }
        return documents;
    }

    public void addOrUpdateFolder(Folder folder){
        SQLiteDatabase db = getWritableDatabase();
        if(db != null){
            ContentValues values = new ContentValues();
            values.put("id", folder.getId());
            values.put("title", folder.getTitle());
            values.put("author", folder.getAuthor());
            values.put("root", folder.getRoot());
            values.put("type", folder.getType());

            long conflict =  db.insertWithOnConflict("folders", null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if (conflict == -1){
                db.update("folders", values, "id=?", new String[]{String.valueOf(folder.getId())});
            }
            db.close();
        }
    }

    public Folder getFolderById(int id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = null;
        Folder folder = null;
        if(db != null){
            try {
                cr = db.query("folders", folder_column, "id=?" , new String[]{String.valueOf(id)}, null, null, null);
                folder = foldersFromCursor(cr).get(0);
            } catch (Exception e){
                Log.d(LOG_TAG,e.getMessage());
            }finally {
                if (cr != null) cr.close();
            }
            db.close();
        }
        return folder;
    }

    public Folder getFolderByTitle(String title){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = null;
        Folder folder = null;
        if(db != null){
            try {
                cr = db.query("folders", folder_column, "title=?" , new String[]{title}, null, null, null);
                folder = foldersFromCursor(cr).get(0);
            } catch (Exception e){
                Log.d(LOG_TAG,e.getMessage());
            }finally {
                if (cr != null) cr.close();
            }
            db.close();
        }
        return folder;
    }

    private ArrayList<Folder> foldersFromCursor(Cursor cursor) throws Exception{
        ArrayList<Folder> folders = new ArrayList<>();
        cursor.moveToFirst();
        Folder folder;
        while (!cursor.isAfterLast()){
            folder = new Folder();

            folder.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            folder.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            folder.setAuthor(cursor.getInt(cursor.getColumnIndexOrThrow("author")));
            folder.setRoot(cursor.getInt(cursor.getColumnIndexOrThrow("root")));
            folder.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("type")));

            folders.add(folder);
            cursor.moveToNext();
        }
        return folders;
    }

    public ArrayList<Folder> getAllChats() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = null;
        ArrayList<Folder> folders = null;
        if(db != null){
            try {
                cr = db.query("folders", folder_column, "type=?" , new String[]{"chat"}, null, null, "name");
                folders = foldersFromCursor(cr);
            } catch (Exception e){
                Log.d(LOG_TAG,e.getMessage());
            }finally {
                if (cr != null) cr.close();
            }
            db.close();
        }
        return folders;
    }

    public ArrayList<Document> getAllDocumentsInFolder(String folder) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = null;
        ArrayList<Document> documents = null;
        if(db != null){
            try {
                cr = db.query("documents", document_column, "folder=?" , new String[]{folder}, null, null, "name");
                documents = documentsFromCursor(cr);
            } catch (Exception e){
                Log.d(LOG_TAG,e.getMessage());
            }finally {
                if (cr != null) cr.close();
            }
            db.close();
        }
        return documents;
    }

    public ArrayList<Folder> getAllFoldersInFolder(int root) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = null;
        ArrayList<Folder> folders = null;
        if(db != null){
            try {
                cr = db.query("documents", folder_column, "root=?" , new String[]{String.valueOf(root)}, null, null, "name");
                folders = foldersFromCursor(cr);
            } catch (Exception e){
                Log.d(LOG_TAG,e.getMessage());
            }finally {
                if (cr != null) cr.close();
            }
            db.close();
        }
        return folders;
    }

}
