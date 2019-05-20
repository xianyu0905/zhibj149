package com.qq149.zhibj149;


import android.os.Bundle;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.qq149.zhibj149.fragment.ContentFragment;
import com.qq149.zhibj149.fragment.LeftMenuFragment;

/*主界面*/
/*6.侧边栏*/
/**
 * 使用slidingmenu
 * 1.引入slidingFragmentActivity
 * 3.onCreate改成public
 * 4.调用相关的api
 * */
public class MainActivity extends SlidingFragmentActivity {

    private static final String TAG_LEFT_MENU = "TAG_LEFT_MENU";
    private static final String TAG_CONTENT = "TAG_CONTENT";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题，必须在setContentView前面
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //设置侧边栏
        setBehindContentView(R.layout.left_menu);
        SlidingMenu slidingMenu = getSlidingMenu();
        //设置右边侧边栏
       // slidingMenu.setSecondaryMenu(R.layout.right_menu);
       // slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);//设置模式，左右都有侧边栏

        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN); //设置全屏触模
        //设置侧边栏的宽度
        slidingMenu.setBehindOffset(600);//设置屏幕预留200像素宽度
        initFragment();
    }
    /**
     * 初始化fragment
     * **/
    private void initFragment(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();//开启事务
        transaction.replace(R.id.fl_left_menu,new LeftMenuFragment(),TAG_LEFT_MENU);//用fragment
        // 替换帧布局：参1：帧布局容器的id;参2：是要替换的id;参3：标记
        transaction.replace(R.id.fl_main,new ContentFragment(),TAG_CONTENT);
        transaction.commit();//提交事务


    }
    //获取侧边栏fragment对象
    public LeftMenuFragment getLeftMenuFragment(){
        FragmentManager fm = getSupportFragmentManager();
        LeftMenuFragment fragment = (LeftMenuFragment) fm.findFragmentByTag(TAG_LEFT_MENU);//根据标记找到对应的fragment
        return fragment;
    }
}
