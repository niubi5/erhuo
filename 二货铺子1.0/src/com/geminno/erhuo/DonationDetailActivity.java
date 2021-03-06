package com.geminno.erhuo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geminno.erhuo.entity.Donation;
import com.geminno.erhuo.utils.Url;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 捐赠详细信息页面
 * 
 * @author Administrator
 * 
 */
public class DonationDetailActivity extends Activity implements OnClickListener {

	private ImageView back;
	private GridView mPhotoWall;
	private ImageView userHead;
	private TextView userName;
	private TextView time;
	private TextView content;
	private TextView logistics;
	private TextView address;
	private TextView consignee;
	private TextView phone;
	private TextView donatorNames;

	private Button donate;
	private ImageView toReport;

	private com.geminno.erhuo.adapter.PhotoWallAdapter mAdapter;
	private int mImageThumbSize;
	private int mImageThumbSpacing;

	// 用来存放所有的求助及其对应的捐赠者的名字
	List<Map<Integer, List<String>>> donatorsName = new ArrayList<Map<Integer, List<String>>>();
	Donation donation;
	ArrayList<String> urls;
    ArrayList<String> singleNames ;
	StringBuffer sb;
	private int helpId;
	private static Toast mToast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_donation_detail);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		singleNames = new ArrayList<>();
		sb = new StringBuffer();
		initData();
		initView();
		
		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_spacing);
		mAdapter = new com.geminno.erhuo.adapter.PhotoWallAdapter(this, 0,
				urls, mPhotoWall);
		mPhotoWall.setAdapter(mAdapter);
		mPhotoWall.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@SuppressWarnings({ "deprecation", "deprecation" })
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
	public void initView() {
		back = (ImageView) findViewById(R.id.iv_back);
		back.setOnClickListener(this);
		mPhotoWall = (GridView) findViewById(R.id.gv_photos);
		userHead = (ImageView) findViewById(R.id.iv_detail_head);
		if (donation.getHeadImage() != null) {
			ImageLoader.getInstance().displayImage(donation.getHeadImage(),
					userHead);
		} else {
			userHead.setImageResource(R.drawable.header_default);
		}

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

		consignee = (TextView) findViewById(R.id.tv_donation_ditail_geterName);
		consignee.setText(donation.getConsignee());

		phone = (TextView) findViewById(R.id.tv_get_donation_photo_user);
		phone.setText(donation.getPhone());

		donatorNames = (TextView) findViewById(R.id.tv_detail_donatorNames);
		
//		if ( !sb.equals("")) {
//			donatorNames.setText(sb.toString() + "已捐赠过");
//		} else {
//			donatorNames.setText("目前还没有人捐赠过，您可以率先捐赠哦");
//		}
		donate = (Button) findViewById(R.id.btn_donation_report);
		donate.setOnClickListener(this);
		toReport = (ImageView) findViewById(R.id.iv_to_report);
		toReport.setOnClickListener(this);
	}

	public void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("Record");
		//helpId = bundle.getInt("helpId", 0);
		
		
		
		donation = (Donation) bundle.getSerializable("SingleDonation");
		//Toast.makeText(this, donation.getId()+"", 1).show();
		// 传递helpId
		getName(donation.getId());

		urls = bundle.getStringArrayList("urls");
	
//		for (int i = 0; i < singleNames.size(); i++) {
//			sb.append(singleNames.get(i)
//					+ ((i == (singleNames.size() - 1)) ? "" : ","));	
//			Log.i("singleNames", singleNames.get(i));
//		}		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		// 捐赠按钮
		case R.id.btn_donation_report:
			if (MyApplication.getCurrentUser() != null) {
				Intent intent = new Intent(this, DonateActivity.class);
				intent.putExtra("donationId", donation.getId());
				intent.putExtra("userId", MyApplication.getCurrentUser()
						.getId() + "");
				startActivity(intent);
			} else {
//				Toast.makeText(DonationDetailActivity.this, "请先登录!",
//						Toast.LENGTH_SHORT).show();
				showToast(DonationDetailActivity.this, "请先登录!", Toast.LENGTH_SHORT);
			}
			break;
		// 举报按钮
		case R.id.iv_to_report:
			Intent intent1 = new Intent(this, ReportDonationActivity.class);
			intent1.putExtra("donationId", donation.getId());
			intent1.putExtra("userId", donation.getUserId());
			startActivity(intent1);

		}

	}

	/**
	 * 获得对每一个求助发出捐赠的用户名的集合
	 * 
	 * @param helpId
	 */
	public void getName(final int helpId) {
       // final ArrayList<String> singleNames = new ArrayList<String>();
     // 设置请求参数
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("helpId", String.valueOf(helpId));
		String url = Url.getUrlHead() + "/GetDonatorServlet";
		// 发送请求
		HttpUtils http = new HttpUtils();
		http.configCurrentHttpCacheExpiry(0);
		http.send(HttpRequest.HttpMethod.GET, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0,
							String arg1) {
						Log.i("helpRequest", "请求失败");

					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.i("helpRequest", "请求成功");
						String result = arg0.result;
						Gson gson = new GsonBuilder()
								.enableComplexMapKeySerialization()
								.setDateFormat("yyyy-MM-dd HH:mm:ss")
								.create();
						Type type = new TypeToken<ArrayList<String>>() {
						}.getType();
						ArrayList<String> names = gson.fromJson(result,
								type);
						for (int i = 0; i < names.size(); i++) {
							Log.i("donators", names.get(i));
						}
						singleNames = getName(names);
						for (int i = 0; i < singleNames.size(); i++) {
							sb.append(singleNames.get(i)
									+ ((i == (singleNames.size() - 1)) ? "" : ","));	
							Log.i("singleNames", singleNames.get(i));
						}
						if ( !sb.equals("")) {
							donatorNames.setText(sb.toString() + "已捐赠过");
						} else {
							donatorNames.setText("目前还没有人捐赠过，您可以率先捐赠哦");
						}

					}

				});

	}
	
	public ArrayList<String> getName(ArrayList<String> mNames){
		
		for(int i = 0; i < mNames.size(); i++){
			singleNames.add(mNames.get(i));
		}
		return singleNames;
	}
	
	 public static void showToast(Context context, String text, int duration) {  
	        if (mToast == null) {  
	            mToast = Toast.makeText(context, text, duration);  
	        } else {  
	            mToast.setText(text);  
	            mToast.setDuration(duration);  
	        }  
	  
	        mToast.show();  
	    }

}
