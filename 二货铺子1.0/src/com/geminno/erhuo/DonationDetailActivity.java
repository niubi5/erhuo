package com.geminno.erhuo;

import java.util.ArrayList;

import com.geminno.erhuo.entity.Donation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
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
	private GridView mPhotoWall;
	private ImageView userHead;
	private TextView userName;
	private TextView time;
	private TextView content;
	private TextView logistics;
	private TextView address;
	private Button donate;
	private ImageView toReport;
	
	private com.geminno.erhuo.adapter.PhotoWallAdapter mAdapter;
	private int mImageThumbSize;
	private int mImageThumbSpacing;
	
	Donation donation;
	ArrayList<String> urls;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_donation_detail);
		initData();
		initView();
		
		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_spacing);
		mAdapter = new com.geminno.erhuo.adapter.PhotoWallAdapter(this, 0, urls,
				mPhotoWall);
		mPhotoWall.setAdapter(mAdapter);
		mPhotoWall.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						final int numColumns = (int) Math.floor(mPhotoWall
								.getWidth()
								/ (mImageThumbSize + mImageThumbSpacing));
						if (numColumns > 0) {
							int cloumnWidth = (mPhotoWall.getWidth() / numColumns)
									- mImageThumbSpacing;
							mAdapter.setItemHeight(cloumnWidth);
							mPhotoWall.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						}
					}
				});
	}
	
	/**
	 * 初始化控件
	 */
	public void initView(){
		back = (ImageView) findViewById(R.id.iv_back);
		back.setOnClickListener(this);
		mPhotoWall = (GridView) findViewById(R.id.gv_photos);
		userHead = (ImageView) findViewById(R.id.iv_detail_head);
		userHead.setImageResource(R.drawable.header_default);
		userName = (TextView) findViewById(R.id.tv_donation_detail_user_name);
		userName.setText(donation.getConsignee());
		time = (TextView) findViewById(R.id.tv_donation_detail_time);
		time.setText(donation.getPubTime());
		content = (TextView) findViewById(R.id.tv_donation_ditail_content);
		content.setText(donation.getDetail());
		logistics = (TextView) findViewById(R.id.tv_donation_detail_logistics);
		logistics.setText(donation.getLogistics());
		address = (TextView) findViewById(R.id.tv_donation_detail_address);
		address.setText(donation.getAddress());
		donate = (Button) findViewById(R.id.btn_donation_report);
		donate.setOnClickListener(this);
		toReport = (ImageView) findViewById(R.id.iv_to_report);
		toReport.setOnClickListener(this);
	}

	public void initData(){
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("Record");
		donation = (Donation) bundle.getSerializable("SingleDonation");
		
		Log.i("SingleDonation", donation.toString() + donation.getAddress());
		urls = bundle.getStringArrayList("urls");
		Log.i("SingleImageUrls", urls.toString());
		
//		donation = (Donation) intent.getSerializableExtra("donation");
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
	    case R.id.iv_to_report:
	    	Intent intent1 = new Intent(this,ReportDonationActivity.class);
	    	intent1.putExtra("donationId", donation.getId());
	    	intent1.putExtra("userId", donation.getUserId());
	    	startActivity(intent1);
	    	
	    }
		
	}

}
