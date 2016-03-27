package com.geminno.erhuo;

import java.io.IOException;
import java.util.Properties;

import com.geminno.erhuo.entity.Url;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener {
	ImageView ivRegBack;
	CheckBox chkAgree;
	// 账号对象

	EditText etphone;
	// 输入的密码

	EditText etpwd;
	// 确认密码

	EditText etpwdagain;
	private String name;
	private String pwd;
	private String pwdagain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		ivRegBack = (ImageView) findViewById(R.id.iv_register_return);
		chkAgree = (CheckBox) findViewById(R.id.chk_agree_rule);
		etphone = (EditText) findViewById(R.id.et_phone_register);
		etpwd = (EditText) findViewById(R.id.et_pwd_register);
		etpwdagain = (EditText) findViewById(R.id.et_pwd_again);
		// 将传过来的账号显示在账号栏
		Intent intent = getIntent();
		String phone = intent.getStringExtra("phone");
		Log.i("result", "register中phone为" + phone);
		etphone.setText(phone);

		ivRegBack.setOnClickListener(this);
		Button button;
		button = (Button) findViewById(R.id.btn_register);
		button.setOnClickListener(this);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this,
				getResources().getColor(R.color.main_red));
		chkAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					chkAgree.setTextColor(getResources()
							.getColor(R.color.black));
				} else {
					chkAgree.setTextColor(getResources().getColor(
							R.color.text_hint_color));
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_register_return:
			this.finish();
			break;
		// 注册
		case R.id.btn_register:
			Log.i("cheshi", "注册");
			
			RequestParams params = new RequestParams();
			name = etphone.getText().toString();
			
			pwd = etpwd.getText().toString();
			pwdagain = etpwdagain.getText().toString();
			
			HttpUtils http = new HttpUtils();
			//String url="http://10.201.1.16:8080/secondHandShop/CanRegisterServlet";
			String headUrl = Url.getUrlHead();
			String url = headUrl + "/CanRegisterServlet";
			params.addQueryStringParameter("identity", name);
			http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO Auto-generated method stub
					Log.i("cheshi", "fuck!失败");
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// TODO Auto-generated method stub
					String result=arg0.result;
					Log.i("cheshi", "首先result"+result);
					if(result.equals("ok")){
						Log.i("cheshi", "应该是可以注册");
						HttpUtils http = new HttpUtils();
						RequestParams params = new RequestParams();
						if (!pwd.equals(pwdagain)) {
							Toast.makeText(RegisterActivity.this, "输入密码错误", 0).show();
							return;
						}
						Log.i("cheshi", "开始注册没有");
						params.addQueryStringParameter("identity", name);
						params.addQueryStringParameter("pwd", pwd);
						String headUrl = Url.getUrlHead();
						String url = headUrl + "/AddUserServlet";
					    //String url="http://10.201.1.16:8080/secondHandShop/AddUserServlet";
						
						http.send(HttpMethod.POST, url, params,
								new RequestCallBack<String>() {

									@Override
									public void onFailure(HttpException arg0, String arg1) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onSuccess(ResponseInfo<String> arg0) {
										// TODO Auto-generated method stub

										String result = arg0.result;
										Log.i("result", "最后"+result);
										if (result != null && !result.equals("null")) {
											Intent intent = new Intent(
													RegisterActivity.this,
													LoginActivity.class);
											startActivity(intent);
											// Toast.makeText(RegisterActivity.this, "注册成功",
											// 1).show();
										} else {
											Toast.makeText(RegisterActivity.this, "注册失败", 1)
													.show();
										}
									}

									private void setAction(Intent intent) {
										// TODO Auto-generated method stub

									}
								});
					}else if (result.equals("no")) {
						Log.i("cheshi", "那就应该是这里");
						Intent intent = new Intent(
								RegisterActivity.this,
								LoginActivity.class);
						startActivity(intent);
						Toast.makeText(RegisterActivity.this, "您已经注册，请登录", 1).show();
					}
				}
			});
			
			
			
			

			break;

		default:
			break;
		}

	}
}
