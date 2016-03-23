package com.geminno.erhuo.view;

import java.util.ArrayList;

import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.ADInfo;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageCycleView extends LinearLayout {

	private Context mContext;
	// 图片轮播视图
	private CycleViewPager mBannerPager = null;
	// 滚动图片视图适配器
	private ImageCycleAdapter mAdvAdapter;
	// 图片轮播指示器控件
	private ViewGroup mGroup;
	// 图片轮播指示器
	private ImageView mImageView = null;
	// 滚动图片指示器-视图列表
	private ImageView[] mImageViews = null;
	// 图片滚动当前图片下标
	private int mImageIndex = 1;
	// 手机密度
	private float mScale;

	public ImageCycleView(Context context) {
		super(context);
	}

	public ImageCycleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		// 获取屏幕密度
		mScale = context.getResources().getDisplayMetrics().density;
		LayoutInflater.from(context)
				.inflate(R.layout.view_banner_content, this);
		// 获得viewPager
		mBannerPager = (CycleViewPager) findViewById(R.id.pager_banner);
		mBannerPager.setOnPageChangeListener(new GuidePageChangeListener());
		mBannerPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					// 开始图片滚动
					startImageTimerTask();
					break;
				default:
					// 停止图片滚动
					stopImageTimerTask();
					break;
				}
				return false;
			}
		});
		// 滚动图片下方小点
		mGroup = (ViewGroup) findViewById(R.id.viewGroup);
	}

	/**
	 * 装填图片数据
	 * 
	 * @param imageUrlList
	 * @param imageCycleViewListener
	 */
	public void setImageResources(ArrayList<ADInfo> infoList,
			ImageCycleViewListener imageCycleViewListener) {
		// 清除所有子视图
		mGroup.removeAllViews();
		// 图片广告数量
		mImageViews = new ImageView[infoList.size()];
		for (int i = 0; i < infoList.size(); i++) {
			// 创建底部小点的ImageView
			mImageView = new ImageView(mContext);
			int imageParams = (int) (mScale * 20 + 0.5f);// PX与DP转换，适应不同分辨率
			int imagePadding = (int) (mScale * 5 + 0.5f);
			LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layout.setMargins(3, 0, 3, 0);
			mImageView.setLayoutParams(layout);
			mImageViews[i] = mImageView;
			// 设置底部小点的图片
			if (i == 0) {
				mImageViews[i].setBackgroundResource(R.drawable.icon_point_pre);
			} else {
				mImageViews[i].setBackgroundResource(R.drawable.icon_point);
			}
			// 将小点加入线性布局中
			mGroup.addView(mImageViews[i]);
		}
		mAdvAdapter = new ImageCycleAdapter(mContext, infoList,
				imageCycleViewListener);
		mBannerPager.setAdapter(mAdvAdapter);
		startImageTimerTask();
	}

	/**
	 * 开始轮播(手动控制自动轮播与否，便于资源控制)
	 */
	public void startImageCycle() {
		startImageTimerTask();
	}

	/**
	 * 暂停轮播——用于节省资源
	 */
	public void pushImageCycle() {
		stopImageTimerTask();
	}

	/**
	 * 开始图片滚动任务
	 */
	private void startImageTimerTask() {
		stopImageTimerTask();
		// 图片每3秒滚动一次,定时操作
		mHandler.postDelayed(mImageTimerTask, 3000);
	}

	/**
	 * 停止图片滚动任务
	 */
	private void stopImageTimerTask() {
		// 将消息队列的Runnable对象移除
		mHandler.removeCallbacks(mImageTimerTask);
	}

	private Handler mHandler = new Handler();

	/**
	 * 图片自动轮播Task
	 */
	private Runnable mImageTimerTask = new Runnable() {

		@Override
		public void run() {
			if (mImageViews != null) {
				// 下标等于图片列表长度说明已滚动到最后一张图片,重置下标
				if ((++mImageIndex) == mImageViews.length + 1) {
					mImageIndex = 1;
				}
				mBannerPager.setCurrentItem(mImageIndex);
			}
		}
	};

	/**
	 * 轮播图片状态监听器
	 */
	private final class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int state) {
			// 滚动停止时
			if (state == ViewPager.SCROLL_STATE_IDLE)
				startImageTimerTask(); // 开始下次计时
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int index) {

			if (index == 0 || index == mImageViews.length + 1) {
				return;
			}
			mImageIndex = index;
			index -= 1;
			// 设置底部小点为选中状态的图片
			mImageViews[index].setBackgroundResource(R.drawable.icon_point_pre);
			// 将其他小点设置为非选中状态的图片
			for (int i = 0; i < mImageViews.length; i++) {
				if (index != i) {
					mImageViews[i].setBackgroundResource(R.drawable.icon_point);
				}
			}

		}

	}

	// 广告ViewPager的适配器
	private class ImageCycleAdapter extends PagerAdapter {

		// 图片视图缓存列表
		private ArrayList<ImageView> mImageViewCacheList;
		// 广告集合
		private ArrayList<ADInfo> mAdList = new ArrayList<ADInfo>();
		// 监听器
		private ImageCycleViewListener mImageCycleViewListener;
		private Context mContext;

		public ImageCycleAdapter(Context context, ArrayList<ADInfo> adList,
				ImageCycleViewListener imageCycleViewListener) {
			mContext = context;
			mAdList = adList;
			mImageCycleViewListener = imageCycleViewListener;
			mImageViewCacheList = new ArrayList<>();
		}

		@Override
		public int getCount() {
			return mAdList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			// 获得广告的Url
			String imageUrl = mAdList.get(position).getUrl();
			ImageView imageView = null;
			if (mImageViewCacheList.isEmpty()) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			} else {
				imageView = mImageViewCacheList.remove(0);
			}
			// 设置图片点击监听
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 触发图片点击事件
					mImageCycleViewListener.onImageClick(mAdList.get(position),
							position, v);
				}
			});
			imageView.setTag(imageUrl);
			container.addView(imageView);
			// 加载图片
			mImageCycleViewListener.displayImage(imageUrl, imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			ImageView view = (ImageView) object;
			container.removeView(view);
			// 将图片加入缓存集合中
			mImageViewCacheList.add(view);
		}

	}

	/**
	 * 接口，轮播控件的监听事件
	 */
	public static interface ImageCycleViewListener {

		/**
		 * 加载图片资源
		 */
		public void displayImage(String imageURL, ImageView imageView);

		/**
		 * 单击图片事件
		 */
		public void onImageClick(ADInfo info, int postion, View imageView);
	}

}
