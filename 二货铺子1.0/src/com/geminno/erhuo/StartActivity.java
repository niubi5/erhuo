package com.geminno.erhuo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.SystemStatusManager;
import com.geminno.erhuo.utils.Url;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class StartActivity extends Activity {

	private String headUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		// 判断是否首次启动
		SharedPreferences sp = getSharedPreferences("flag", MODE_PRIVATE);
		boolean flag = sp.getBoolean("flag", true);
		if (flag) {
			SharedPreferences sp1 = getSharedPreferences("flag", MODE_PRIVATE);
			SharedPreferences.Editor editor = sp1.edit();
			editor.putBoolean("flag", false);
			editor.commit();
			// 跳转到引导页
			Intent intent = new Intent(this, GuideActivity.class);
			startActivity(intent);
			finish();
			return;
		} 
		SharedPreferences sharedPreferences = getSharedPreferences("userInfo",
				MODE_PRIVATE);
		final String userName = sharedPreferences.getString("userName", "");
		String userPwd = sharedPreferences.getString("userPwd", "");
		if (!userName.isEmpty() && !userPwd.isEmpty()) {
			final HttpUtils httpUtils = new HttpUtils();
			headUrl = Url.getUrlHead();
			String url = headUrl + "/LoginServlet";
//			String url = "http://10.201.1.16:8080/secondHandShop/LoginServlet";
			RequestParams params = new RequestParams();
			params.addBodyParameter("identity", userName);
			params.addBodyParameter("pwd", userPwd);
			httpUtils.send(HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {

							}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							String result = arg0.result;
							Log.i("erhuo", result);
							Gson gson = new Gson();
							
							final Users users = gson.fromJson(result, Users.class);
							Log.i("CurrentUser", users.toString());
							MyApplication.setUsers(users);
							Log.i("CurrentUser", "StartCurrentUser:"+users.getId());
							//设置极光推送别名
							if(MyApplication.getCurrentUser() != null){
								Log.i("Jpush",MyApplication.getCurrentUser().getId()+"");
								JPushInterface.setAlias(StartActivity.this, MyApplication.getCurrentUser().getId()+"", new TagAliasCallback() {
									@Override
									public void gotResult(int arg0, String arg1, Set<String> arg2) {
										// TODO Auto-generated method stub
										Log.i("Jpush", "返回码:" + arg0 + "别名:" + arg1);
									}
								});
							}
							
							Toast.makeText(StartActivity.this,
									users.getName() + ",欢迎回来！ ",
									Toast.LENGTH_SHORT).show();
							// 获得当前用户收藏的商品
							String url = headUrl + "/CollectionsServlet";
							RequestParams params = new RequestParams();
							params.addQueryStringParameter("userId", users.getId() + "");
							httpUtils.send(HttpMethod.GET, url, params, new RequestCallBack<String>() {

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									
								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									String result = arg0.result;
									Gson gson = new Gson();
									Type type = new TypeToken<ArrayList<Integer>>(){}.getType();
									ArrayList<Integer> goodsIdList = gson.fromJson(result, type);
									MyApplication.setCollections(goodsIdList);
									// 获得用户收藏的集市列表
									HttpUtils http = new HttpUtils();
									String url = Url.getUrlHead() + "/UserMarketServlet";
									RequestParams params = new RequestParams();
									params.addQueryStringParameter("userId", users.getId() + "");
									http.send(HttpMethod.GET, url, params,
											new RequestCallBack<String>() {

												@Override
												public void onFailure(HttpException arg0, String arg1) {

												}

												@Override
												public void onSuccess(ResponseInfo<String> arg0) {
													String result = arg0.result;
													Type type = new TypeToken<List<Integer>>(){}.getType();
													Gson gson = new Gson();
													List<Integer> myMarkets = gson.fromJson(result, type);
													MyApplication.setMyMarkets(myMarkets);// 存入我收藏的集市集合
													
													
													
												}
											});
									
								}
							}); 
						}
					});
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setTranslucentStatus();
		setContentView(R.layout.activity_start);
		start();
	}

	// 沉浸式状态栏(图片)
	/**
	 * 设置状态栏背景状态
	 */
	private void setTranslucentStatus() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);
		}
		SystemStatusManager tintManager = new SystemStatusManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(0);// 状态栏无背景
	}

	//

	private void start() {

		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(StartActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}

		}.start();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(StartActivity.this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(StartActivity.this);
	}
	

}
