package com.geminno.erhuo;

import com.geminno.erhuo.entity.Donation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
	
	Donation donation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_donation_detail);
		initData();
		initView();
	}
	
	/**
	 * 初始化控件
	 */
	public void initView(){
		back = (ImageView) findViewById(R.id.iv_back);
		back.setOnClickListener(this);
		gridView = (GridView) findViewById(R.id.gv_photos);
		userHead = (ImageView) findViewById(R.id.iv_detail_head);
		userHead.setImageResource(R.drawable.header_default);
		userName = (TextView) findViewById(R.id.tv_donation_detail_user_name);
		userName.setText(donation.getUserName());
		time = (TextView) findViewById(R.id.tv_donation_detail_time);
		time.setText(donation.getPubTime());
		content = (TextView) findViewById(R.id.tv_donation_ditail_content);
		content.setText(donation.getDetail());
		logistics = (TextView) findViewById(R.id.tv_donation_detail_logistics);
		logistics.setText(donation.getLogistics());
		address = (TextView) findViewById(R.id.tv_donation_detail_address);
		address.setText(donation.getAddress());
		report = (Button) findViewById(R.id.btn_donation_report);
		report.setOnClickListener(this);
	}

	public void initData(){
		Intent intent = getIntent();
		donation = (Donation) intent.getSerializableExtra("donation");
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
