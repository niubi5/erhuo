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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * @author LuoShiHeng
 * @version 创建时间:2016-3-27下午8:51:21
 */
public class PullToFreshListView extends ListView implements OnScrollListener {

	private View headView;
	private ImageView imageView;
	private ProgressBar progressBar;
	private TextView tvRefreshState;
	private OnPullTofreshCallBack OnPullTofreshCallBack;
	private int headHeight;// 头部高度
	private float startY;// 开始坐标
	private float moveY;// 移动坐标
	
	private RotateAnimation upAnimation;
	private RotateAnimation downAnimation;
	
	private int headState;// 头部状态（INIT,REPAREREFRESH ISREFRESH）
	public final int INIT = 0;// 初始状态
	public final int PREPAREREFRESH = 1;// 准备刷新
	public final int ISREFRESHING = 2;// 正在刷新
	
	private int firstVisibleItem;// 第一条可见的位置
	private boolean loading = false;// 正在加载

	public PullToFreshListView(Context context) {
		super(context);
		initHead(context);
//		initAnimation(context);
		setOnScrollListener(this);
	}

	public PullToFreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHead(context);
//		initAnimation(context);
		setOnScrollListener(this);
	}

	public PullToFreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHead(context);
//		initAnimation(context);
		setOnScrollListener(this);
	}

	private void initHead(Context context) {
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

	private void initAnimation(Context context) {
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

	public void setOnPullToFresh(OnPullTofreshCallBack OnPullTofreshCallBack) {
		this.OnPullTofreshCallBack = OnPullTofreshCallBack;
	}

	// 接口：封装下拉刷新，上拉加载的方法
	public interface OnPullTofreshCallBack {
		void onRefresh();// 刷新
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			startY = ev.getY();
//			break;
//		case MotionEvent.ACTION_MOVE:
//			if (headState == ISREFRESHING) {
//				// 如果是正在刷新 不做任何操作
//				return false;
//			}
//			moveY = ev.getY();
//			// 如果第一条可见并且是向下拉
//			if (firstVisibleItem == 0 && (moveY > startY)) {
//				int paddingHeight = (int) (-headHeight + (moveY - startY));
//				// 如果拉出来的距离》=头部高度，状态改变
//				// 拉出来的瞬间，状态改变
//				if (paddingHeight >= 0 && headState == INIT) {
//					// 状态改变 ==> 准备刷新
//					headState = PREPAREREFRESH;
////					changeState();// 改变控件相关属性
//				} else if (headState == PREPAREREFRESH && paddingHeight < 0) {
//					// 准备刷新==》初始状态
//					headState = INIT;
////					changeState();
//				}
//				headView.setPadding(0, paddingHeight, 0, 0);
//				return false;
//			}
//			break;
//		case MotionEvent.ACTION_UP:
//			// 如果当前距离 < 头部高度 ， 则缩回去
//			if (headState == INIT) {
//				headView.setPadding(0, -headHeight, 0, 0);
//			} else if (headState == PREPAREREFRESH) {
//				// 如果距离 >= 头部高度 ，则准备刷新 ==》 正在刷新
//				headState = ISREFRESHING;
////				changeState();
//				headView.setPadding(0, 0, 0, 0);
//				// 开始刷新操作：回调
//				if (OnPullTofreshCallBack != null) {
//					OnPullTofreshCallBack.onRefresh();
//					return true;
//				}
//			}
//			break;
//		default:
//			break;
//		}
//		return super.onTouchEvent(ev);
//	}
	
	// 改变状态，界面显示内容跟着改变
//		private void changeState() {
//			switch (headState) {
//			case INIT:
//				progressBar.setVisibility(View.INVISIBLE);
//				imageView.setVisibility(View.VISIBLE);
//				// 给imageView设置动画
//				imageView.startAnimation(downAnimation);// 设置箭头朝上
//				tvRefreshState.setText("下拉刷新");
//				break;
//			case PREPAREREFRESH:
//				progressBar.setVisibility(View.INVISIBLE);
//				imageView.setVisibility(View.VISIBLE);
//				imageView.startAnimation(upAnimation);// 设置箭头朝下
//				tvRefreshState.setText("释放刷新");
//				break;
//			case ISREFRESHING:
//				progressBar.setVisibility(View.VISIBLE);
//				imageView.setVisibility(View.INVISIBLE);
//				imageView.clearAnimation();// 清除动画
//				tvRefreshState.setText("正在刷新");
//				break;
//			}
//		}
//		
//		// 完成刷新后
//		public void completeRefresh() {
//			// 改变padding值
//			headView.setPadding(0, -headHeight, 0, 0);
//			// 改变成初始状态
//			headState = INIT;
//			changeState();
//		}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
	}

}
