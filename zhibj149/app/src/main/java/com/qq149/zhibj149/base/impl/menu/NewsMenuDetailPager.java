package com.qq149.zhibj149.base.impl.menu;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.qq149.zhibj149.MainActivity;
import com.qq149.zhibj149.R;
import com.qq149.zhibj149.base.BaseMenuDetailPager;
import com.qq149.zhibj149.domain.NewsMenu.NewsTabData;
import com.viewpagerindicator.TabPageIndicator;

import android.app.Activity;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * 菜单详情页---新闻
 *
 * ViewPagerIndicator使用流程：
 * 1.引入库
 * 2.解决support-v4冲突（让两个版本一致）
 * 3.从例子中拷贝布局文件
 * 4.从例子程序中拷贝相关代码（指示器和viewpager绑定；重写getPageTitle）
 * 5.在清单文件中增加样式
 * 6.修改背景颜色为白色
 *
 * @author zhuww
 * @description: 149
 * @date :2019/5/20 19:32
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements
        ViewPager.OnPageChangeListener {

    @ViewInject(R.id.vp_news_menu_detail)
    private ViewPager mViewPager;

    @ViewInject(R.id.indicator)
    private TabPageIndicator mIndicator;

    private ArrayList<NewsTabData> mTabData;// 页签网络数据
    private ArrayList<TabDetailPager> mPagers;// 页签页面集合

    public NewsMenuDetailPager(Activity activity, ArrayList<NewsTabData> children) {
        super(activity);
        mTabData =children;
    }

    @Override
    public View initView() {
        /*TextView view = new TextView(mActivity);
        view.setText("菜单详情页---新闻");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);*/
        View view = View.inflate(mActivity, R.layout.pager_news_menu_detail,null);
        ViewUtils.inject(this,view);
        return view;
    }

    @Override
    public void initData() {
        //初始化页签
        mPagers = new ArrayList<TabDetailPager>();
        for (int i = 0;i < mTabData.size();i++){
           TabDetailPager pager = new TabDetailPager(mActivity,mTabData.get(i));
           mPagers.add(pager);
         }
        mViewPager.setAdapter(new NewsMenuDetailAdapter());
        mIndicator.setViewPager(mViewPager);//将viewpager和指示器绑在一起，注意：必须在viewpager设置完数据之后绑定

        //设置页面滑动监听
        //mViewPager.setOnPageChangeListener(this);
        mIndicator.setOnPageChangeListener(this);//此处必须给指示器设置监听，不能设置给viewpager
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        System.out.println("当前位置："+position);
        if (position == 0){
            //开启侧边栏
            setSlidingMenuEnable(true);
        }else{
            //禁用侧边栏
            setSlidingMenuEnable(false);
        }
    }

    /**
     * 开启或禁用侧边栏
     * @param enable
     */
    private void setSlidingMenuEnable(boolean enable) {
        //获取侧边栏对象
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        if (enable){
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }else{
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }
    //btn_next按钮监听事件,下面的写法就不用加onClick属行
    @OnClick(R.id.btn_next)
    public void nextPage(View view){
        //跳到下一个页面
        int currentItem = mViewPager.getCurrentItem();
        currentItem++;
        mViewPager.setCurrentItem(currentItem);
    }
    @Override
    public void onPageScrollStateChanged(int i) {

    }

    class NewsMenuDetailAdapter extends PagerAdapter{

        //指定指示器的标题


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            NewsTabData data = mTabData.get(position);
            return data.title;
        }

        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            TabDetailPager pager = mPagers.get(position);

            View view = pager.mRootView;
            container.addView(view);

            pager.initData();

            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
           container.removeView((View) object);
        }
    }
}
