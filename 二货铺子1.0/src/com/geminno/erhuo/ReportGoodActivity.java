package com.geminno.erhuo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ReportGoodActivity extends Activity {

	private int goodId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_report_good);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		goodId = getIntent().getIntExtra("goodId",0);
	}
	public void onclick(View v){
		switch (v.getId()) {
		case R.id.iv_report_return:
			finish();
			break;
		case R.id.btn_commit_report:
			RadioGroup rgReason = (RadioGroup) findViewById(R.id.rg_report_reason);
			RadioButton rbSelected = (RadioButton) findViewById(rgReason.getCheckedRadioButtonId());
			String reason = rbSelected.getText().toString();
			EditText etReason = (EditText) findViewById(R.id.et_reason);
			if(TextUtils.isEmpty(etReason.getText())){
				Toast.makeText(this, goodId+reason, Toast.LENGTH_SHORT).show();				
			}else{
				Toast.makeText(this, goodId+reason+"&"+etReason.getText().toString(), Toast.LENGTH_SHORT).show();
			}
			int USERID = 3;//测试，正式发布应从MyApplication.getCurrentUser().getId()获取
			
		break;
//		case R.id.radioButton1:
//			Toast.makeText(this, "违规", Toast.LENGTH_SHORT).show();
//			break;
//		case R.id.radioButton2:
//			Toast.makeText(this, "欺诈", Toast.LENGTH_SHORT).show();
//			break;
//		case R.id.radioButton3:
//			Toast.makeText(this, "垃圾", Toast.LENGTH_SHORT).show();
//			break;
//		case R.id.radioButton4:
//			Toast.makeText(this, "色情", Toast.LENGTH_SHORT).show();
//			break;
//		case R.id.radioButton5:
////			if(((RadioButton)v).isChecked()){
////				Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
////				((RadioButton)v).setChecked(false);
////			}else{
////				Toast.makeText(this, "false", Toast.LENGTH_SHORT).show();
////				((RadioButton)v).setChecked(true);
////			}
//			Toast.makeText(this, "暴力", Toast.LENGTH_SHORT).show();
//			break;
		default:
			break;
		}
	}

}
