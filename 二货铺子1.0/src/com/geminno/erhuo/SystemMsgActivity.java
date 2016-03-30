package com.geminno.erhuo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class SystemMsgActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_system_msg);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
	}
	
	public void msgSys(View v){
		switch(v.getId()){
		case R.id.message_sys_back:
			finish();
			break;
		}
	}

}
