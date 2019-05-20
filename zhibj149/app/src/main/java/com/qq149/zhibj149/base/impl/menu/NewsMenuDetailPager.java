package com.qq149.zhibj149.base.impl.menu;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.qq149.zhibj149.R;
import com.qq149.zhibj149.base.BaseMenuDetailPager;
import com.qq149.zhibj149.domain.NewsMenu.NewsTabData;
import android.app.Activity;


import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * 菜单详情页---新闻
 * @author zhuww
 * @description: 149
 * @date :2019/5/20 19:32
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager {

    @ViewInject(R.id.vp_news_menu_detail)
    private ViewPager mViewPager;
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
    }

    class NewsMenuDetailAdapter extends PagerAdapter{

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
