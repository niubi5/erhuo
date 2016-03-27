package com.geminno.erhuo;

import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.SystemStatusManager;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		Log.i("cheshi", "userName,userPwd" + userName + userPwd);
		if (!userName.isEmpty() && !userPwd.isEmpty()) {
			HttpUtils httpUtils = new HttpUtils();
			String headUrl = Url.getUrlHead();
			String url = headUrl + "/LoginServlet";
			// String url="http://10.201.1.16:8080/secondHandShop/LoginServlet";
			RequestParams params = new RequestParams();
			params.addBodyParameter("identity", userName);
			params.addBodyParameter("pwd", userPwd);
			httpUtils.send(HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							Log.i("cheshi", "罗叼");
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							String result = arg0.result;
							Log.i("erhuo", result);
							Gson gson = new Gson();
							Users users = gson.fromJson(result, Users.class);
							MyApplication.setUsers(users);
							Toast.makeText(StartActivity.this,
									users.getName() + ",欢迎回来！ ",
									Toast.LENGTH_SHORT).show();
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

}
