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

public class ZhaoHuiActivity extends Activity implements OnClickListener{
	
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_zhao_hui);
		//调用setColor()方法,实现沉浸式状态栏
	  	MainActivity.setColor(this, getResources().getColor(R.color.login_background));
	    button=(Button) findViewById(R.id.btn_verify_zhaohui);
	    button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_verify_zhaohui:
			Intent intent = new Intent(this,ResetActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	
}
