package com.qq149.zhibj149.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.qq149.zhibj149.MainActivity;
import com.qq149.zhibj149.base.BasePager;
import com.qq149.zhibj149.domain.NewsMenu;
import com.qq149.zhibj149.fragment.LeftMenuFragment;
import com.qq149.zhibj149.global.GlobalConstants;
import com.qq149.zhibj149.utils.CacheUtils;


/**
 * 新闻中心
 * @author zhuww
 * @description: 149
 * @date :2019/5/18 14:55
 */
public class NewsCenterPager extends BasePager {

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        System.out.println("新闻中心初始化啦...");
        //要给帧布局填充布局对象
        TextView view = new TextView(mActivity);
        view.setText("新闻中心");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);

        flContent.addView(view);
        //修改页面标题
        tvTitle.setText("新闻");
        //显示菜单按钮
        btnMenu.setVisibility(View.VISIBLE);
        /**
         * 第二周09网络缓存
         */
        //先判断有没有缓存，如果有的话，就加载缓存
        String cache = CacheUtils.getCache(GlobalConstants.CATEGORY_URL,mActivity);
        if (!TextUtils.isEmpty(cache)){
           System.out.println("发现缓存啦。。。");
           processData(cache);
        }

        //调用getDateFromServer
        getDataFromServer();


    }
    //请求服务器，获取数据
    //开源框架：XUtils
    private void getDataFromServer(){
       HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.CATEGORY_URL,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        //请求成功
                        String result = responseInfo.result;
                        System.out.println("服务器返回结果"+result);

                        //JsonObject,Gson
                        processData(result);

                        //写缓存
                        CacheUtils.setCache(GlobalConstants.CATEGORY_URL,result,mActivity);

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        //请求失败
                        error.printStackTrace();
                        Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();
                    }
                });
    }
    /**
     * 解析数据
     * **/

    private void processData(String json) {
        //Gson：Google Json
        Gson gson = new Gson();
        NewsMenu data  = gson.fromJson(json, NewsMenu.class);
        System.out.println("解析结果："+data);

        //获取侧边栏对象
        MainActivity mainUI = (MainActivity) mActivity;
        LeftMenuFragment fragment = mainUI.getLeftMenuFragment();

        //给侧边栏设置数据
        fragment.setMenuData(data.data);
    }


}
