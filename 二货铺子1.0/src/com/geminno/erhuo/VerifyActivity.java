package com.geminno.erhuo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.geminno.erhuo.utils.Contant;
import com.geminno.erhuo.utils.Url;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
		// 主体方法，供回调试用
		public void afterEvent(int event, int result, Object data) {
			Log.i("Msm", "event:" + event + "    result:" + result
					+ "    data:" + data.toString());
			switch (event) {
			// 验证的时候使用
			case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
				Log.i("result", "验证码结果");
				if (result == SMSSDK.RESULT_COMPLETE) {
					Intent intent = new Intent(VerifyActivity.this,
							RegisterActivity.class);
					intent.putExtra("phone", phone);
					Log.i("result", "phone:" + phone);
					startActivity(intent);
				} else {
					toast("请输入正确的验证码");
				}
				break;
			// 得到验证码时候使用
			case SMSSDK.EVENT_GET_VERIFICATION_CODE:
				Log.i("result", result + "");
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

				Toast.makeText(VerifyActivity.this, str, Toast.LENGTH_SHORT)
						.show();
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
	String phone = null;
	private TimeCount time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_verify);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		// 3.17短信验证
		time=new TimeCount(60000, 1000);
		btnVerify = (Button) findViewById(R.id.btn_verify);
		ivBack = (ImageView) findViewById(R.id.iv_verify_return);
		//获取短信按钮	
		butmsgverify = (Button) findViewById(R.id.btn_msg_verify);

		etphone = (EditText) findViewById(R.id.et_phone_verify);
		etpwd = (EditText) findViewById(R.id.et_pwd_verify);

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
		phone = etphone.getText().toString().trim();
		switch (v.getId()) {

		case R.id.iv_verify_return:
			this.finish();
			break;
		// 获取验证码
		case R.id.btn_msg_verify:
			// 判断手机号是否已注册过
			HttpUtils http = new HttpUtils();
			String headUrl = Url.getUrlHead();
			String url = headUrl + "/CanRegisterServlet";
			RequestParams params = new RequestParams();
			params.addQueryStringParameter("identity", phone);
			http.send(HttpRequest.HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Toast.makeText(VerifyActivity.this, "网络异常！",
									Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							if (arg0.result.toString().equals("ok")) {
								if (phone != null && isMobileNO(phone) == true) {
									SMSSDK.getVerificationCode("86", phone);
									time.start();
								} else {
									toast("请输入正确的电话号码");
								}
							} else {
								Toast.makeText(VerifyActivity.this,
										"该手机号码已注册过，请直接登录！", Toast.LENGTH_SHORT)
										.show();
							}

						}
					});
			break;
		// 短信验证按钮
		case R.id.btn_verify:
			// 客户端输入的验证码
			// 验证对应手机，返回的短信验证码，会回调前面的afterEvent方法
			if (phone != null && isMobileNO(phone) == true) {
				SMSSDK.submitVerificationCode("86", phone, SMS);
			} else {
				toast("请输入正确的电话号码");
			}
			break;
		default:
			break;
		}

	}

	// 判断输入的的是否为正确的手机号码
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("result", "崩了");
		super.onDestroy();
	}
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {// 计时完毕
			butmsgverify.setText("获取验证码");
			butmsgverify.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程
			butmsgverify.setClickable(false);// 防止重复点击
			butmsgverify.setText(millisUntilFinished / 1000 +"");
		}
	}

	
}

