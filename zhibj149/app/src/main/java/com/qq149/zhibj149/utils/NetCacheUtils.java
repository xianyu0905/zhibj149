package com.qq149.zhibj149.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络缓存工具
 *
 * @author zhuww
 * @description: 149
 * @date :2019/5/23 21:34
 */
public class NetCacheUtils {

    private Bitmap bitmap;
    private HttpURLConnection conn;
    private ImageView imageView;
    private LocalCacheUtils mLocalCacheUtils;
    public NetCacheUtils(LocalCacheUtils localCacheUtils) {
        mLocalCacheUtils = localCacheUtils;
    }


    public void getBitmapFromNet(ImageView imageView, String url) {
        //AsynTask 一部封装的工具，可以实现异步请求及主界面更新（对线程池+handler的封装）
        new BitmapTask().execute(imageView, url);//启动AsyncTask


    }

    /**
     * 三个泛型意义
     * 第一泛型：doInBackground里的参数类型
     * 第二个泛型:onProgressUpdate里的参数类型
     * 第三个泛型:onPostExecute里的参数类型及doInBackgroung的返回类型
     */
    class BitmapTask extends AsyncTask<Object, Integer, Bitmap> {

        private String url;

        //1.预加热，运行在主线程
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //System.out.println("onPreExecute");//


        }

        //2.正在加载，运行在子线程(核心方法)，可以直接异步请求
        @Override
        protected Bitmap doInBackground(Object... params) {
            //System.out.println("doInBackground");
            imageView = (ImageView) params[0];
            url = (String) params[1];

            imageView.setTag(url);
            //开始下载图片
            bitmap = download(url);
            // publishProgress(values);调用此方法实现进度更新（会回调）
            return bitmap;
        }

        //3.更新进度的方法，运行在主线程
        @Override
        protected void onProgressUpdate(Integer... values) {
            //更新进度条
            super.onProgressUpdate(values);
        }

        //4.加载结束，运行在主线程（核心方法），可以直接跟新UI
        @Override
        protected void onPostExecute(Bitmap result) {
            //System.out.println("onPostExecute");
            if (result != null) {
                //给imageView设置图片
                //由于listview的重用机制导致imageview对象可能被多个item共用，从而可能将错误的图片给了imageView对象
                //所以需要在此校验，判断是否是正确的图片

                String url = (String) imageView.getTag();

                if (url.equals(this.url)) {//判断图片绑定url是否就是当前的bitmap的url,
                    //如果是，说明图片正确
                    imageView.setImageBitmap(result);
                    System.out.println("从网络加载图片啦！！！");

                    //写本地缓存
                    mLocalCacheUtils.setLocalCache(url,result);
                }


            }
            super.onPostExecute(result);
        }
    }

    //下载图片
    private Bitmap download(String url) {

        try {
            conn = (HttpURLConnection) new URL(url).openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);//连接超时
            conn.setConnectTimeout(5000);//读取超时
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                InputStream inputStream = conn.getInputStream();
                //根据输入流生成bitmap对象
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
