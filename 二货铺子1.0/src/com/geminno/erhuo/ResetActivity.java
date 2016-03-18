package com.geminno.erhuo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class ResetActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reset);
		 //调用setColor()方法,实现沉浸式状态栏
		//10.18heikki
	  	MainActivity.setColor(this, getResources().getColor(R.color.login_background));
	}
	
	

	
}
