package com.qq149.zhibj149.base.impl.menu;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.qq149.zhibj149.base.BaseMenuDetailPager;

/**
 * 菜单详情页---互动
 * @author zhuww
 * @description: 149
 * @date :2019/5/20 19:32
 */
public class InteractMenuDetailPager extends BaseMenuDetailPager {


    public InteractMenuDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        TextView view = new TextView(mActivity);
        view.setText("菜单详情页---互动");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        return view;
    }
}
