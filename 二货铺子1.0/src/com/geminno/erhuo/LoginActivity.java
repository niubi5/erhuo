package com.geminno.erhuo;

import com.geminno.erhuo.entity.Url;
import com.geminno.erhuo.entity.Users;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
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
			Intent intent = new Intent(this,VerifyActivity.class);
			startActivity(intent);

			break;
		case R.id.iv_login_return:
			this.finish();
		case R.id.btn_login:
			Log.i("cheshi", "进来了");
			RequestParams params = new RequestParams();
			// 获取输入的账号
			String name = etName.getText().toString();
			String pwd = etPwd.getText().toString();
			//
			params.addBodyParameter("identity", name);
			params.addBodyParameter("pwd", pwd);

			HttpUtils http=new HttpUtils();
			//服务器路劲
			//String url="http://10.201.1.16:8080/secondHandShop/LoginServlet";
			http.send(HttpMethod.POST, Url.urllogin, params,new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							Log.i("cheshi", "失败");
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							result = arg0.result;
							Log.i("cheshi", result);
							if (result != null && !result.equals("null")) {
								Intent intent2 = new Intent(LoginActivity.this,
										MainActivity.class);
								startActivity(intent2);
								Gson gson = new Gson();
								MyApplication.setUsers((Users) gson.fromJson(
										result, Users.class));
								// Toast.makeText(LoginActivity.this, "登陆成功",
								// 0).show();
							} else {
								Toast.makeText(LoginActivity.this,
										"登陆失败,您还未注册", 0).show();
							}
						}
					});

			break;
			//页面跳转到找回密码
		case R.id.tv_forget_mima:
			Intent intent2=new Intent(this,ZhaoHuiActivity.class);
			startActivity(intent2);
			Intent intent1 = new Intent(this, ZhaoHuiActivity.class);
			startActivity(intent1);
			break;

		default:
			break;
		}

	}

}
