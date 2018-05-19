package com.vkdisk.konstantin.vkdisk_mobile.requests;

import java.util.ArrayList;

/**
 * Created by nagai on 19.05.2018.
 */

public class DocumentDeleteRequest {
    final ArrayList<Integer> docs;

    public DocumentDeleteRequest(ArrayList<Integer> docs) {
        this.docs = docs;
    }
}
