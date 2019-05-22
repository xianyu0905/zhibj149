package com.qq149.zhibj149.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qq149.zhibj149.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhuww
 * @description: 149
 * @date :2019/5/21 22:23
 */
public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener {

    //声明几种状态
    private static final int STATE_PULL_TO_REFRESH = 1;
    private static final int STATE_RELEASE_TO_REFRESH = 2;
    private static final int STATE_REFRESHING = 3;
    private int mCurrentState = STATE_PULL_TO_REFRESH;//当前刷新的状态

    private View mHeaderView;
    private int mHeaderViewHeight;
    private int startY = -1;

    private TextView tvTitle;
    private TextView tvTime;
    private ImageView ivArrow;

    private RotateAnimation animUp;
    private RotateAnimation animDown;
    private ProgressBar pbProgressBar;

    private View mFooterView;

    public PullToRefreshListView(Context context) {
        super(context);
        initHeaderView();
        initFooterView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
        initFooterView();
    }


    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh_header, null);
        this.addHeaderView(mHeaderView);

        tvTitle = mHeaderView.findViewById(R.id.tv_title);
        tvTime = mHeaderView.findViewById(R.id.tv_time);
        ivArrow = mHeaderView.findViewById(R.id.iv_arrow);
        pbProgressBar = mHeaderView.findViewById(R.id.pd_loading);

        //隐藏头布局
        mHeaderView.measure(0, 0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

        initAnim();
        setCurrentTime();//刚进来也得调用下，不然是默认时间
    }

    //设置刷新时间
    private void setCurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//MM 大写，从1月开始，小写的话从零开始
        String time = format.format(new Date());

        tvTime.setText(time);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //当用户按住头条新闻的viewpager进行下拉时，ACTION_DOWN会被viewpager消费掉，
                // 导致startY没有赋值，此处需要重新获取一下
                if (startY == -1) {
                    startY = (int) ev.getY();
                }

                if (mCurrentState == STATE_REFRESHING) {
                    //加载是再刷新，跳出事件
                    break;
                }

                int endY = (int) ev.getY();
                int dy = endY - startY;

                int firstVisiblePsition = getFirstVisiblePosition();//当前显示的第一个item的位置

                //必须下拉，并且当显示的第一个item
                if (dy > 0 && firstVisiblePsition == 0) {
                    int padding = dy - mHeaderViewHeight;//计算当前下拉控件的padding的值

                    mHeaderView.setPadding(0, padding, 0, 0);

                    if (padding > 0 && mCurrentState != STATE_RELEASE_TO_REFRESH) {
                        //改为松开刷新
                        mCurrentState = STATE_RELEASE_TO_REFRESH;
                        refreshState();
                    } else if (padding < 0 && mCurrentState != STATE_PULL_TO_REFRESH) {
                        //改为下拉刷新
                        mCurrentState = STATE_PULL_TO_REFRESH;
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP://抬起手
                startY = -1;

                if (mCurrentState == STATE_RELEASE_TO_REFRESH) {
                    mCurrentState = STATE_REFRESHING;
                    refreshState();
                    //完整展示头部
                    mHeaderView.setPadding(0, 0, 0, 0);

                    //4.进行回调
                    if (mListener != null) {
                        mListener.onRefresh();
                    }

                } else if (mCurrentState == STATE_PULL_TO_REFRESH) {
                    //隐藏头布局
                    mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
                }

                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 初始化箭头动画
     * 动画效果上拉的时候箭头旋转180°
     */
    private void initAnim() {
        //向上的动画
        animUp = new RotateAnimation(0, -180, Animation.
                RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(200);
        animUp.setFillAfter(true);
        //向下的动画
        animDown = new RotateAnimation(-180, 0, Animation.
                RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(200);
        animUp.setFillAfter(true);
    }

    /**
     * 根据当前状态刷新界面
     */
    private void refreshState() {
        switch (mCurrentState) {
            case STATE_PULL_TO_REFRESH:
                tvTitle.setText("下拉刷新");
                ivArrow.startAnimation(animDown);
                pbProgressBar.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                break;
            case STATE_RELEASE_TO_REFRESH:
                tvTitle.setText("松开刷新");
                ivArrow.startAnimation(animUp);
                pbProgressBar.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                break;
            case STATE_REFRESHING:
                tvTitle.setText("正在刷新...");
                //必须清除箭头动画，否则无法隐藏
                ivArrow.clearColorFilter();
                pbProgressBar.setVisibility(View.VISIBLE);
                ivArrow.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 刷新结束，收起控件
     */
    public void onRefreshComplete(boolean sucess){

        if (sucess){//只有刷新成功之后才更新时间
            //隐藏
            mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
            //归为原状
            mCurrentState = STATE_PULL_TO_REFRESH;
            tvTitle.setText("下拉刷新");
            pbProgressBar.setVisibility(View.INVISIBLE);
            ivArrow.setVisibility(View.VISIBLE);

            setCurrentTime();
        }

    }
    /**
     * 初始化脚步局
     */
    private void initFooterView(){
        mFooterView = View.inflate(getContext(),R.layout.pull_to_refresh_footer,null);
        this.addFooterView(mFooterView);

        mFooterView.measure(0,0);
        int mFooterViewHeight = mFooterView.getMeasuredHeight();

        //隐藏脚步局
        mFooterView.setPadding(0,-mFooterViewHeight,0,0);

        //给listView 设置滑动监听
        this.setOnScrollListener(this);//滑动监听
    }

    /**
     * 3.定义成员变量，接收监听对象
     */
    private OnRefreshListener mListener;

    /**
     * 2.暴露接口，设置监听
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener=listener;
    }

    /**
     * 1.下拉刷新的回调接口
     */
    //回调接口
    public interface OnRefreshListener {
        public void onRefresh();
    }

    //滑动状态发生变化
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE){//空闲状态
            int lastVisiblePosition = getLastVisiblePosition();

            if (lastVisiblePosition == getCount()-1){
                //到底了
                System.out.println("加载更多...");
                mFooterView.setPadding(0,0,0,0);//显示加载更多的布局

                setSelection(getCount()-1);//将listViem 显示在最后一个item上，从而加载会直接展示出来，无需手动滑动
            }

        }
    }
    //滑动过程回顾
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
