package com.geminno.erhuo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.fragment.BaseFragment;
import com.geminno.erhuo.fragment.DonateFragment;
import com.geminno.erhuo.fragment.HomeFragment;
import com.geminno.erhuo.fragment.MessageFragment;
import com.geminno.erhuo.fragment.UserInfoFragment;
import com.geminno.erhuo.utils.HomePageAdapter;
import com.geminno.erhuo.view.RefreshListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class MainActivity extends FragmentActivity implements OnClickListener {

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
	private boolean flag = false;
	private RefreshListView refreshListView;
	// ------------------------
	private List<Markets> listMarkets = new ArrayList<Markets>();
	private boolean isGetData = false;

	@SuppressLint("ResourceAsColor") @TargetApi(Build.VERSION_CODES.KITKAT) @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		//调用setColor()方法
		setColor(this, getResources().getColor(R.color.main_red));
		initView();
	}
	
	//沉浸式状态栏
	/** * 设置状态栏颜色 * * @param activity 需要设置的activity * @param color 状态栏颜色值 */
	public static void setColor(Activity activity, int color) {
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        // 设置状态栏透明
	        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        // 生成一个状态栏大小的矩形
	        View statusView = createStatusView(activity, color);
	        // 添加 statusView 到布局中
	        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
	        decorView.addView(statusView);
	        // 设置根布局的参数
	        ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
	        rootView.setFitsSystemWindows(true);
	        rootView.setClipToPadding(true);
	    }
	}
	/** * 生成一个和状态栏大小相同的矩形条 * * @param activity 需要设置的activity * @param color 状态栏颜色值 * @return 状态栏矩形条 */
	private static View createStatusView(Activity activity, int color) {
	    // 获得状态栏高度
	    int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
	    int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
	 
	    // 绘制一个和状态栏一样高的矩形
	    View statusView = new View(activity);
	    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
	            statusBarHeight);
	    statusView.setLayoutParams(params);
	    statusView.setBackgroundColor(color);
	    return statusView;
	}
	

	private void initView() {
		homeFragment = new HomeFragment();
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
		if (!flag) {
			initData();
		}
		super.onResume();
	}
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		 // 如果数据未加载成功，再次点击屏幕重新发出请求
//		if(!isGetData){
//			switch (event.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//				initData();
//			}
//		}
//		return true;
//	}
	
	private void initData() {

		new Thread() {

			@Override
			public void run() {

				HttpUtils http = new HttpUtils();
				Properties prop = new Properties();
				String head = null;// http: 头部
				try {
					// 从属性文件读取url
					prop.load(MainActivity.class
							.getResourceAsStream("/com/geminno/erhuo/utils/url.properties"));
					head = prop.getProperty("url");
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 拼接url
				String url = head + "/ListMarketsServlet";
				http.send(HttpRequest.HttpMethod.POST, url,
						new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								Toast.makeText(MainActivity.this, "网络异常",
										Toast.LENGTH_SHORT).show();
								
							}

							@SuppressWarnings("unchecked")
							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								String result = arg0.result;// 获得响应结果
								Gson gson = new Gson();
								Type type = new TypeToken<List<Markets>>() {
								}.getType();
								listMarkets = (List<Markets>) gson.fromJson(
										result, type);
								// 获得listview
								refreshListView = homeFragment
										.getRefreshListView();
								// 设置数据源
								refreshListView.setAdapter(new HomePageAdapter(
										MainActivity.this, listMarkets));
								isGetData = true;// 成功获得数据
							}

						});
				super.run();
			}

		}.start();

	}

}
