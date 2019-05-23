package com.qq149.zhibj149.base.impl.menu;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
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
import com.qq149.zhibj149.domain.PhotosBean;
import com.qq149.zhibj149.global.GlobalConstants;
import com.qq149.zhibj149.utils.CacheUtils;

import java.util.ArrayList;

/**
 * 菜单详情页---组图
 * @author zhuww
 * @description: 149
 * @date :2019/5/20 19:32
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager {

    @ViewInject(R.id.lv_photo)
    private ListView lvPhoto;
    @ViewInject(R.id.gv_photo)
    private GridView gvPhoto;
    private ArrayList<PhotosBean.PhotosData.PhotoNews> mNewsList;

    public PhotosMenuDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        /*TextView view = new TextView(mActivity);
        view.setText("菜单详情页---组图");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);*/

        View view = View.inflate(mActivity, R.layout.pager_photos_menu_detail,null);
        ViewUtils.inject(this,view);
        return view;
    }

    @Override
    public void initData() {
        String cache = CacheUtils.getCache(GlobalConstants.PHOTOS_URL,mActivity);
        if (!TextUtils.isEmpty(cache)){
            processData(cache);
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();

        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.PHOTOS_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result);

                CacheUtils.setCache(GlobalConstants.PHOTOS_URL,result,mActivity);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                //请求失败
                error.printStackTrace();
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processData(String result) {
        Gson gson = new Gson();
        PhotosBean photosBean = gson.fromJson(result, PhotosBean.class);

        mNewsList = photosBean.data.news;

        lvPhoto.setAdapter(new PhotoAdapter());

    }

    class PhotoAdapter extends BaseAdapter{

        private BitmapUtils mBitmapUtils;
        public PhotoAdapter(){
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.drawable.pic_item_list_default);
        }
        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public PhotosBean.PhotosData.PhotoNews getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null){
                convertView = View.inflate(mActivity,R.layout.list_item_photos,null);
                holder = new ViewHolder();
                holder.ivPic = convertView.findViewById(R.id.iv_pic);
                holder.tvTitle = convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            PhotosBean.PhotosData.PhotoNews item = getItem(position);

            holder.tvTitle.setText(item.title);
            mBitmapUtils.display(holder.ivPic,item.listimage);
            return convertView;
        }
    }

    static class ViewHolder{
        public ImageView ivPic;
        public TextView tvTitle;

    }
}
