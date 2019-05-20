package com.qq149.zhibj149.fragment;


import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.qq149.zhibj149.MainActivity;
import com.qq149.zhibj149.R;
import com.qq149.zhibj149.base.BasePager;
import com.qq149.zhibj149.base.impl.GovAffairsPager;
import com.qq149.zhibj149.base.impl.HomePager;
import com.qq149.zhibj149.base.impl.NewsCenterPager;
import com.qq149.zhibj149.base.impl.SettingPager;
import com.qq149.zhibj149.base.impl.SmartServicePager;
import com.qq149.zhibj149.view.NoScrollViewPager;

import java.util.ArrayList;


/**
 * 主页面Fragment
 * @author zhuww
 * @description: 149
 * @date :2019/5/18 11:18
 *
 */
public class ContentFragment extends BaseFragment {


    private NoScrollViewPager mViewPager;
    private ArrayList<BasePager> mPagers;//五个标签页的集合

    private RadioGroup rgGroup;
    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_content,null);
        mViewPager=view.findViewById(R.id.vp_content);
        rgGroup=view.findViewById(R.id.rg_group);
        return view;
    }

    @Override
    public void initData() {
        mPagers = new ArrayList<BasePager>();
        //添加五个标签页
        mPagers.add(new HomePager(mActivity));
        mPagers.add(new NewsCenterPager(mActivity));
        mPagers.add(new SmartServicePager(mActivity));
        mPagers.add(new GovAffairsPager(mActivity));
        mPagers.add(new SettingPager(mActivity));

        mViewPager.setAdapter(new ContentAdapter());
        //底栏标签切换监听
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_home:
                        //首页按钮
                        mViewPager.setCurrentItem(0,false);//参2：表示是否由滑动效果
                        break;
                        case R.id.rb_news:
                        //新闻中心
                        mViewPager.setCurrentItem(1,false);//参2：表示是否由滑动效果
                        break;
                         case R.id.rb_smart:
                        //智慧服务
                        mViewPager.setCurrentItem(2,false);//参2：表示是否由滑动效果
                        break;
                         case R.id.rb_gov:
                        //政务
                        mViewPager.setCurrentItem(3,false);//参2：表示是否由滑动效果
                        break;
                        case R.id.rb_setting:
                        //设置
                        mViewPager.setCurrentItem(4,false);//参2：表示是否由滑动效果
                        break;
                        default:
                            break;
                }
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BasePager pager = mPagers.get(position);
                pager.initData();

                if (position==0 || position == mPagers.size()-1){
                    //首页和设置页要禁用侧边栏
                    setSlidingMenuEnable(false);
                }else{
                    //其他页面开启页边栏
                    setSlidingMenuEnable(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //手动加载第一页数据
        mPagers.get(0).initData();
        //首页禁止侧边栏(手动加句)
        setSlidingMenuEnable(false);
    }

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

    class ContentAdapter extends PagerAdapter{

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
            BasePager pager= mPagers.get(position);
            View view = pager.mRootView;//获取当前页面对象的布局

            pager.initData();//初始化数据

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
          container.removeView((View) object);
        }
    }

    //获取新闻中心页面
    public NewsCenterPager getNewsCenterPager(){
        NewsCenterPager pager = (NewsCenterPager) mPagers.get(1);
        return pager;
    }

}
