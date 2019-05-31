package com.qq149.zhibj149.base.impl.menu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.qq149.zhibj149.NewsDetailActivity;
import com.qq149.zhibj149.R;
import com.qq149.zhibj149.base.BaseMenuDetailPager;
import com.qq149.zhibj149.domain.NewsMenu.NewsTabData;
import com.qq149.zhibj149.domain.NewsTabBean;
import com.qq149.zhibj149.global.GlobalConstants;
import com.qq149.zhibj149.utils.CacheUtils;
import com.qq149.zhibj149.utils.PrefUtils;
import com.qq149.zhibj149.view.PullToRefreshListView;
import com.qq149.zhibj149.view.TopNewsViewPager;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;


/**
 * 页签页面对象
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
    private PullToRefreshListView lvList;


    private String mUrl;

    private ArrayList<NewsTabBean.TopNews> mTopNews;
    private ArrayList<NewsTabBean.NewsData> mNewsList;
    private NewsAdapter mNewsAdapter;
    private String mMoreUrl;
    private Handler mHandler;


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
        View mHeaderView = View.inflate(mActivity, R.layout.list_item_header, null);
        ViewUtils.inject(this, mHeaderView);//此处将头布局也注入
        lvList.addHeaderView(mHeaderView);

        /**
         * 5.前端界面设置回调（设置回调监听）
         */

        lvList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新数据
                getDataFromServer();
            }

            @Override
            public void onLoadMore() {
                //判断是否有下一页数据
                if (mMoreUrl != null) {
                    //有下一页
                    getMoreDataFromServer();
                } else {
                    //没有下一页
                    Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_SHORT).show();
                    //没有数据时，也要收起控件
                    lvList.onRefreshComplete(true);
                }
            }
        });

        //给listView监听事件
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int headerViewsCount = lvList.getHeaderViewsCount();//获取头布局数量
                position = position - headerViewsCount;//需要减去头布局的占位
                System.out.println("第" + position + "个被点击了");

                NewsTabBean.NewsData news = mNewsList.get(position);
                //read_ids:1101,1102,1105,1203
                String readIds = PrefUtils.getString(mActivity, "read_ids", "");
                if (!readIds.contains(news.id + "")) {//只有不包含当前id,才追加
                    //避免重复添加同一个id
                    readIds = readIds + news.id + ",";//1101,1102一直往后追加
                    PrefUtils.setString(mActivity, "read_ids", readIds);
                }
                //要将被点击的item的文字颜色改为灰色，局部刷新，view对象就是当前被点击的对象
                TextView tvTitle = view.findViewById(R.id.tv_title);
                tvTitle.setTextColor(Color.GRAY);
                //mNewsAdapter.notifyDataSetChanged();//全局刷新

                //跳到新闻详情页面
                Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                intent.putExtra("url", news.url);
                mActivity.startActivity(intent);

            }
        });
        return view;
    }

    /**
     * 加载下一页数据
     */
    private void getMoreDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {//在主线程
                String result = responseInfo.result;
                processDate(result, true);

                //收起下拉刷新控件
                lvList.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException error, String msg) {//在主线程
                //请求失败
                error.printStackTrace();
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();

                //收起下拉刷新控件
                lvList.onRefreshComplete(false);
            }
        });
    }

    @Override
    public void initData() {
        //view.setText(mTabData.title);
        //加图片缓存
        String cache = CacheUtils.getCache(mUrl, mActivity);
        if (!TextUtils.isEmpty(cache)) {
            processDate(cache, false);
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {//在主线程
                String result = responseInfo.result;
                processDate(result, false);

                CacheUtils.setCache(mUrl, result, mActivity);

                //收起下拉刷新控件
                lvList.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException error, String msg) {//在主线程
                //请求失败
                error.printStackTrace();
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();

                //收起下拉刷新控件
                lvList.onRefreshComplete(false);
            }
        });
    }

    /**
     * 3.08
     *
     * @param result
     */
    protected void processDate(String result, boolean isMore) {//isMore表示的是是否加载更多
        Gson gson = new Gson();
        NewsTabBean newsTabBean = gson.fromJson(result, NewsTabBean.class);

        String moreUrl = newsTabBean.data.more;
        if (!TextUtils.isEmpty(moreUrl)) {
            mMoreUrl = GlobalConstants.SERVER_URL + moreUrl;
        } else {
            mMoreUrl = null;
        }

        if (!isMore) {
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
                        NewsTabBean.TopNews topNews = mTopNews.get(position);
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
            }
            //列表新闻设置
            mNewsList = newsTabBean.data.news;
            if (mNewsList != null) {
                mNewsAdapter = new NewsAdapter();
                lvList.setAdapter(mNewsAdapter);
            }
            //头条新闻自动轮播
            if (mHandler == null) {
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        int currentItem = mViewPager.getCurrentItem();
                        currentItem++;

                        if (currentItem > mTopNews.size() - 1) {
                            currentItem = 0;//如果已经跳转到了最后一个页面，跳到第一页

                        }
                        mViewPager.setCurrentItem(currentItem);

                        mHandler.sendEmptyMessageDelayed(0, 3000);//继续发送延时3秒的消息，形成内循环
                    }
                };
                //保证启动自动轮播逻辑只执行一次
                mHandler.sendEmptyMessageDelayed(0, 3000);//发送延时3秒的消息
                mViewPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN://鼠标按下的时候
                                System.out.println("ACTION_DOWN");
                                //停止广告自动轮播
                                //删除handler的所有消息
                                mHandler.removeCallbacksAndMessages(null);

                                break;
                            case MotionEvent.ACTION_CANCEL://鼠标抬起的时候---取消事件
                                //当按下viewpager后，直接滑动listview，导致抬起事件无法响应，但会走此事件
                                System.out.println("ACTION_CANCEL");
                                //启动广告
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                                break;
                            case MotionEvent.ACTION_UP:
                                System.out.println("ACTION_UP");
                                //启动广告
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
            }

        } else {
            //加载更多数据
            ArrayList<NewsTabBean.NewsData> moreNews = newsTabBean.data.news;
            mNewsList.addAll(moreNews);//将数据追加在原来的集合中

            //刷新listView
            mNewsAdapter.notifyDataSetChanged();
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
            //mBitmapUtils.display(view, imageUrl);
            mBitmapUtils.display(view,GlobalConstants.SERVER_URL+imageUrl.substring(25));
            //mBitmapUtils.display(view, imageUrl.replace("http://10.0.2.2:8080/zhbj",GlobalConstants.SERVER_URL));
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    class NewsAdapter extends BaseAdapter {

        public NewsAdapter() {
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
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.list_item_news, null);
                holder = new ViewHoder();
                holder.ivIcon = convertView.findViewById(R.id.iv_icon);
                holder.tvTitle = convertView.findViewById(R.id.tv_title);
                holder.tvDate = convertView.findViewById(R.id.tv_date);

                convertView.setTag(holder);
            } else {
                holder = (ViewHoder) convertView.getTag();
            }
            NewsTabBean.NewsData news = (NewsTabBean.NewsData) getItem(position);
            holder.tvTitle.setText(news.title);
            holder.tvDate.setText(news.pubdate);

            //根据记录标记已读未读
            String readIds = PrefUtils.getString(mActivity, "read_ids", "");
            if (readIds.contains(news.id + "")) {
                holder.tvTitle.setTextColor(Color.GRAY);
            } else {
                holder.tvTitle.setTextColor(Color.BLACK);
            }

            //mBitmapUtils.display(holder.ivIcon, news.listimage);
            mBitmapUtils.display(holder.ivIcon,GlobalConstants.SERVER_URL+news.listimage.substring(25));
            //mBitmapUtils.display(holder.ivIcon,news.listimage.replace("http://10.0.2.2:8080/zhbj", GlobalConstants.SERVER_URL));
            return convertView;
        }
    }

    static class ViewHoder {
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvDate;
    }

}
