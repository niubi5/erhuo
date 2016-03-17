package com.geminno.erhuo;

import com.geminno.erhuo.utils.Contant;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

public class VerifyActivity extends Activity implements OnClickListener {
	Button btnVerify;
	ImageView ivBack;
	Button butverify;
	//输入电话栏
	EditText etphone;
	//输入验证嘛
	EditText etpwd;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_verify);
		btnVerify = (Button) findViewById(R.id.btn_verify);
		ivBack = (ImageView) findViewById(R.id.iv_verify_return);
		butverify=(Button) findViewById(R.id.btn_msg_verify);
		
		etphone=(EditText) findViewById(R.id.et_phone_number);
		etpwd=(EditText) findViewById(R.id.et_pwd);
		
		btnVerify.setOnClickListener(this);
		butverify.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		//Mob创建发送验证码应用的key，secret
		
		SMSSDK.initSDK(this, Contant.getAppkey(),Contant.getSecret() );
		//发送短信，也会回调前面的方法
		SMSSDK.registerEventHandler(ehHandler);
		//调用setColor()方法,实现沉浸式状态栏
	  	MainActivity.setColor(this, getResources().getColor(R.color.main_red));
	}

	@Override
	public void onClick(View v) {
		//获取客户端输入的验证码
		String SMS=etpwd.getText().toString().trim();
		String phone=etphone.getText().toString().trim();
		switch (v.getId()) {
		//验证提交按钮
		case R.id.btn_verify:
			//验证对应手机
			SMSSDK.submitVerificationCode("86", phone, SMS);
			Intent intent = new Intent(VerifyActivity.this,RegisterActivity.class);
			startActivity(intent);
			break;
			//
		case R.id.iv_verify_return:
			this.finish();
			//短信验证按钮
		case 	R.id.btn_msg_verify:
			SMSSDK.getVerificationCode("86",phone);
			break;
		default:
			break;
		}
		
	}
	
	
	EventHandler ehHandler=new EventHandler(){

		//主体方法 供回调使用
		@Override
		public void afterEvent(int event, int result, Object data) {
			 Log.i("Msm","event:"+event+"    result:"+result+"    data:"+data.toString());
			// TODO Auto-generated method stub
			switch (event) {
			//验证的时候使用
			case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
				if(result==SMSSDK.RESULT_COMPLETE){
					Toast.makeText(VerifyActivity.this, "获取验证码成功", Toast.LENGTH_SHORT).show();
				    //默认的智能验证是开启的，我已经在后台关闭
					
				}else {
					Toast.makeText(VerifyActivity.this, "获取验证码失败", Toast.LENGTH_SHORT).show();
				
				}
				break;

			default:
				break;
			}
		}
		
	};
}
