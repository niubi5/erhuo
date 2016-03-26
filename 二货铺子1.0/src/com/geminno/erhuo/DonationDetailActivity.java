package com.geminno.erhuo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 捐赠详细信息页面
 * 
 * @author Administrator
 *
 */
public class DonationDetailActivity extends Activity implements OnClickListener{
	
	private ImageView back;
	private GridView gridView;
	private ImageView userHead;
	private TextView userName;
	private TextView time;
	private TextView content;
	private TextView logistics;
	private TextView address;
	private Button report;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_donation_detail);
		init();
	}
	
	/**
	 * 初始化控件
	 */
	public void init(){
		back = (ImageView) findViewById(R.id.iv_back);
		back.setOnClickListener(this);
		gridView = (GridView) findViewById(R.id.gv_photos);
		userHead = (ImageView) findViewById(R.id.iv_detail_head);
		userName = (TextView) findViewById(R.id.tv_donation_detail_user_name);
		time = (TextView) findViewById(R.id.tv_donation_detail_time);
		content = (TextView) findViewById(R.id.tv_donation_ditail_content);
		logistics = (TextView) findViewById(R.id.tv_donation_detail_logistics);
		address = (TextView) findViewById(R.id.tv_donation_detail_address);
		report = (Button) findViewById(R.id.btn_donation_report);
		report.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
	    switch(v.getId()){
	    case R.id.iv_back:
	    	finish();
	    	break;
	    case R.id.btn_donation_report:
	    	Intent intent = new Intent(this,DonationReportActivity.class);
	    	startActivity(intent);
	    	break;
	    }
		
	}

}
