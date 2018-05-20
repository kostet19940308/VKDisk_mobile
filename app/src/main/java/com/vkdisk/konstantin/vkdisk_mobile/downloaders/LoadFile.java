package com.vkdisk.konstantin.vkdisk_mobile.downloaders;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by nagai on 18.05.2018.
 */

public class LoadFile extends Thread {
    private final String src;
    private final File dest;

    public LoadFile(String src, File dest) {
        this.src = src;
        this.dest = dest;
    }

    @Override
    public void run() {
        try {
            FileUtils.copyURLToFile(new URL(src), dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
