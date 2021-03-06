package com.qq149.zhibj149.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.qq149.zhibj149.base.BaseMenuDetailPager;
import com.qq149.zhibj149.base.BasePager;
import com.qq149.zhibj149.base.impl.menu.InteractMenuDetailPager;
import com.qq149.zhibj149.base.impl.menu.NewsMenuDetailPager;
import com.qq149.zhibj149.base.impl.menu.PhotosMenuDetailPager;
import com.qq149.zhibj149.base.impl.menu.TopicMenuDetailPager;
import com.qq149.zhibj149.domain.NewsMenu;
import com.qq149.zhibj149.fragment.LeftMenuFragment;
import com.qq149.zhibj149.global.GlobalConstants;
import com.qq149.zhibj149.utils.CacheUtils;

import java.util.ArrayList;


/**
 * 新闻中心
 * @author zhuww
 * @description: 149
 * @date :2019/5/18 14:55
 */
public class NewsCenterPager extends BasePager {
    //菜单详情集合
    private ArrayList<BaseMenuDetailPager> mMenuDetailPagers;
    private NewsMenu mNewsData;//分类信息网络数据



    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        System.out.println("新闻中心初始化啦...");
        //要给帧布局填充布局对象
       /* TextView view = new TextView(mActivity);
        view.setText("新闻中心");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);

        flContent.addView(view);*/
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
        mNewsData  = gson.fromJson(json, NewsMenu.class);
        System.out.println("解析结果："+mNewsData);

        //获取侧边栏对象
        MainActivity mainUI = (MainActivity) mActivity;
        LeftMenuFragment fragment = mainUI.getLeftMenuFragment();

        //给侧边栏设置数据
        fragment.setMenuData(mNewsData.data);
        //初始化4个菜单详情页
        mMenuDetailPagers = new ArrayList<BaseMenuDetailPager>();
        mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity,mNewsData.data.get(0).children));
        mMenuDetailPagers.add(new TopicMenuDetailPager(mActivity));
        mMenuDetailPagers.add(new PhotosMenuDetailPager(mActivity,btnPhoto));
        mMenuDetailPagers.add(new InteractMenuDetailPager(mActivity));

        //将新闻菜单详情页设置为默认页面
        setCurrentDetailPager(0);

    }

    //设置菜单详情页
    public void setCurrentDetailPager(int position){
        //重新给fragLayout添加内容
        BaseMenuDetailPager pager = mMenuDetailPagers.get(position);//获取当前应该显示的页面
        View view= pager.mRootView;//当前页面的布局

        //清除之前旧的布局
        flContent.removeAllViews();
        flContent.addView(view);//给帧布局添布局

        //初始化页面数据
        pager.initData();

        //更新标题
        tvTitle.setText(mNewsData.data.get(position).title);

        //如果是组图页面，需要显示切换按钮
        if (pager instanceof  PhotosMenuDetailPager){
            btnPhoto.setVisibility(View.VISIBLE);
        }else{
            //隐藏切换按钮
            btnPhoto.setVisibility(View.GONE);
        }
    }

}
