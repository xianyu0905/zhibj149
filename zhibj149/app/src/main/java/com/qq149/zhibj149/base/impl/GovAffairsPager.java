package com.qq149.zhibj149.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.qq149.zhibj149.base.BasePager;


/**
 * 政务
 * @author zhuww
 * @description: 149
 * @date :2019/5/18 14:55
 */
public class GovAffairsPager extends BasePager {

    public GovAffairsPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        System.out.println("政务初始化啦...");
        //要给帧布局填充布局对象
        TextView view = new TextView(mActivity);
        view.setText("政务");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);

       flContent.addView(view);
        //修改页面标题
        tvTitle.setText("政务");
        //显示菜单按钮
        btnMenu.setVisibility(View.VISIBLE);
    }
}
