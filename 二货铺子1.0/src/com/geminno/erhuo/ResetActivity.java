package com.geminno.erhuo;

import com.geminno.erhuo.entity.Url;
import com.geminno.erhuo.entity.Users;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

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
import android.widget.Toast;

public class ResetActivity extends Activity implements OnClickListener{

	

	EditText etpwd;
	EditText etPwdagain;
	Button butreset;
	ImageView ivreset;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reset);
		 //调用setColor()方法,实现沉浸式状态栏
	  	MainActivity.setColor(this, getResources().getColor(R.color.login_background));
	    etpwd=(EditText) findViewById(R.id.et_pwd_reset);
	    etPwdagain=(EditText) findViewById(R.id.et_pwd_resetagain);
	    butreset=(Button) findViewById(R.id.btn_reset);
	    ivreset=(ImageView) findViewById(R.id.iv_return_reset);
	    ivreset.setOnClickListener(this);
	    butreset.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.i("cheshi", "罗叼");
		switch (v.getId()) {
		case R.id.btn_reset:
			String pwd=etpwd.getText().toString().trim();
			String pwdagain=etPwdagain.getText().toString();
			if(pwd==null){
				toast("请输入密码");
				return;
			}else if (pwdagain==null) {
				toast("请再次输入密码");
				return;
			}else if (pwd!=pwdagain&&!pwd.equals(pwdagain)) {
				toast("两次输入密码不一样,请输入正确密码");
				return;
			}else {
				RequestParams params=new RequestParams();
			    Intent intent=getIntent();
				String phone=intent.getStringExtra("phone");
				//String phone="15071048315";
				Log.i("cheshi", "账号密码"+phone+pwd);
				params.addBodyParameter("phone", phone);
				params.addBodyParameter("pwd", pwd);
				HttpUtils httpUtils=new HttpUtils();
				httpUtils.send(HttpMethod.POST, Url.urlreget, params,new RequestCallBack<String>() {

					
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						Log.i("cheshi", "fuck");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						String result = arg0.result;
						Log.i("cheshi", result);
						if(result!=null&&!result.equals("null")){
							Intent intent2 = new Intent(ResetActivity.this,LoginActivity.class);
							startActivity(intent2);
							Gson gson = new Gson();
							MyApplication.setUsers((Users)gson.fromJson(result, Users.class));
							//Toast.makeText(LoginActivity.this, "登陆成功", 0).show();
						}else {
							Toast.makeText(ResetActivity.this, "修改失败,您还未注册", 0).show();
						}
						
					}

					

					
				});
				
				
			}
			break;
		case R.id.iv_return_reset:
			this.finish();
			break;

		default:
			break;
		}
		
	}
	
	private void toast(final String str) {

        runOnUiThread(new Runnable() {

            @Override

            public void run() {

                Toast.makeText(ResetActivity.this, str, Toast.LENGTH_SHORT).show();

            }
        });

    }

	

	
}
