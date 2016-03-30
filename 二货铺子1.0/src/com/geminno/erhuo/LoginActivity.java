package com.geminno.erhuo;

import java.io.IOException;
import java.util.Properties;

import com.geminno.erhuo.entity.Address;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.ActivityCollector;
import com.geminno.erhuo.utils.Url;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.ViewInjectInfo;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener {
	TextView tvRegister;
	TextView tvforget;
	ImageView ivBack;
	@ViewInject(R.id.et_name)
	EditText etName;
	@ViewInject(R.id.et_pwd)
	EditText etPwd;

	private String result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		ViewUtils.inject(this);
		etName.setFocusable(true);
		etName.setFocusableInTouchMode(true);
		etName.requestFocus();
		tvRegister = (TextView) findViewById(R.id.tv_register);
		ivBack = (ImageView) findViewById(R.id.iv_login_return);
		Button button;
		tvforget = (TextView) findViewById(R.id.tv_forget_mima);
		button = (Button) findViewById(R.id.btn_login);
		tvRegister.setOnClickListener(this);
		tvforget.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		button.setOnClickListener(this);
		ViewUtils.inject(this);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this,
				getResources().getColor(R.color.login_background));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_register:
			Log.i("cheshi", "注册");
			Intent intent = new Intent(this, VerifyActivity.class);
			startActivity(intent);
			break;
		case R.id.iv_login_return:
			this.finish();
			break;
		case R.id.btn_login:
			Log.i("cheshi", "进来了");
			RequestParams params = new RequestParams();
			// 获取输入的账号
			String name = etName.getText().toString();
			String pwd = etPwd.getText().toString();
			if (TextUtils.isEmpty(etPwd.getText())) {
				Toast.makeText(this, "请输入密码!", Toast.LENGTH_SHORT).show();
			} else {
				params.addBodyParameter("pwd", pwd);
				params.addBodyParameter("identity", name);
				HttpUtils http = new HttpUtils();
				// 服务器路径
				 String headUrl = Url.getUrlHead();
				 String url = headUrl + "/LoginServlet";
//				String url = "http://10.201.1.16:8080/secondHandShop/LoginServlet";
				http.send(HttpMethod.POST, url, params,
						new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								// TODO Auto-generated method stub
								Log.i("cheshi", "失败");
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								result = arg0.result;
								if (result != null && !result.equals("null")) {
									if (result.toString().equals("phoneIsNull")) {
										Toast.makeText(LoginActivity.this,
												"登陆失败,您还未注册！", 0).show();
									} else {
										Intent intent2 = new Intent(
												LoginActivity.this,
												MainActivity.class);
										startActivity(intent2);
										Gson gson = new Gson();
										Users users = gson.fromJson(result,
												Users.class);
										MyApplication.setUsers(users);
										getCurUserAddress();
										SharedPreferences shared = getSharedPreferences(
												"userInfo", MODE_PRIVATE);
										shared.edit()
												.putString("userName",
														users.getIdentity())
												.putString("userPwd",
														users.getPwd())
												.commit();
										ActivityCollector.finishAll();
									}

								} else {
									Toast.makeText(LoginActivity.this,
											"登陆失败,密码错误！", 0).show();
								}
							}
						});
			}

			break;
		// 页面跳转到找回密码
		case R.id.tv_forget_mima:
			Intent intent1 = new Intent(this, ZhaoHuiActivity.class);
			startActivity(intent1);
			break;
		default:
			break;
		}

	}

	// 获取当前用户默认收货地址
	public void getCurUserAddress() {
		Users curUser = MyApplication.getCurrentUser();
		if (curUser != null) {
			 String url = Url.getUrlHead() + "/UserAddressServlet";
//			String url = "http://10.201.1.16:8080/secondHandShop/UserAddressServlet";
			RequestParams rp = new RequestParams();
			rp.addBodyParameter("curUserId", curUser.getId() + "");
			HttpUtils hu = new HttpUtils();
			hu.send(HttpRequest.HttpMethod.POST, url, rp,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							if (result != null && !result.equals("null")) {
								Gson gson = new Gson();
								Address curUserAddress = gson.fromJson(
										arg0.result.toString(), Address.class);
								MyApplication
										.setCurUserDefAddress(curUserAddress);
							}
						}
					});
		}
	}

}
