package com.geminno.erhuo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnClickListener{
	TextView tvRegister;
	ImageView ivBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		tvRegister = (TextView) findViewById(R.id.tv_register);
		ivBack = (ImageView) findViewById(R.id.iv_login_return);
		tvRegister.setOnClickListener(this);
		ivBack.setOnClickListener(this);
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
		default:
			break;
		}
		
	}

}
