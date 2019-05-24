package com.qq149.zhibj149;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;


import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

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
    private static final int REQUEST_CODE = 1;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }else {
            Log.e("读写权限","获取到了");
        }

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
    //获取-侧边栏-fragment对象
    public LeftMenuFragment getLeftMenuFragment(){
        FragmentManager fm = getSupportFragmentManager();
        LeftMenuFragment fragment = (LeftMenuFragment) fm.findFragmentByTag(TAG_LEFT_MENU);//根据标记找到对应的fragment
        return fragment;
    }
    //获取-主页-fragment对象
    public ContentFragment getContentFragment(){
        FragmentManager fm = getSupportFragmentManager();
        ContentFragment fragment = (ContentFragment) fm.findFragmentByTag(TAG_CONTENT);//根据标记找到对应的fragment
        return fragment;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("读写权限","获取到了");
            }else {
                Toast.makeText(this,"你拒绝了读写权限，不能进行文件读取",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
