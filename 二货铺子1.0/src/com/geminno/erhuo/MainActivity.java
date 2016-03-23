package com.geminno.erhuo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.geminno.erhuo.adapter.HomePageAdapter;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.fragment.BaseFragment;
import com.geminno.erhuo.fragment.DonateFragment;
import com.geminno.erhuo.fragment.HomeFragment;
import com.geminno.erhuo.fragment.MessageFragment;
import com.geminno.erhuo.fragment.UserInfoFragment;
import com.geminno.erhuo.view.RefreshListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private Context context;
	private HomeFragment homeFragment;
	private DonateFragment donateFragment;
	private MessageFragment messageFragment;
	private UserInfoFragment shopFragment;
	private Button homeBtn;
	private Button donateBtn;
	private Button messageBtn;
	private Button shopBtn;
	private ImageView publishGoods;
	private List<BaseFragment> fragments;
	private List<Button> btns;
	private int currentIndex; // 当前fragment索引
	private RefreshListView refreshListView;
	// ------------------------
	private List<Markets> listMarkets = null;
	private List<Goods> listGoods = null;
	// 定位相关
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	@SuppressLint("ResourceAsColor")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		// 调用setColor()方法
		setColor(this, getResources().getColor(R.color.main_red));
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener);
		initView();
		initLocation();
		mLocationClient.start();
	}

	// 沉浸式状态栏
	/** * 设置状态栏颜色 * * @param activity 需要设置的activity * @param color 状态栏颜色值 */
	public static void setColor(Activity activity, int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// 设置状态栏透明
			activity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 生成一个状态栏大小的矩形
			View statusView = createStatusView(activity, color);
			// 添加 statusView 到布局中
			ViewGroup decorView = (ViewGroup) activity.getWindow()
					.getDecorView();
			decorView.addView(statusView);
			// 设置根布局的参数
			ViewGroup rootView = (ViewGroup) ((ViewGroup) activity
					.findViewById(android.R.id.content)).getChildAt(0);
			rootView.setFitsSystemWindows(true);
			rootView.setClipToPadding(true);
		}
	}

	/**
	 * * 生成一个和状态栏大小相同的矩形条 * * @param activity 需要设置的activity * @param color
	 * 状态栏颜色值 * @return 状态栏矩形条
	 */
	private static View createStatusView(Activity activity, int color) {
		// 获得状态栏高度
		int resourceId = activity.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		int statusBarHeight = activity.getResources().getDimensionPixelSize(
				resourceId);

		// 绘制一个和状态栏一样高的矩形
		View statusView = new View(activity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
		statusView.setLayoutParams(params);
		statusView.setBackgroundColor(color);
		return statusView;
	}
	
	// 配置定位SDK参数
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		//int span = 1000;
		option.setScanSpan(0);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}
	
	/**
	 * 定位SDK监听函数
	 */
	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// 单位：公里每小时
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\nheight : ");
				sb.append(location.getAltitude());// 单位：米
				sb.append("\ndirection : ");
				sb.append(location.getDirection());// 单位度
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\ndescribe : ");
				sb.append("gps定位成功");

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}
			sb.append("\nlocationdescribe : ");
			sb.append(location.getLocationDescribe());// 位置语义化信息
			List<Poi> list = location.getPoiList();// POI数据
			if (list != null) {
				sb.append("\npoilist size = : ");
				sb.append(list.size());
				for (Poi p : list) {
					sb.append("\npoi= : ");
					sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
				}
			}
			Log.i("BaiduLocationApiDem", sb.toString());
//			tvLocation.setText("经度:"+location.getLongitude() + ",纬度:"
//					+ location.getLatitude());
			MyApplication.setLocation(location);
			LatLng ptCenter = new LatLng(location.getLatitude(),location.getLongitude());
			// 反Geo搜索
//			mSearch.reverseGeoCode(new ReverseGeoCodeOption()
//					.location(ptCenter));

		}
	}

	private void initView() {
		context = this;
		homeFragment = new HomeFragment(context);
		donateFragment = new DonateFragment();
		messageFragment = new MessageFragment();
		shopFragment = new UserInfoFragment();
		homeBtn = (Button) findViewById(R.id.btn_main_home);
		donateBtn = (Button) findViewById(R.id.btn_main_donate);
		messageBtn = (Button) findViewById(R.id.btn_main_message);
		shopBtn = (Button) findViewById(R.id.btn_main_userinfo);
		publishGoods = (ImageView) findViewById(R.id.iv_publish_goods);
		fragments = new ArrayList<BaseFragment>();
		btns = new ArrayList<Button>();
		fragments.add(homeFragment);
		fragments.add(donateFragment);
		fragments.add(messageFragment);
		fragments.add(shopFragment);
		btns.add(homeBtn);
		btns.add(donateBtn);
		btns.add(messageBtn);
		btns.add(shopBtn);
		homeBtn.setOnClickListener(this);
		donateBtn.setOnClickListener(this);
		messageBtn.setOnClickListener(this);
		shopBtn.setOnClickListener(this);
		publishGoods.setOnClickListener(this);
		// 默认显示第一个fragment
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, fragments.get(0)).commit();
		// 默认选中首页按钮
		btns.get(0).setSelected(true);
	}

	@Override
	public void onClick(View v) {
		int nextIndex = 0; // 即将显示的fragment
		switch (v.getId()) {
		// 主页按钮
		case R.id.btn_main_home:
			nextIndex = 0;
			break;
		// 捐赠按钮
		case R.id.btn_main_donate:
			nextIndex = 1;
			break;
		// 消息按钮
		case R.id.btn_main_message:
			nextIndex = 2;
			break;
		// 个人中心按钮
		case R.id.btn_main_userinfo:
			nextIndex = 3;
			break;
		// 发布按钮
		case R.id.iv_publish_goods:
			nextIndex = currentIndex;
			startActivity(new Intent(this, PublishGoodsActivity.class));
			break;
		}
		// 取消当前按钮选中状态
		btns.get(currentIndex).setSelected(false);
		// 更换fragment
		changeFragment(nextIndex);
		// 选中nextIndex按钮
		btns.get(nextIndex).setSelected(true);
	}

	private void changeFragment(int nextIndex) {
		// 判断是否是当前选中的fragment
		if (currentIndex != nextIndex) {
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			// 不是，则隐藏当前fragment
			transaction.hide(fragments.get(currentIndex));
			// 显示要跳转的fragment，显示之前判断是否添加过
			if (!fragments.get(nextIndex).isAdded()) {
				// 未添加，则先添加
				transaction.add(R.id.fragment_container,
						fragments.get(nextIndex));
			}
			transaction.show(fragments.get(nextIndex)).commit();
		}
		// 改变currentIndex值
		currentIndex = nextIndex;
	}

	@Override
	protected void onResume() {
		// 初始化数据源
		// if (!flag) {
		// initData();
		// }m
		super.onResume();
	}

}
