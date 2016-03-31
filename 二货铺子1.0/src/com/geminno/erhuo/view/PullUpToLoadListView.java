package com.geminno.erhuo.view;

import com.geminno.erhuo.R;
import com.geminno.erhuo.view.RefreshListView.OnRefreshCallBack;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-29下午12:31:24
 */
public class PullUpToLoadListView extends ListView implements OnScrollListener {

	private View footView;
	private int footHeight;// 底部高度
	private boolean loading = false;// 正在加载

	private OnPullUpToLoadCallBack pullUpToLoadCallback;

	public PullUpToLoadListView(Context context) {
		super(context);
		initFoot(context);
		setOnScrollListener(this);

	}

	public PullUpToLoadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFoot(context);
		setOnScrollListener(this);
	}

	public PullUpToLoadListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initFoot(context);
		setOnScrollListener(this);
	}

	// 初始化底部
	public void initFoot(Context context) {
		footView = LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_foot, null);
		// 添加foot
		addFooterView(footView);
		// 获取foot高度
		footView.measure(0, 0);
		footHeight = footView.getMeasuredHeight();
		// 隐藏底部
		footView.setPadding(0, -footHeight, 0, 0);
	}

	// 传接口的实现类
	public void setOnPullToLoadCallback(OnPullUpToLoadCallBack pullUpToLoadCallback) {
		this.pullUpToLoadCallback = pullUpToLoadCallback;
	}

	// 接口：封装上拉加载的方法
	public interface OnPullUpToLoadCallBack {
		void onPull();// 加载
	}

	// 加载完成：隐藏foot，loading = false
	public void completePull() {
		footView.setPadding(0, -footHeight, 0, 0);
		loading = false;
	}
	

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 最后一条记录可见，并且手在上面 || 手拿掉 ==》 加载操作&当前不在加载
		if (!loading && getLastVisiblePosition() == getCount() - 1) {
			// 滚动装态
			if (scrollState == SCROLL_STATE_IDLE
					|| scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				// foot显示
				footView.setPadding(0, 0, 0, 0);
				loading = true;
				// 调用onpull方法
				if (pullUpToLoadCallback != null) {
					pullUpToLoadCallback.onPull();
				}
			}

		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

}
