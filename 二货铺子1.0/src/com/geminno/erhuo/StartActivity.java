package com.geminno.erhuo;

import com.geminno.erhuo.utils.SystemStatusManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

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
		
		setTranslucentStatus();
		setContentView(R.layout.activity_start);
		start();
	}
	
	//沉浸式状态栏(图片)
	/**
     * 设置状态栏背景状态
     */
    private void setTranslucentStatus() 
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }
        SystemStatusManager tintManager = new SystemStatusManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(0);//状态栏无背景
    }
	//
	
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
