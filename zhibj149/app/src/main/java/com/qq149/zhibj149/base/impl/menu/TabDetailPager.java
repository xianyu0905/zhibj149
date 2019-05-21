package com.qq149.zhibj149.base.impl.menu;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
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
import com.qq149.zhibj149.utils.CacheUtils;
import com.qq149.zhibj149.view.TopNewsViewPager;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;


/**
 * 页签页面对象
 *
 * @author zhuww
 * @description: 149
 * @date :2019/5/20 21:01
 */
public class TabDetailPager extends BaseMenuDetailPager {

    private BitmapUtils mBitmapUtils;
    private NewsTabData mTabData;//单个页签的网络数据
    // private TextView view;

    @ViewInject(R.id.vp_top_news)
    private TopNewsViewPager mViewPager;

    @ViewInject(R.id.indicator)
    private CirclePageIndicator mIndicator;

    @ViewInject(R.id.tv_title)
    private TextView tvTitle;

    @ViewInject(R.id.lv_list)
    private ListView lvList;


    private String mUrl;

    private ArrayList<NewsTabBean.TopNews> mTopNews;
    private ArrayList<NewsTabBean.NewsData> mNewsList;
    private NewsAdapter mNewsAdapter;


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
        View view = View.inflate(mActivity, R.layout.pager_tab_detail, null);
        ViewUtils.inject(this, view);

        //给listview添加头布局
        View mHeaderView = View.inflate(mActivity,R.layout.list_item_header,null);
        ViewUtils.inject(this, mHeaderView);//此处将头布局也注入
        lvList.addHeaderView(mHeaderView);

        return view;
    }

    @Override
    public void initData() {
        //view.setText(mTabData.title);
        //加图片缓存
        String cache = CacheUtils.getCache(mUrl, mActivity);
        if (!TextUtils.isEmpty(cache)) {
            processDate(cache);
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processDate(result);

                CacheUtils.setCache(mUrl, result, mActivity);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                //请求失败
                error.printStackTrace();
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 3.08
     *
     * @param result
     */
    protected void processDate(String result) {
        Gson gson = new Gson();
        NewsTabBean newsTabBean = gson.fromJson(result, NewsTabBean.class);

        //头条新闻填充数据
        mTopNews = newsTabBean.data.topnews;

        if (mTopNews != null) {
            mViewPager.setAdapter(new TopNewsAdapter());
            mIndicator.setViewPager(mViewPager);
            mIndicator.setSnap(true);//快照方式展示

            //事件要设mIndicator
            mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset,
                                           int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //更新头条新闻标题
                    NewsTabBean.TopNews topNews =  mTopNews.get(position);
                    tvTitle.setText(topNews.title);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            //更新第一个头条新闻标题
            tvTitle.setText(mTopNews.get(0).title);
            //默认让第一个选中（解决页面销毁后重新初始化时，Indicator仍然保留上次圆点位置的bug）
            mIndicator.onPageSelected(0);

            //列表新闻设置
            mNewsList =newsTabBean.data.news;
            if (mNewsList!=null){
                mNewsAdapter = new NewsAdapter();
                lvList.setAdapter(mNewsAdapter);
            }

        }
    }

    //头条新闻数据适配器
    class TopNewsAdapter extends PagerAdapter {

        public TopNewsAdapter() {
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadFailedImage(R.drawable.topnews_item_default);//设置加载中的默认图片
        }

        @Override
        public int getCount() {
            return mTopNews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView view = new ImageView(mActivity);
            //设置默认图片
            // view.setImageResource(R.drawable.topnews_item_default);//没用
            view.setScaleType(ImageView.ScaleType.FIT_XY);//设置图片缩放方式，宽高填充父控件
            String imageUrl = mTopNews.get(position).topimage;//图片下载链接

            //下载图片--将图片设置为ImageView-避免内存溢出-缓存
            //BitmapUtils-XUtils
            mBitmapUtils.display(view, imageUrl);
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    class NewsAdapter extends BaseAdapter{

        public NewsAdapter(){
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.drawable.news_pic_default);
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHoder holder;
            if (convertView == null){
                convertView = View.inflate(mActivity,R.layout.list_item_news,null);
                holder = new ViewHoder();
                holder.ivIcon = convertView.findViewById(R.id.iv_icon);
                holder.tvTitle = convertView.findViewById(R.id.tv_title);
                holder.tvDate = convertView.findViewById(R.id.tv_date);

                convertView.setTag(holder);
            }else {
                holder = (ViewHoder) convertView.getTag();
            }
            NewsTabBean.NewsData news = (NewsTabBean.NewsData) getItem(position);
            holder.tvTitle.setText(news.title);
            holder.tvDate.setText(news.pubdate);

            mBitmapUtils.display(holder.ivIcon,news.listimage);
            return convertView;
        }
    }
    static class ViewHoder{
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvDate;
    }

}
