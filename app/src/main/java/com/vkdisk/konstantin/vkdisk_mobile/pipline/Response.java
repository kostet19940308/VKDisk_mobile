package com.vkdisk.konstantin.vkdisk_mobile.pipline;

public class Response<T> {
    public int type;
    public T content;

    public Response(int type, T content) {
        this.type = type;
        this.content = content;
    }
}
