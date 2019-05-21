package com.qq149.zhibj149.domain;

import android.app.Activity;

import java.util.ArrayList;

/**
 * @author zhuww
 * @description: 149
 * @date :2019/5/21 2:10
 */
public class NewsTabBean {
    public NewsTab data;

    public class NewsTab{
        public String more;
        public ArrayList<NewsData> news;
        public ArrayList<TopNews> topnews;
    }
    //新闻列表对象
    public  class NewsData{
        public int id;
        public String listimage;
        public String pubdate;
        public String title;
        public String type;
        public String url;
    }
    //头条新闻
    public  class TopNews{
        public int id;
        public String topimage;
        public String pubdate;
        public String title;
        public String type;
        public String url;
    }
}
