package com.geminno.erhuo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.geminno.erhuo.entity.Donates;
import com.geminno.erhuo.entity.Helps;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.view.RoundCornerImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

public class DonateDetialActivity extends Activity {
	private Donates donate;
	private TextView tvDonateName;
	private TextView tvDonateBrief;
	private TextView tvDonateLogisCom;
	private TextView tvDonateLogisNum;
	private TextView tvDonateTime;
	private RoundCornerImageView rciv;
	private TextView tvHelpName;
	private TextView tvHelpTime;
	private TextView tvHelpBrief;
	private TextView tvHelpReceiveName;
	private TextView tvHelpReceiveAddress;
	
	private String headUrl;
	private Map<Helps, List<String>> donateMap = new HashMap<Helps, List<String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_donate_detial);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		
		headUrl = Url.getUrlHead();
		initData();
		initView();
	}
	
	//初始化数据
	public void initData(){
		donate = (Donates) getIntent().getSerializableExtra("donate");
		
		//发送请求获得求助信息
		HttpUtils hu = new HttpUtils();
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("helpId",donate.getHelpId()+"");
		String url = headUrl +"/GetHelpServlet";
		
		hu.send(HttpRequest.HttpMethod.POST, url, rp, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(DonateDetialActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				if(arg0 != null && !"null".equals(arg0.result)){
					rciv = (RoundCornerImageView) findViewById(R.id.rciv_donate_detial_help);
					tvHelpName = (TextView) findViewById(R.id.tv_donate_detial_help_name);
					tvHelpBrief = (TextView) findViewById(R.id.tv_donate_detial_help_brief);
					tvHelpTime = (TextView) findViewById(R.id.tv_donate_detial_help_time);
					tvHelpReceiveName = (TextView) findViewById(R.id.tv_donate_detial_help_receive_name);
					tvHelpReceiveAddress = (TextView) findViewById(R.id.tv_donate_detial_help_receive_address);
					
					Log.i("HelpResult", arg0.result);
					Gson gson = new Gson();
					Type type = new TypeToken<Map<Helps, List<String>>>(){}.getType();
					donateMap = gson.fromJson(arg0.result, type);
					Set<Entry<Helps, List<String>>> keySet = donateMap.entrySet();
					Helps help = new Helps();
					List<String> urls = new ArrayList<String>();
					for (Map.Entry<Helps, List<String>> e : keySet) {
						help = e.getKey();
						urls = e.getValue();
					}
					if(!urls.isEmpty()){
						ImageLoader.getInstance().displayImage(urls.get(0), rciv);
					}
					tvHelpName.setText("标题："+help.getTitle());
					tvHelpBrief.setText("详情："+help.getDetail());
					tvHelpTime.setText("发布时间："+help.getPubTime());
					tvHelpReceiveAddress.setText("收货地址："+help.getAddress());
					tvHelpReceiveName.setText("收货人："+help.getConsignee());
				}
			}
		});
		
	}
	
	//初始化界面
	public void initView(){
		tvDonateName = (TextView) findViewById(R.id.tv_donate_detial_name);
		tvDonateBrief = (TextView) findViewById(R.id.tv_donate_detial_brief);
		tvDonateTime = (TextView) findViewById(R.id.tv_donate_detial_time);
		tvDonateLogisCom = (TextView) findViewById(R.id.tv_donate_detial_wuliu_name);
		tvDonateLogisNum = (TextView) findViewById(R.id.tv_donate_detial_wuliu_num);
		
		tvDonateName.setText("标题："+donate.getTitle());
		tvDonateBrief.setText("描述："+donate.getBrief());
		tvDonateLogisCom.setText("快递名称："+donate.getLogisticsCom());
		tvDonateLogisNum.setText("快递单号："+donate.getLogisticsNum());
		tvDonateTime.setText("捐助时间："+donate.getDonTime());
	}
	
	//界面点击事件
	public void onclick(View v){
		switch (v.getId()) {
		case R.id.iv_donate_detial_return:
			finish();
			break;

		default:
			break;
		}
	}
}
