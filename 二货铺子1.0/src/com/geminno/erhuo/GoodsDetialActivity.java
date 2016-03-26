package com.geminno.erhuo;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.geminno.erhuo.GoodsDetialActivity.AsyncImageLoader.ImageCallback;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Users;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.GoodsDetialActivity.AsyncImageLoader.ImageCallback;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Users;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GoodsDetialActivity extends Activity {
	public static Activity goodsDetialActivity;
	private ViewPager viewPager;
	private ArrayList<View> pageview;

	private ImageView image;
	private View item;
	private MyAdapter adapter;
	private ImageView[] indicator_imgs;// 存放引到图片数组
	private LayoutInflater inflater;
	private String[] urls;
	private Users user;
	private Goods goods;
	private TextView tvGoodBrief;
	private TextView tvGoodName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_goods_detial);
		goodsDetialActivity = this;
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		viewPager = (ViewPager) findViewById(R.id.vp_goods_images);
		List<View> list = new ArrayList<View>();
		inflater = LayoutInflater.from(this);
		// 获得当前商品的id
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		user = (Users) bundle.getSerializable("user");
		goods = (Goods) bundle.getSerializable("goods");
		List<String> goodUrl = bundle.getStringArrayList("urls");
		urls = new String[goodUrl.size()];
		for (int i = 0; i < goodUrl.size(); i++) {
			urls[i] = goodUrl.get(i);
		}
		indicator_imgs = new ImageView[goodUrl.size()];
		/**
		 * 创建多个item （每一条viewPager都是一个item） 从服务器获取完数据（如文章标题、url地址） 后，再设置适配器
		 */
		for (int i = 0; i < urls.length; i++) {
			item = inflater.inflate(R.layout.goods_images_item, null);
			list.add(item);
		}
		// 创建适配器， 把组装完的组件传递进去
		adapter = new MyAdapter(list);
		viewPager.setAdapter(adapter);

		// 绑定动作监听器：如翻页的动画
		viewPager.setOnPageChangeListener(new MyListener());
		initData();
		initIndicator();
	}

	public void initData(){
		ImageView ivHead = (ImageView) findViewById(R.id.iv_user_head);
		TextView tvUserName = (TextView) findViewById(R.id.tv_user_name);
		TextView tvUserLocation = (TextView) findViewById(R.id.tv_user_location);
		TextView tvGoodPrice = (TextView) findViewById(R.id.tv_goods_price);
		TextView tvGoodOldPrice = (TextView) findViewById(R.id.tv_goods_oldprice);
		tvGoodName = (TextView) findViewById(R.id.tv_goods_name);
		TextView tvGoodTime = (TextView) findViewById(R.id.tv_goods_time);
		tvGoodBrief = (TextView) findViewById(R.id.tv_goods_brief);
		Log.i("imagelocation", user.getPhoto());
		if(user.getPhoto() != null && !user.getPhoto().equals("")){
			Properties prop = new Properties();
			String headUrl = null;
			try {
				prop.load(PublishGoodsActivity.class.getResourceAsStream("/com/geminno/erhuo/utils/url.properties"));
				headUrl = prop.getProperty("headUrl");
			} catch (IOException e) {
				e.printStackTrace();
			}
			final String userHeadUrl = headUrl + user.getPhoto();
			ImageLoader.getInstance().displayImage(userHeadUrl, ivHead);
		}
		tvUserName.setText(user.getName());
		int instance = Distance(goods.getLongitude(), goods.getLatitude(), MyApplication.getLocation().getLongitude(), MyApplication.getLocation().getLatitude());
		tvUserLocation.setText(instance >= 100 ? ("距我:" + instance/1000+"km") : ("距我:" + instance+"m"));
		tvGoodPrice.setText("¥"+goods.getSoldPrice());
		tvGoodOldPrice.setText("原价:"+goods.getBuyPrice());
		tvGoodName.setText(goods.getName());
		tvGoodTime.setText((goods.getPubTime().substring(2,10)));
		tvGoodBrief.setText(goods.getImformation());
	}

	//
	/**
	 * 计算地球上任意两点(经纬度)距离
	 * 
	 * @param long1
	 *            第一点经度
	 * @param lat1
	 *            第一点纬度
	 * @param long2
	 *            第二点经度
	 * @param lat2
	 *            第二点纬度
	 * @return 返回距离 单位：米
	 */
	public static int Distance(double long1, double lat1, double long2,
			double lat2) {
		double a, b, R;
		R = 6378137; // 地球半径
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2
				* R
				* Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
						* Math.cos(lat2) * sb2 * sb2));
		return (int) d;
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(tvGoodName.getText().toString());
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText(tvGoodBrief.getText().toString());
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(this);
	}

	// 点击事件
	public void onclick(View v) {
		switch (v.getId()) {
		case R.id.iv_share_report:
			showPopupWindow(v);
			break;
		case R.id.iv_detial_return:
			finish();
			break;
		case R.id.btn_buy:
			if(goods.getState() == 2){
				Toast.makeText(GoodsDetialActivity.this, "该商品已被下单", Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent(this, BuyGoodsActivity.class);
				intent.putExtra("user", user);
				intent.putExtra("good", goods);
				intent.putExtra("url", urls);
				startActivity(intent);
			}
		default:
			break;
		}
	}

	// 显示popupwindow
	private void showPopupWindow(View view) {

		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(this).inflate(
				R.layout.pop_window, null);
		// 设置按钮的点击事件
		TextView tvShare = (TextView) contentView.findViewById(R.id.tv_share);
		TextView tvReport = (TextView) contentView.findViewById(R.id.tv_report);
		tvShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Toast.makeText(GoodsDetialActivity.this, "分享",
				// Toast.LENGTH_SHORT).show();
				showShare();
			}
		});
		tvReport.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(GoodsDetialActivity.this, "举报",
//						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(GoodsDetialActivity.this,ReportGoodActivity.class);
				intent.putExtra("goodId", goods.getId());
				startActivity(intent);
			}
		});

		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

		popupWindow.setTouchable(true);

		popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				Log.i("mengdd", "onTouch : ");

				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});

		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.round_box));

		// 设置好参数之后再show
		popupWindow.showAsDropDown(view);
		//popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

	}

	/**
	 * 初始化引导图标 动态创建多个小圆点，然后组装到线性布局里
	 */
	private void initIndicator() {

		ImageView imgView;
		View v = findViewById(R.id.indicator);// 线性水平布局，负责动态调整导航图标

		for (int i = 0; i < urls.length; i++) {
			imgView = new ImageView(this);
			LinearLayout.LayoutParams params_linear = new LinearLayout.LayoutParams(
					10, 10);
			params_linear.setMargins(7, 10, 7, 10);
			imgView.setLayoutParams(params_linear);
			indicator_imgs[i] = imgView;
			if (i == 0) { // 初始化第一个为选中状态

				indicator_imgs[i].setBackgroundResource(R.drawable.round_point);
			} else {
				indicator_imgs[i]
						.setBackgroundResource(R.drawable.round_point_normal);
			}
			((ViewGroup) v).addView(indicator_imgs[i]);
		}

	}

	/**
	 * 适配器，负责装配 、销毁 数据 和 组件 。
	 */
	private class MyAdapter extends PagerAdapter {

		private List<View> mList;

		private AsyncImageLoader asyncImageLoader;

		public MyAdapter(List<View> list) {
			mList = list;
			asyncImageLoader = new AsyncImageLoader();
		}

		/**
		 * Return the number of views available.
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		/**
		 * Remove a page for the given position. 滑动过后就销毁 ，销毁当前页的前一个的前一个的页！
		 * instantiateItem(View container, int position) This method was
		 * deprecated in API level . Use instantiateItem(ViewGroup, int)
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(mList.get(position));

		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		/**
		 * Create the page for the given position.
		 */
		@SuppressLint("NewApi")
		@Override
		public Object instantiateItem(final ViewGroup container,
				final int position) {
			Drawable cachedImage = asyncImageLoader.loadDrawable(
					urls[position], new ImageCallback() {

						@SuppressLint("NewApi")
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {

							View view = mList.get(position);
							image = ((ImageView) view
									.findViewById(R.id.iv_goods_image));
							// image.setBackground(imageDrawable);
							image.setImageDrawable(null);
							image.setImageDrawable(imageDrawable);
							// image.setScaleType(ScaleType.CENTER_CROP);
							container.removeView(mList.get(position));
							container.addView(mList.get(position));
							// adapter.notifyDataSetChanged();

						}
					});

			View view = mList.get(position);
			image = ((ImageView) view.findViewById(R.id.iv_goods_image));
			// image.setBackground(cachedImage);
			image.setImageDrawable(cachedImage);

			container.removeView(mList.get(position));
			container.addView(mList.get(position));
			// adapter.notifyDataSetChanged();

			return mList.get(position);

		}
	}

	/**
	 * 动作监听器，可异步加载图片
	 * 
	 */
	private class MyListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int state) {
			// TODO Auto-generated method stub
			if (state == 0) {
				// new MyAdapter(null).notifyDataSetChanged();
				Log.i("ViewPagerMyListener", "onPageScrollStateChanged");
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			Log.i("ViewPagerMyListener", "onPageScrolled");

		}

		@Override
		public void onPageSelected(int position) {

			// 改变所有导航的背景图片为：未选中
			for (int i = 0; i < indicator_imgs.length; i++) {
				Log.i("ViewPagerMyListener", "onPageSelected");
				indicator_imgs[i]
						.setBackgroundResource(R.drawable.round_point_normal);

			}

			// 改变当前背景图片为：选中
			indicator_imgs[position]
					.setBackgroundResource(R.drawable.round_point);
		}

	}

	/**
	 * 异步加载图片
	 */
	static class AsyncImageLoader {

		// 软引用，使用内存做临时缓存 （程序退出，或内存不够则清除软引用）
		private HashMap<String, SoftReference<Drawable>> imageCache;

		public AsyncImageLoader() {
			imageCache = new HashMap<String, SoftReference<Drawable>>();
		}

		/**
		 * 定义回调接口
		 */
		public interface ImageCallback {
			public void imageLoaded(Drawable imageDrawable, String imageUrl);
		}

		/**
		 * 创建子线程加载图片 子线程加载完图片交给handler处理（子线程不能更新ui，而handler处在主线程，可以更新ui）
		 * handler又交给imageCallback，imageCallback须要自己来实现，在这里可以对回调参数进行处理
		 * 
		 * @param imageUrl
		 *            ：须要加载的图片url
		 * @param imageCallback
		 *            ：
		 * @return
		 */
		public Drawable loadDrawable(final String imageUrl,
				final ImageCallback imageCallback) {

			// 如果缓存中存在图片 ，则首先使用缓存
			if (imageCache.containsKey(imageUrl)) {
				SoftReference<Drawable> softReference = imageCache
						.get(imageUrl);
				Drawable drawable = softReference.get();
				if (drawable != null) {
					imageCallback.imageLoaded(drawable, imageUrl);// 执行回调
					return drawable;
				}
			}

			/**
			 * 在主线程里执行回调，更新视图
			 */
			final Handler handler = new Handler() {
				public void handleMessage(Message message) {
					imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
				}
			};

			/**
			 * 创建子线程访问网络并加载图片 ，把结果交给handler处理
			 */
			new Thread() {
				@Override
				public void run() {
					Drawable drawable = loadImageFromUrl(imageUrl);
					// 下载完的图片放到缓存里
					imageCache.put(imageUrl, new SoftReference<Drawable>(
							drawable));
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
				}
			}.start();

			return null;
		}

		/**
		 * 下载图片 （注意HttpClient 和httpUrlConnection的区别）
		 */
		public Drawable loadImageFromUrl(String url) {

			try {
				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 1000 * 15);
				HttpGet get = new HttpGet(url);
				HttpResponse response;

				response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();

					Drawable d = Drawable.createFromStream(entity.getContent(),
							"src");

					return d;
				} else {
					return null;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		// 清除缓存
		public void clearCache() {

			if (this.imageCache.size() > 0) {

				this.imageCache.clear();
			}

		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i("popupwindow", "onPause");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("popupwindow", "onStop");
	}

}
