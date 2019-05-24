package com.qq149.zhibj149.utils;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 内存缓存
 * @author zhuww
 * @description: 149
 * @date :2019/5/24 20:57
 */
public class MemoryCacheUtils {

   // private HashMap<String, Bitmap> mMenoryCache= new HashMap<String,Bitmap>();
   private HashMap<String, SoftReference<Bitmap>> mMenoryCache= new HashMap<String,SoftReference<Bitmap>>();
    /**
     * 写缓存
     */
    public void setMemoryCache(String url,Bitmap bitmap){
       // mMenoryCache.put(url,bitmap);
        SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bitmap);//使用软引用将bitmap包装起来
        mMenoryCache.put(url,soft);
    }

    /**
     *读缓存
     */
    public Bitmap getMemoryCache(String url){
        SoftReference<Bitmap> softReference = mMenoryCache.get(url);
        if (softReference!=null){
            Bitmap bitmap = softReference.get();
            return bitmap;
        }
        return null;
      // return mMenoryCache.get(url);
    }


}
