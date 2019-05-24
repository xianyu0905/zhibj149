package com.qq149.zhibj149.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.qq149.zhibj149.R;

/**
 * 自定义三级缓存图片加载工具
 * @author zhuww
 * @description: 149
 * @date :2019/5/23 21:23
 */
public class MyBitmapUtils {

    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;

    private ImageView imageView;
    private String url;
    //构造方法new好
    public MyBitmapUtils(){
        mLocalCacheUtils = new LocalCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils);
    }

    public void display(ImageView imageView, String url) {
        //优先从内存中加载图片，速度最快，不浪费流量
        //其次从本地（sdcard）加载图片，速度快，不浪费流量
        //最后从网络下载图片，速度慢，浪费流量
        imageView.setImageResource(R.drawable.pic_item_list_default);

        Bitmap bitmap =mLocalCacheUtils.getLocalCache(url);
        if (bitmap!=null){
            imageView.setImageBitmap(bitmap);
            System.out.println("从本地加载图片啦！！！");
            return;//截胡
        }

        //mNetCacheUtils.getBitmapFromNet(imageView,url);
        mNetCacheUtils.getBitmapFromNet(imageView,url);

    }
}
