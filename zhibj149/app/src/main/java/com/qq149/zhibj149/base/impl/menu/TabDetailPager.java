package com.qq149.zhibj149.base.impl.menu;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.qq149.zhibj149.R;
import com.qq149.zhibj149.base.BaseMenuDetailPager;
import com.qq149.zhibj149.domain.NewsMenu.NewsTabData;
import com.qq149.zhibj149.domain.NewsTabBean;
import com.qq149.zhibj149.global.GlobalConstants;


/**
 * 页签页面对象
 * @author zhuww
 * @description: 149
 * @date :2019/5/20 21:01
 */
public class TabDetailPager extends BaseMenuDetailPager {

    private NewsTabData mTabData;//单个页签的网络数据
   // private TextView view;
    @ViewInject(R.id.vp_top_news)
    private ViewPager mViewPager;
    String mUrl;


    public TabDetailPager(Activity activity, NewsTabData newsTabData) {
        super(activity);
        mTabData = newsTabData;
        mUrl = GlobalConstants.SERVER_URL + mTabData.url;
    }

    @Override
    public View initView() {
        //要给帧布局填充布局对象
       /* view = new TextView(mActivity);

        //view.setText(mTabData.title);//此处空指针

        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);*/
       View view = View.inflate(mActivity, R.layout.pager_tab_detail,null);
        ViewUtils.inject(this,view);
        return view;
    }

    @Override
    public void initData() {
        //view.setText(mTabData.title);
        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, "", new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                //请求失败
                error.printStackTrace();
                Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 3.08
     * @param result
     */
    protected  void  processDate(String result){
        Gson gson = new Gson();
        NewsTabBean newsTabBean =gson.fromJson(result, NewsTabBean.class);
    }
    //头条新闻数据适配器
    class TopNewsAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.destroyItem(container, position, object);
        }
    }

}
