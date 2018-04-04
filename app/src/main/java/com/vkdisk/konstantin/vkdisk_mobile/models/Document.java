package com.vkdisk.konstantin.vkdisk_mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Document {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("author")
    @Expose
    private Integer author;
    @SerializedName("folder")
    @Expose
    private Folder folder;
    @SerializedName("vk_url")
    @Expose
    private String vkUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public String getVkUrl() {
        return vkUrl;
    }

    public void setVkUrl(String vkUrl) {
        this.vkUrl = vkUrl;
    }

}