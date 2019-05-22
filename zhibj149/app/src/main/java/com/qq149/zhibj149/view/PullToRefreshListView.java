package com.qq149.zhibj149.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qq149.zhibj149.R;

/**
 * @author zhuww
 * @description: 149
 * @date :2019/5/21 22:23
 */
public class PullToRefreshListView extends ListView {

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

    public PullToRefreshListView(Context context) {
        super(context);
        initHeaderView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
    }


    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh, null);
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
    public void onRefreshComplete(){
        //隐藏
        mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
        //归为原状
        mCurrentState = STATE_PULL_TO_REFRESH;
        tvTitle.setText("下拉刷新");
        pbProgressBar.setVisibility(View.INVISIBLE);
        ivArrow.setVisibility(View.VISIBLE);
    }

    /**
     * 3.定义成员变量，接收监听对象
     */
    private OnRefreshListener mListener;

    /**
     * 2.暴露接口，设置监听
     */
    public void setOnRefreshListener(OnRefreshListener listener) {

    }

    /**
     * 1.下拉刷新的回调接口
     */
    //回调接口
    public interface OnRefreshListener {
        public void onRefresh();
    }
}
