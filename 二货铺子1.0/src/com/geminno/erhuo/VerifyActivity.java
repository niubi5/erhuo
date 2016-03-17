package com.geminno.erhuo;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
	
	 EventHandler eh = new EventHandler() {

	        @Override
	      //主体方法，供回调试用
	        public void afterEvent(int event, int result, Object data) {

	            Log.i("Msm","event:"+event+"    result:"+result+"    data:"+data.toString());

	                switch (event) {
	                	//验证的时候使用
	                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:

	                        if (result == SMSSDK.RESULT_COMPLETE) {

	                            Intent intent=new Intent(VerifyActivity.this,RegisterActivity.class);
                                startActivity(intent);
                                
	                        } else {

	                           toast("请输入正确的验证码");

	                        }

	                        break;
	                       //得到验证码时候使用
	                    case SMSSDK.EVENT_GET_VERIFICATION_CODE:

	                        if (result == SMSSDK.RESULT_COMPLETE) {

	                            toast("获取验证码成功");

	                            //默认的智能验证是开启的,我已经在后台关闭

	                        } else {

	                            toast("获取验证码失败");

	                        }

	                        break;

	                }

	        }

	    };
	    
	    private void toast(final String str) {

	        runOnUiThread(new Runnable() {

	            @Override

	            public void run() {

	                Toast.makeText(VerifyActivity.this, str, Toast.LENGTH_SHORT).show();

	            }

	        });

	    }
	
	Button btnVerify;
	ImageView ivBack;
	Button butmsgverify;
	// 输入电话栏
	EditText etphone;
	// 输入验证嘛
	EditText etpwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_verify);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this,
				getResources().getColor(R.color.login_background));
		// 3.17短信验证
		btnVerify = (Button) findViewById(R.id.btn_verify);
		ivBack = (ImageView) findViewById(R.id.iv_verify_return);
		butmsgverify = (Button) findViewById(R.id.btn_msg_verify);

		etphone = (EditText) findViewById(R.id.et_phone_number);
		etpwd = (EditText) findViewById(R.id.et_pwd_ver);

		btnVerify.setOnClickListener(this);
		butmsgverify.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		// Mob创建发送验证码应用的key，secret

		SMSSDK.initSDK(this, Contant.APPKEY, Contant.SECRET);

		// //发送短信，也会回调前面的方法
		SMSSDK.registerEventHandler(eh);

	}

	@Override
	public void onClick(View v) {
		// 获取客户端输入的验证码

		String SMS = etpwd.getText().toString().trim();
		String phone=etphone.getText().toString().trim();
		switch (v.getId()) {

		case R.id.iv_verify_return:
			this.finish();
			break;
		// 获取验证码
		case R.id.btn_msg_verify:

			if(phone!=null&&isMobileNO(phone)==true){
			    SMSSDK.getVerificationCode("86",phone);
			}else {
				toast("请输入正确的电话号码");
			}
			break;
		// 短信验证按钮
		case R.id.btn_verify:
			//客户端输入的验证码
			 	//验证对应手机，返回的短信验证码，会回调前面的afterEvent方法
		        SMSSDK.submitVerificationCode("86", phone, SMS);
			break;
		//
		default:
			break;
		}

	}

	 public static boolean isMobileNO(String mobiles) {
	 Pattern p = Pattern
	 .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
	 Matcher m = p.matcher(mobiles);
	 System.out.println(m.matches() + "---");
	 return m.matches();
	 }

	// ///////////////

	

}
