package com.qq149.zhibj149.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 本地缓存
 * @author zhuww
 * @description: 149
 * @date :2019/5/24 20:18
 */
public class LocalCacheUtils {

    private static final String LOCAL_CACHE_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()+"/zhbj74_cache";
    //写本地缓存
    public void setLocalCache(String url, Bitmap bitmap){
        File dir = new File(LOCAL_CACHE_PATH);
        if (!dir.exists()|| !dir.isDirectory()){
            dir.mkdirs();//创建文件夹
        }
        try {
            String fileName=MD5Encoder.encode(url);

            File cacheFile = new File(dir,fileName);

            bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(cacheFile));
            //参1：图片格式 参2：压缩比例0-100 参3：输出流

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //读本地缓存
    public Bitmap getLocalCache(String url){
        try {
            File cacheFile = new File(LOCAL_CACHE_PATH,MD5Encoder.encode(url));
            if (cacheFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }
}
