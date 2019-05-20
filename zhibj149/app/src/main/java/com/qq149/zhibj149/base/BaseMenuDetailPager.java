package com.qq149.zhibj149.base;

import android.app.Activity;
import android.view.View;

/**
 * 菜单详情页基类
 * @author zhuww
 * @description: 149
 * @date :2019/5/20 19:23
 */
public abstract class BaseMenuDetailPager {

    public Activity mActivity;
    public View mRootView;//菜单详情页根布局

    public BaseMenuDetailPager(Activity activity){
        mActivity = activity;
        mRootView = initView();

    }
    //初始化布局，必须子类实现
    public abstract View initView();

    //初始化数据
    public void initData(){

    }
}
