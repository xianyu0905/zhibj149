package com.qq149.zhibj149.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 头条新闻自定义viewpager
 *
 * @author zhuww
 * @description: 149
 * @date :2019/5/21 17:30
 */
public class TopNewsViewPager extends ViewPager {
    private int startX;
    private int startY;

    public TopNewsViewPager(@NonNull Context context) {
        super(context);
    }

    public TopNewsViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 页面拦截情况
     * 1.上下滑动需要拦截
     * 2.向右滑动并且当前是第一个页面，需要拦截
     * 3.向左滑动并且当前是最后一个页面，需要拦截
     *
     * @param ev
     * @return
     */
    //拦截
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //先不拦截
        getParent().requestDisallowInterceptTouchEvent(true);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN://按下
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE://移动

                int endX = (int) ev.getX();
                int endY = (int) ev.getY();

                //水平方向
                int dx = endX - startX;
                int dy = endY - startY;

                if (Math.abs(dy) < Math.abs(dx)) {
                    int currentItem = getCurrentItem();
                    //左右滑动
                    if (dx > 0) {
                        //向右滑

                        if (currentItem == 0) {
                            //第一个页面，需要拦截
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    } else {
                        //向左滑
                        int count = getAdapter().getCount();//item总数
                        if (currentItem == count - 1) {
                            //最后一个页面，需要拦截
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }

                    }
                } else {
                    //上下滑动，不拦截
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
