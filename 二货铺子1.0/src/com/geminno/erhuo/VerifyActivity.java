package com.geminno.erhuo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class VerifyActivity extends Activity implements OnClickListener {
	Button btnVerify;
	ImageView ivBack;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_verify);
		btnVerify = (Button) findViewById(R.id.btn_verify);
		ivBack = (ImageView) findViewById(R.id.iv_verify_return);
		btnVerify.setOnClickListener(this);
		ivBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_verify:
			Intent intent = new Intent(this,RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.iv_verify_return:
			this.finish();
		default:
			break;
		}
		
	}
}
