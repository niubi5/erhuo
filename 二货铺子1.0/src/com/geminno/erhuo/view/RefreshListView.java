package com.geminno.erhuo.view;

import com.geminno.erhuo.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-10下午8:33:20
 */
public class RefreshListView extends ListView implements OnScrollListener {

	private View headView;
	private View footView;
	private ImageView imageView;
	private ProgressBar progressBar;
	private TextView tvRefreshState;
	private OnRefreshCallBack refreshCallBack;
	private int headHeight;// 头部高度
	private int footHeight;// 底部高度
	private float startY;// 开始坐标
	private float moveY;// 移动坐标

	private int headState;// 头部状态（INIT,REPAREREFRESH ISREFRESH）
	public final int INIT = 0;// 初始状态
	public final int PREPAREREFRESH = 1;// 准备刷新
	public final int ISREFRESHING = 2;// 正在刷新

	private RotateAnimation upAnimation;
	private RotateAnimation downAnimation;
	private int firstVisibleItem;// 第一条可见的位置
	private boolean loading = false;// 正在加载

	public RefreshListView(Context context) {
		super(context);
		initHead(context);
		initFoot(context);
		initAnimation(context);
		setOnScrollListener(this);
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHead(context);
		initFoot(context);
		initAnimation(context);
		setOnScrollListener(this);
	}

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHead(context);
		initFoot(context);
		initAnimation(context);
		setOnScrollListener(this);
	}

	// 传接口的实现类
	public void setOnRefreshCallBack(OnRefreshCallBack refreshCallBack) {
		this.refreshCallBack = refreshCallBack;

	}

	// 初始化头部
	public void initHead(Context context) {
		// 解析XML文件
		headView = LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_head, null);
		// 添加头部
		addHeaderView(headView);
		// 头部高度
		headView.measure(0, 0);
		headHeight = headView.getMeasuredHeight();
		// 隐藏头部
		headView.setPadding(0, -headHeight, 0, 0);
		// 初始化头部控件
		imageView = (ImageView) headView.findViewById(R.id.iv_refresher);// 箭头
		progressBar = (ProgressBar) headView.findViewById(R.id.refresher);// progressBar
		tvRefreshState = (TextView) headView
				.findViewById(R.id.tv_refreshertext);
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

	// 初始化动画
	public void initAnimation(Context context) {
		// 中心点在自身的中心
		upAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		upAnimation.setFillAfter(true);
		upAnimation.setDuration(100);
		downAnimation = new RotateAnimation(-180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		downAnimation.setFillAfter(true);
		downAnimation.setDuration(1000);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (headState == ISREFRESHING) {
				// 如果是正在刷新 不做任何操作
				return false;
			}
			moveY = ev.getY();
			// 如果第一条可见并且是向下拉
			if (firstVisibleItem == 0 && (moveY > startY)) {
				int paddingHeight = (int) (-headHeight + (moveY - startY));
				// 如果拉出来的距离》=头部高度，状态改变
				// 拉出来的瞬间，状态改变
				if (paddingHeight >= 0 && headState == INIT) {
					// 状态改变 ==> 准备刷新
					headState = PREPAREREFRESH;
					changeState();// 改变控件相关属性
				} else if (headState == PREPAREREFRESH && paddingHeight < 0) {
					// 准备刷新==》初始状态
					headState = INIT;
					changeState();
				}
				headView.setPadding(0, paddingHeight, 0, 0);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			// 如果当前距离 < 头部高度 ， 则缩回去
			if (headState == INIT) {
				headView.setPadding(0, -headHeight, 0, 0);
			} else if (headState == PREPAREREFRESH) {
				// 如果距离 >= 头部高度 ，则准备刷新 ==》 正在刷新
				headState = ISREFRESHING;
				changeState();
				headView.setPadding(0, 0, 0, 0);
				// 开始刷新操作：回调
				if (refreshCallBack != null) {
					refreshCallBack.onRefresh();
				}
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	// 改变状态，界面显示内容跟着改变
	private void changeState() {
		switch (headState) {
		case INIT:
			progressBar.setVisibility(View.INVISIBLE);
			imageView.setVisibility(View.VISIBLE);
			// 给imageView设置动画
			imageView.startAnimation(downAnimation);// 设置箭头朝上
			tvRefreshState.setText("下拉刷新");
			break;
		case PREPAREREFRESH:
			progressBar.setVisibility(View.INVISIBLE);
			imageView.setVisibility(View.VISIBLE);
			imageView.startAnimation(upAnimation);// 设置箭头朝下
			tvRefreshState.setText("释放刷新");
			break;
		case ISREFRESHING:
			progressBar.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.INVISIBLE);
			imageView.clearAnimation();// 清除动画
			tvRefreshState.setText("正在刷新");
			break;
		}
	}

	// 接口：封装下拉刷新，上拉加载的方法
	public interface OnRefreshCallBack {
		void onRefresh();// 刷新

		void onPull();// 加载
	}

	// 完成刷新后
	public void completeRefresh() {
		// 改变padding值
		headView.setPadding(0, -headHeight, 0, 0);
		// 改变成初始状态
		headState = INIT;
		changeState();
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
				if (refreshCallBack != null) {
					refreshCallBack.onPull();
				}
			}

		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
	}

}
