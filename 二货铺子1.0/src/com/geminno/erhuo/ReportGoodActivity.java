package com.geminno.erhuo;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import com.geminno.erhuo.entity.GoodsReports;
import com.geminno.erhuo.utils.MySdf;
import com.geminno.erhuo.utils.Url;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
		goodId = getIntent().getIntExtra("goodId", 0);
	}

	public void onclick(View v) {
		switch (v.getId()) {
		case R.id.iv_report_return:
			finish();
			break;
		case R.id.btn_commit_report:
			RadioGroup rgReason = (RadioGroup) findViewById(R.id.rg_report_reason);
			RadioButton rbSelected = (RadioButton) findViewById(rgReason
					.getCheckedRadioButtonId());
			String reason = rbSelected.getText().toString();
			EditText etReason = (EditText) findViewById(R.id.et_reason);
			if (TextUtils.isEmpty(etReason.getText())) {
//				Toast.makeText(this, goodId + reason, Toast.LENGTH_SHORT)
//						.show();
			} else {
				reason = reason + "%" + etReason.getText().toString();
//				Toast.makeText(this,
//						goodId + reason + "&" + etReason.getText().toString(),
//						Toast.LENGTH_SHORT).show();
			}
			//int USERID = 3;// 测试，正式发布应从MyApplication.getCurrentUser().getId()获取
			//int 
			int USERID = MyApplication.getCurrentUser().getId();
			GoodsReports gr = new GoodsReports();
			gr.setGoodId(goodId);
			gr.setUserId(USERID);
			gr.setBrief(reason);
			gr.setRepTime(MySdf.getDateToString(new Date(System
					.currentTimeMillis())));
			gr.setState(1);
			// 将举报信息封装成GoodsReports对象，并转换成Json数据
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
					.create();
			String goodsReportsJson = gson.toJson(gr);
			// 从配置文件获取服务器url
//			String url = prop.getProperty("heikkiUrl")
//					+ "";
			String url = Url.getUrlHead() + "/AddGoodsReportsServlet";
			RequestParams rp = new RequestParams();
			// 添加数据
			rp.addBodyParameter("goodsReportsJson", goodsReportsJson);
			HttpUtils hu = new HttpUtils();
			// 发送请求
			hu.send(HttpRequest.HttpMethod.POST, url,rp,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							Log.i("goodsReport", "失败");
							Toast.makeText(ReportGoodActivity.this, "网络错误!", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							Log.i("goodsReport", "成功");
							AlertDialog.Builder builder = new Builder(ReportGoodActivity.this);
							builder.setMessage("我们会及时处理您的举报信息！"+"\n"+"感谢您对二货铺子的支持，我们会越做越好！");
							builder.setTitle("举报成功");
							builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
										finish();
										GoodsDetialActivity.goodsDetialActivity.finish();
								}
							});
							builder.create().show();
						}
					});
			break;
		default:
			break;
		}
	}

}
