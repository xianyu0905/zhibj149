package com.qq149.zhibj149.utils;

import android.content.Context;

/**
 * @author zhuww
 * @description: 149
 * @date :2019/5/24 22:15
 */
public class DensityUtils {

    public static int dip2px(float dip, Context ctx){
        float density = ctx.getResources().getDisplayMetrics().density;
        int px = (int) (dip*density+0.5);//4.9->4   4.1->4 四舍五入
        return px;
    }

    public static float px2dip(int px,Context ctx){
        float density = ctx.getResources().getDisplayMetrics().density;
        int dp = (int) (px/density);//4.9->4   4.1->4 四舍五入
        return dp;
    }
}
