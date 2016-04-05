package com.geminno.erhuo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class FeedbackActivity extends Activity implements OnClickListener{

	private Button button;
	private EditText etText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_feedback);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
	    etText=(EditText) findViewById(R.id.et_feed_name);
		button=(Button) findViewById(R.id.but_feedback);
		findViewById(R.id.ib_feedback_return).setOnClickListener(this);
		button.setOnClickListener(this);
	}

	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_feedback_return:
			this.finish();
			break;
		case R.id.but_feedback:
			String edtext=etText.getText().toString();
			if (edtext.length()!=0) {
				Toast.makeText(this, "提交成功！", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(this, "请输入您的意见！", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}
}
