package com.geminno.erhuo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class DonationReportActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_donation_report);
	}
	
	public void click(View v){
		switch(v.getId()){
		case R.id.iv_donation_report_back:
			finish();
			break;
		case R.id.btn_report_submit:
			break;
		}
	}
}
