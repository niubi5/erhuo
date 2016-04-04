package com.geminno.erhuo;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.geminno.erhuo.entity.Donates;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Helps;
import com.geminno.erhuo.utils.MyAdapter;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.utils.ViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HelpDetialActivity extends Activity {
	private String headUrl;
	private List<Donates> listDoante;
	private MyAdapter<Donates> myAdapter;
	private ListView lvDonate;
	private TextView tvNoDonate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_help_detial);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));

		tvNoDonate = (TextView) findViewById(R.id.tv_no_donate);
		headUrl = Url.getUrlHead();
		initData();
	}

	// 初始化数据
	public void initData() {
		Helps help = (Helps) getIntent().getSerializableExtra("help");
		lvDonate = (ListView) findViewById(R.id.lv_help_detail_receive);
		
		// 发送请求获取该求助的所有捐赠
		HttpUtils hu = new HttpUtils();
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("helpId", help.getId() + "");
		String url = headUrl + "/GetHelpAllDonateServlet";

		hu.send(HttpRequest.HttpMethod.POST, url, rp,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(HelpDetialActivity.this, "网络错误",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						if (arg0 != null && !"null".equals(arg0.result)) {
							Log.i("HelpDonate", arg0.result);
							String result = arg0.result;// 获得响应结果
							Gson gson = new Gson();
							Type type = new TypeToken<List<Donates>>() {}.getType();
							listDoante = gson.fromJson(arg0.result, type);
							Log.i("HelpDonate", "listDoante size:"+listDoante.isEmpty());
							if(listDoante.isEmpty()){
								tvNoDonate.setVisibility(View.VISIBLE);
							}else{
								tvNoDonate.setVisibility(View.INVISIBLE);
							}
							myAdapter = new MyAdapter<Donates>(HelpDetialActivity.this,listDoante,R.layout.help_donate_item) {

								@Override
								public void convert(ViewHolder holder, Donates t) {
									holder.setText(R.id.tv_help_donate_name, "标题："+t.getTitle());
									holder.setText(R.id.tv_help_donate_brief, "内容："+t.getBrief());
									holder.setText(R.id.tv_help_donate_time, "捐赠时间："+t.getDonTime());
									holder.setText(R.id.tv_help_donate_wuliu_name, "快递公司："+t.getLogisticsCom());
									holder.setText(R.id.tv_help_donate_wuliu_num, "快递单号："+t.getLogisticsNum());
								}
							};
							lvDonate.setAdapter(myAdapter);
						}

					}
				});
	}
	
	
	public void onclick(View v){
		switch (v.getId()) {
		case R.id.iv_help_detial_return:
			finish();
			break;

		default:
			break;
		}
	}
}
