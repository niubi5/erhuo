package com.geminno.erhuo;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.geminno.erhuo.entity.Donates;
import com.geminno.erhuo.utils.Url;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class DonateActivity extends Activity {

	private EditText etTitle;
	private EditText etDetail;
	private EditText etLogisticsCom;
	private EditText etLogisticsNum;

	Donates donates;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_donation_report);
		initView();
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.iv_donation_report_back:
			finish();
			break;
		case R.id.btn_report_submit:
			// 将取到的数据封装到实体
			donates = new Donates();

			// 获取控件输入值
			String title = etTitle.getText().toString();
			String detail = etDetail.getText().toString();
			String logisticsCom = etLogisticsCom.getText().toString();
			String logisticsNum = etLogisticsNum.getText().toString();

			if (!title.equals("")) {
				if (!detail.equals("")) {
					if (!logisticsCom.equals("")) {
						if (!logisticsNum.equals("")) {
							// 组装对象
							donates.setHelpId(getIntent().getIntExtra(
									"donationId", 1));
							donates.setUserId(MyApplication.getCurrentUser().getId());
							Log.i("DonateFragmentResult", MyApplication.getCurrentUser().getId()+"");
							donates.setTitle(title);
							donates.setBrief(detail);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							donates.setDonTime(sdf.format(new Date()));
							donates.setLogisticsCom(logisticsCom);
							donates.setLogisticsNum(logisticsNum);
							
							Log.i("donates", title + detail);
							

							Gson gson = new GsonBuilder().setDateFormat(
									"yyyy-MM-dd HH:mm:ss").create();
							String donatesGson = gson.toJson(donates);

							RequestParams params = new RequestParams();
							params.addBodyParameter("donates", donatesGson);
                            String url = Url.getUrlHead() + "/DonateServlet";				
							HttpUtils http = new HttpUtils();
							http.send(HttpRequest.HttpMethod.POST, url,params,
									new RequestCallBack<String>() {

										@Override
										public void onFailure(
												HttpException arg0, String arg1) {
											Toast.makeText(DonateActivity.this,
													"捐赠失败，请检查您的网络设置！",
													Toast.LENGTH_SHORT).show();
										}

										@Override
										public void onSuccess(
												ResponseInfo<String> arg0) {
											Toast.makeText(DonateActivity.this,
													"捐赠成功，谢谢您的捐赠！",
													Toast.LENGTH_SHORT).show();
											try {
												Thread.sleep(1000);
											} catch (InterruptedException e) {
										
												e.printStackTrace();
											}
											DonateActivity.this.finish();

										}
									});
						} else {
							Toast.makeText(this, "没有编号别人找不到东西的哦",
									Toast.LENGTH_SHORT).show();
							break;
						}
					} else {
						Toast.makeText(this, "填写一下物流吧，收您捐赠的人也知道去哪里去快递",
								Toast.LENGTH_SHORT).show();
						break;
					}
				} else {
					Toast.makeText(this, "您捐赠了哪些东西呢", Toast.LENGTH_SHORT)
							.show();
					break;
				}
			} else {
				Toast.makeText(this, "给您的爱心取个标题吧", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	public void initView() {
		etTitle = (EditText) findViewById(R.id.et_donate_title);
		etDetail = (EditText) findViewById(R.id.et_donate_detail);
		etLogisticsCom = (EditText) findViewById(R.id.et_donate_logisticsCom);
		etLogisticsNum = (EditText) findViewById(R.id.et_donate_logisticsNum);
	}
}
