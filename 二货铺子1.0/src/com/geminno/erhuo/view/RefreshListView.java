package com.geminno.erhuo.view;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-10下午8:33:20
 */
public class RefreshListView extends ListView {

	private View headView;
	private View footView;
	private ImageView imageView;
	private ProgressBar progressBar;
	private TextView tvRefreshTime;
	private OnRefreshCallBack refreshCallBack;
	private int headHeigt;// 头部高度
	private int footHeight;// 底部高度
	private float startY;// 开始坐标
	private float moveY;// 移动坐标
	private int headState;// 头部状态（INIT,REPAREREFRESH ISREFRESH）
	public final int INIT = 0;// 初始状态
	public final int REPAREREFRESH = 1;// 准备刷新
	public final int ISREFRESH = 2;// 正在刷新
	private RotateAnimation upAnimation;
	private RotateAnimation downAnimation;

	public RefreshListView(Context context) {
		super(context);
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnRefreshCallBack(OnRefreshCallBack refreshCallBack) {
		this.refreshCallBack = refreshCallBack;

	}
	
	// 初始化头部
	public void initHead(Context context){
		// 解析XML文件
		headView = LayoutInflater.from(context).inflate(null, null);
		// 添加头部
		addHeaderView(headView);
		// 头部高度
		headView.measure(0, 0);
		headHeigt = headView.getMeasuredHeight();
		// 隐藏头部
		headView.setPadding(0, -headHeigt, 0, 0);
		// 初始化头部控件 
		
		
		
	}
	
	// 初始化底部
	public void initfoot(Context context){
		footView = LayoutInflater.from(context).inflate(null, null);
		// 添加foot
		addFooterView(footView);
		// 获取foot高度
		footView.measure(0, 0);
		footHeight = footView.getMeasuredHeight();
		// 隐藏底部
		footView.setPadding(0, -footHeight, 0, 0);
	}
	
	public void initAnimation(Context context){
		// 中心点在自身的中心
		upAnimation = new RotateAnimation(0,180,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		upAnimation.setFillAfter(true);
		upAnimation.setDuration(100);
		downAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		downAnimation.setFillAfter(true);
		downAnimation.setDuration(1000);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			startY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			
			break;
		case MotionEvent.ACTION_UP:
			
			break;
		}
		return super.onTouchEvent(ev);
	}



	// 接口：封装下拉刷新，上拉加载的方法
	public interface OnRefreshCallBack {
		void onRefresh();// 刷新
		void onPull();// 加载
	}

}
