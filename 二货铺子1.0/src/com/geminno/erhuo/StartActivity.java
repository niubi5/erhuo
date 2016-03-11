package com.geminno.erhuo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 判断是否首次启动
		SharedPreferences sp = getSharedPreferences("flag", MODE_PRIVATE);
		boolean flag = sp.getBoolean("flag", true);
		if (flag) {
			SharedPreferences sp1 = getSharedPreferences("flag", MODE_PRIVATE);
			SharedPreferences.Editor editor = sp1.edit();
			editor.putBoolean("flag", false);
			editor.commit();
			// 跳转到引导页
			Intent intent = new Intent(this, GuideActivity.class);
			startActivity(intent);
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_start);
		start();
	}

	private void start() {

		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(StartActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}

		}.start();

	}

}
