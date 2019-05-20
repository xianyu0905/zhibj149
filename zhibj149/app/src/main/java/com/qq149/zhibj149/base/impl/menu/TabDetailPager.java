package com.qq149.zhibj149.base.impl.menu;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.qq149.zhibj149.base.BaseMenuDetailPager;
import com.qq149.zhibj149.domain.NewsMenu.NewsTabData;


/**
 * 页签页面对象
 * @author zhuww
 * @description: 149
 * @date :2019/5/20 21:01
 */
public class TabDetailPager extends BaseMenuDetailPager {

    private NewsTabData mTabData;//单个页签的网络数据
    private TextView view;

    public TabDetailPager(Activity activity, NewsTabData newsTabData) {
        super(activity);
        mTabData = newsTabData;
    }

    @Override
    public View initView() {
        //要给帧布局填充布局对象
        view = new TextView(mActivity);

        //view.setText(mTabData.title);//此处空指针

        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    @Override
    public void initData() {
        view.setText(mTabData.title);
    }
}
