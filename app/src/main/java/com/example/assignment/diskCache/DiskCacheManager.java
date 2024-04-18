package com.example.assignment.diskCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class DiskCacheManager {
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final long MAX_SIZE = 1000 * 1024 * 1024; // 10MB
    private static final String TAG = "DiskCacheManager";

    private final DiskLruCache diskLruCache;

    public DiskCacheManager(Context context) {
        File diskCacheDir = getDiskCacheDir(context, "bitmap_cache");
        diskLruCache = DiskLruCache.openCache(context, diskCacheDir, MAX_SIZE);
    }

    public void putBitmap(String key, Bitmap bitmap) {
        if (diskLruCache == null || key == null || bitmap == null) return;

        diskLruCache.put(key, bitmap);

    }

    public Bitmap findBitmap(String key) {
        if (diskLruCache == null || key == null) return null;

        return diskLruCache.get(key);

    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        return new File(context.getCacheDir(), uniqueName);
    }
}
