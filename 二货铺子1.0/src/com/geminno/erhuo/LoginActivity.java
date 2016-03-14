package com.geminno.erhuo;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{
	TextView tvRegister;
	ImageView ivBack;
	@ViewInject(R.id.et_name)
	EditText etName;
	@ViewInject(R.id.et_pwd)
	EditText etPwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		tvRegister = (TextView) findViewById(R.id.tv_register);
		ivBack = (ImageView) findViewById(R.id.iv_login_return);
		Button button;
		button=(Button) findViewById(R.id.btn_login);
		tvRegister.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		button.setOnClickListener(this);
		ViewUtils.inject(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_register:
			Intent intent = new Intent(this,VerifyActivity.class);
			startActivity(intent);
			
			break;
		case R.id.iv_login_return:
			this.finish();
		case R.id.btn_login:
			Log.i("cheshi", "进来了");
			RequestParams params=new RequestParams();
			//获取输入的账号
			String name=etName.getText().toString();
			String pwd=etPwd.getText().toString();
			//
			params.addQueryStringParameter("identity", name);
			params.addQueryStringParameter("pwd", pwd);
			HttpUtils http=new HttpUtils();
			//服务器路劲
			String url="http://10.40.5.34:8080/secondHandShop/LoginServlet";
			http.send(HttpMethod.POST, url, params,new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// TODO Auto-generated method stub
					String result=arg0.result;
					Log.i("cheshi", result);
					if(result!=null&&!result.equals("null")){
						Toast.makeText(LoginActivity.this, "登陆成功", 0).show();
					}else {
						Toast.makeText(LoginActivity.this, "登陆失败", 0).show();
					}
				}
			});
			
			break;
		
		default:
			break;
		}
		
	}

}

