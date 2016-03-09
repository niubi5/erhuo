package com.geminno.erhuo;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

public class RegisterActivity extends Activity implements OnClickListener {
	ImageView ivRegBack;
	CheckBox chkAgree;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		ivRegBack = (ImageView) findViewById(R.id.iv_register_return);
		chkAgree = (CheckBox) findViewById(R.id.chk_agree_rule);
		ivRegBack.setOnClickListener(this);
		chkAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					chkAgree.setTextColor(getResources().getColor(R.color.black));					
				}else{
					chkAgree.setTextColor(getResources().getColor(R.color.text_hint_color));
				}
				
			}
		});
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_register_return:
			this.finish();
			break;
		default:
			break;
		}
		
	}
}
