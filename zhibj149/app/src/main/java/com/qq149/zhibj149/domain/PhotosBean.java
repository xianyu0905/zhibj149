package com.qq149.zhibj149.domain;

import java.util.ArrayList;

/**
 * 组图对象
 * @author zhuww
 * @description: 149
 * @date :2019/5/23 19:27
 */
public class PhotosBean {

    public PhotosData data;

    public class PhotosData{
        public ArrayList<PhotoNews> news;

        public class PhotoNews{
            public int id;
            public String listimage;
            public String title;
        }

    }
}
