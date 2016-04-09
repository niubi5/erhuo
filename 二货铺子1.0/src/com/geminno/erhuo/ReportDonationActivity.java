package com.geminno.erhuo;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.geminno.erhuo.entity.Donation;
import com.geminno.erhuo.entity.HelpsReports;
import com.geminno.erhuo.utils.Url;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class ReportDonationActivity extends Activity implements OnClickListener {

	private ImageView back;
	private EditText title;
	private EditText content;
	private Button report;
	private Donation donation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_report_donation);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		initView();
	}

	// 初始化控件
	public void initView() {

		// 返回按钮
		back = (ImageView) findViewById(R.id.iv_back_report);
		back.setOnClickListener(this);

		content = (EditText) findViewById(R.id.et_report_content);

		// 举报按钮点击事件
		report = (Button) findViewById(R.id.btn_donation_report);
		report.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_back_report:
			finish();
			break;
		// 将获得到数据写入数据库
		case R.id.btn_donation_report:
			// 获得用户输入值
			String reportContent = content.getText().toString();
			if (MyApplication.getCurrentUser() != null) {

				if (!reportContent.equals("")) {
					// 封装HelpsReports
					HelpsReports helpReport = new HelpsReports();
					helpReport.setHelpId(getIntent().getIntExtra("donationId",
							1));
					// helpReport.setUserId(getIntent().getIntExtra("userId",
					// 1));
					helpReport.setBrief(reportContent);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					helpReport.setRepTime(sdf.format(new Date()));
					helpReport.setState(1);

					// 转化成Json
					Gson gson = new GsonBuilder().setDateFormat(
							"yyyy-MM-dd HH:mm:ss").create();
					String helpReportGson = gson.toJson(helpReport);

					// 请求url
					// String url =
					// "http://10.201.1.20:8080/secondHandShop/HelpsReportServlet";
					String url = Url.getUrlHead() + "/HelpsReportServlet";
					// 设置参数
					RequestParams params = new RequestParams();
					params.addBodyParameter("helpReportGson", helpReportGson);

					// 发送请求
					HttpUtils http = new HttpUtils();
					http.send(HttpRequest.HttpMethod.POST, url, params,
							new RequestCallBack<String>() {

								@SuppressWarnings("static-access")
								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									Toast toast = new Toast(
											ReportDonationActivity.this);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.makeText(ReportDonationActivity.this,
											"发送失败，请检查您的网络设置",
											Toast.LENGTH_SHORT).show();

								}

								@SuppressWarnings("static-access")
								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									Toast toast = new Toast(
											ReportDonationActivity.this);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.makeText(ReportDonationActivity.this,
											"您的举报信息已成功发送,我们会对您的信息进行处理,谢谢您的配合！",
											Toast.LENGTH_SHORT).show();
									ReportDonationActivity.this.finish();
								}
							});

				} else {
					Toast.makeText(this, "内容很重要", Toast.LENGTH_SHORT).show();
					break;
				}
			} else {
				Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
			}

		}

	}
}
