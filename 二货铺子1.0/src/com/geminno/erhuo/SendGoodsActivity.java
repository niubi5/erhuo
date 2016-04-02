package com.geminno.erhuo;

import java.util.Date;

import com.geminno.erhuo.utils.GetExpressageCom;
import com.geminno.erhuo.utils.MySdf;
import com.geminno.erhuo.utils.Url;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SendGoodsActivity extends Activity {
	private EditText etCom;
	private EditText etNum;
	private int goodId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_goods);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));

		etCom = (EditText) findViewById(R.id.et_expressage_com);
		etNum = (EditText) findViewById(R.id.et_expressage_num);

		goodId = getIntent().getIntExtra("sendGoodsId", -1);
	}

	public void onclick(View v) {
		switch (v.getId()) {
		case R.id.iv_send_return:
			finish();
			break;
		case R.id.btn_send_commit:
			String com = GetExpressageCom.getExpressageName(etCom.getText()
					.toString().trim());
			String num = etNum.getText().toString().trim();
			// Toast.makeText(this,com+num,Toast.LENGTH_SHORT).show();
			if (com != null && !"".equals(com) && num != null
					&& !"".equals(num)) {
				Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
				// 准备请求参数
				HttpUtils hu = new HttpUtils();
				RequestParams rp = new RequestParams();
				rp.addBodyParameter("goodId", goodId + "");
				rp.addBodyParameter("com", com);
				rp.addBodyParameter("num", num);
				rp.addBodyParameter("sendTime", MySdf.getDateToString(new Date(
						System.currentTimeMillis())));
				String headUrl = Url.getHeikkiUrlHead();
				String url = headUrl + "/UpdateOrderServlet";
				// 发送请求
				hu.send(HttpRequest.HttpMethod.POST, url, rp,
						new RequestCallBack<String>() {

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								Toast.makeText(SendGoodsActivity.this, "网络错误!",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								Toast.makeText(SendGoodsActivity.this, "发货成功!",
										Toast.LENGTH_SHORT).show();
								finish();
							}
						});
			}
			break;
		default:
			break;
		}
	}
}
