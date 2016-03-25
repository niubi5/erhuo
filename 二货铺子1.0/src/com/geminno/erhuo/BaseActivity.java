package com.geminno.erhuo;

import com.geminno.erhuo.utils.ActivityCollector;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
	}

	@Override
	protected void onDestroy() {
		ActivityCollector.removeActivity(this);
		super.onDestroy();
	}
	
	

}
