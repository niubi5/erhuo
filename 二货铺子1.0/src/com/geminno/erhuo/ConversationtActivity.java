package com.geminno.erhuo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class ConversationtActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_conversationt);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
	}
	public void onclick(View v){
		switch (v.getId()) {
		case R.id.iv_rongyun_return:
			finish();
			break;

		default:
			break;
		}
	}
}
