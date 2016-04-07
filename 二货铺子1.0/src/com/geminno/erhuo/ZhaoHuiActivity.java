package com.geminno.erhuo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geminno.erhuo.VerifyActivity.TimeCount;
import com.geminno.erhuo.utils.Contant;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class ZhaoHuiActivity extends Activity implements OnClickListener {

	EventHandler eh = new EventHandler() {

		@Override
		// 主体方法，供回调试用
		public void afterEvent(int event, int result, Object data) {

			Log.i("Msm", "event:" + event + "    result:" + result
					+ "    data:" + data.toString());

			switch (event) {
			// 验证的时候使用
			case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:

				if (result == SMSSDK.RESULT_COMPLETE) {

					Intent intent = new Intent(ZhaoHuiActivity.this,
							ResetActivity.class);
					intent.putExtra("phone", phone);
					startActivity(intent);
					finish();
				} else {

					toast("请输入正确的验证码");

				}

				break;
			// 得到验证码时候使用
			case SMSSDK.EVENT_GET_VERIFICATION_CODE:

				if (result == SMSSDK.RESULT_COMPLETE) {

					toast("获取验证码成功");

					// 默认的智能验证是开启的,我已经在后台关闭

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

				Toast.makeText(ZhaoHuiActivity.this, str, Toast.LENGTH_SHORT)
						.show();

			}

		});

	}

	EditText etphone;
	EditText etcode;
	Button button;
	ImageView ivzhaohui;
	Button butverify;
	String phone = null;
	private TimeCount time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_zhao_hui);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this,
				getResources().getColor(R.color.main_red));
		time=new TimeCount(60000, 1000);
		button = (Button) findViewById(R.id.btn_verify_zhaohui);
		butverify = (Button) findViewById(R.id.btn_zhaohui_verify);
		etphone = (EditText) findViewById(R.id.et_phone_number_zhaohui);
		etcode = (EditText) findViewById(R.id.et_pwd_zhaohui);
		ivzhaohui = (ImageView) findViewById(R.id.iv_zhaohui_return);
		button.setOnClickListener(this);
		butverify.setOnClickListener(this);
		etphone.setOnClickListener(this);
		etcode.setOnClickListener(this);
		ivzhaohui.setOnClickListener(this);
		// Mob创建发送验证码应用的key，secret

		SMSSDK.initSDK(this, Contant.APPKEY, Contant.SECRET);

		// //发送短信，也会回调前面的方法
		SMSSDK.registerEventHandler(eh);
	}

	@Override
	public void onClick(View v) {

		String SMS = etcode.getText().toString().trim();
		phone = etphone.getText().toString().trim();
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// 验证提交按钮
		case R.id.btn_verify_zhaohui:
			// 客户端输入的验证码
			// 验证对应手机，返回的短信验证码，会回调前面的afterEvent方法
			SMSSDK.submitVerificationCode("86", phone, SMS);
			break;
		// 获取验证码
		case R.id.btn_zhaohui_verify:
			if (phone != null && isMobileNO(phone) == true) {
				Toast.makeText(this, "验证码已发送！", Toast.LENGTH_SHORT).show();
				SMSSDK.getVerificationCode("86", phone);
				time.start();
			} else {
				toast("请输入正确的电话号码");
			}
			break;
		case R.id.iv_zhaohui_return:
			this.finish();
			break;

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
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {// 计时完毕
			butverify.setText("获取验证码");
			butverify.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程
			butverify.setClickable(false);// 防止重复点击
			butverify.setText(millisUntilFinished / 1000 +"");
		}
	}


}
