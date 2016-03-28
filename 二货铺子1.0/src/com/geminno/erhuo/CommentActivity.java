package com.geminno.erhuo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class CommentActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_comment);
	}
	
	public void msgComment(View v){
		switch(v.getId()){
		case R.id.message_back:
			finish();
			break;
		}
	}

}
