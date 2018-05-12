package com.vkdisk.konstantin.vkdisk_mobile.pipline;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiListResponse<T> {
    @SerializedName("count")
    private Integer count;

    @SerializedName("next")
    private String next;

    @SerializedName("prev")
    private String prev;

    @SerializedName("results")
    private List<T> results;

    public List<T> getResults() {
        return results;
    }
}
