package com.geminno.erhuo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.Toast;

public class ReportGoodActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_report_good);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
	}
	public void onclick(View v){
		switch (v.getId()) {
		case R.id.iv_report_return:
			finish();
			break;
		case R.id.btn_commit_report:
		break;
		case R.id.radioButton1:
		case R.id.radioButton2:
		case R.id.radioButton3:
		case R.id.radioButton4:
		case R.id.radioButton5:
//			if(((RadioButton)v).isChecked()){
//				Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
//				((RadioButton)v).setChecked(false);
//			}else{
//				Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
//				((RadioButton)v).setChecked(true);
//			}
			break;
		default:
			break;
		}
	}

}
