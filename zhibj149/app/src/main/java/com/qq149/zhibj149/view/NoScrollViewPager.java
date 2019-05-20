package com.qq149.zhibj149.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 不允许滑动的viewPager
 * @author zhuww
 * @description: 149
 * @date :2019/5/18 18:16
 */
public class NoScrollViewPager extends ViewPager {


    public NoScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public NoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //重写此方法，触摸什么都不做，从而实现对滑动事件的禁用
        return true;
    }
    //事件拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return false;//不拦截子控件的事件
    }
}
