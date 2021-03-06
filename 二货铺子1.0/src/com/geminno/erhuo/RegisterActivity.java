package com.geminno.erhuo;

import java.io.IOException;
import java.util.Properties;

import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.Url;
import com.google.gson.Gson;
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
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener {
	ImageView ivRegBack;
	CheckBox chkAgree;
	// 账号对象

	TextView etphone;
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
		etphone = (TextView) findViewById(R.id.et_phone_register);
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
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
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
			if (!pwd.equals(pwdagain)) {
				Toast.makeText(RegisterActivity.this, "两次输入的密码不一致！", 0).show();
				return;
			}
			Log.i("cheshi", "开始注册没有");
			Users user = new Users();
			user.setIdentity(name);
			user.setPwd(pwd);
			user.setSex(0);
			user.setInvCode(null);
			user.setName("erhuo" + name.substring(7, 11));
			user.setPhoto(null);
			user.setJifen(0);
			Gson gson = new Gson();
			String userJson = gson.toJson(user);
			params.addBodyParameter("userJson", userJson);
			String headUrl = Url.getUrlHead();
			String url = headUrl + "/AddUserServlet";
			http.send(HttpMethod.POST, url, params,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Toast.makeText(RegisterActivity.this, "网络异常！", 1)
									.show();
						}
						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							String result = arg0.result;
							Log.i("result", "最后" + result);
							if (result != null && !result.equals("null")) {
								Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
								intent.putExtra("phone", name);
								startActivity(intent);
								Toast.makeText(RegisterActivity.this, "注册成功！",1).show();
								finish();
							} else {
								Toast.makeText(RegisterActivity.this, "注册失败！",1).show();
							}
						}
					});
			break;
		default:
			break;
		}

	}
}
