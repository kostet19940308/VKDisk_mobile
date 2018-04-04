package com.vkdisk.konstantin.vkdisk_mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Folder {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("author")
    @Expose
    private Integer author;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("root")
    @Expose
    private Integer root;
    @SerializedName("type")
    @Expose
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getRoot() {
        return root;
    }

    public void setRoot(Integer root) {
        this.root = root;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}