package com.geminno.erhuo;

import java.util.Date;

import com.geminno.erhuo.entity.Address;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.entity.Orders;
import com.geminno.erhuo.utils.GetExpressageCom;
import com.geminno.erhuo.utils.MySdf;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.view.RoundCornerImageView;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendGoodsActivity extends Activity {
	private RoundCornerImageView rciv;
	private TextView tvGoodName;
	private TextView tvGoodPrice;
	private TextView tvGoodOldPrice;
	private TextView tvGoodBrief;
	private TextView tvGoodType;
	private TextView tvGoodMarket;
	
	private TextView tvReceiveName;
	private TextView tvReceivePhone;
	private TextView tvReceiveAddress;
	
	private EditText etCom;
	private EditText etNum;
	private TextView tvHint;
	
	
	private boolean flag = false;
	private String headUrl;
	private Goods good;
	private String goodUrl;
	private Orders order;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_goods);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		
		headUrl = Url.getHeikkiUrlHead();
		initData();
		initView();
	}
	
	public void initView(){
		rciv = (RoundCornerImageView) findViewById(R.id.rciv_send_good);
		tvGoodName = (TextView) findViewById(R.id.tv_send_good_name);
		tvGoodPrice = (TextView) findViewById(R.id.tv_send_good_price);
		tvGoodOldPrice = (TextView) findViewById(R.id.tv_send_good_buyprice);
		tvGoodBrief = (TextView) findViewById(R.id.tv_send_good_brief);
		tvGoodType = (TextView) findViewById(R.id.tv_send_good_type);
		tvGoodMarket = (TextView) findViewById(R.id.tv_mysend_good_market);
		
		tvReceiveName = (TextView) findViewById(R.id.tv_receive_name);
		tvReceivePhone = (TextView) findViewById(R.id.tv_receive_phone);
		tvReceiveAddress = (TextView) findViewById(R.id.tv_receive_address);
		
		etCom = (EditText) findViewById(R.id.et_expressage_com);
		etNum = (EditText) findViewById(R.id.et_express_num);
		tvHint = (TextView) findViewById(R.id.tv_hint);
		
		
		ImageLoader.getInstance().displayImage(goodUrl, rciv);
		tvGoodName.setText(good.getName());
		tvGoodPrice.setText("¥"+good.getSoldPrice());
		tvGoodOldPrice.setText("原价:"+good.getBuyPrice());
		tvGoodBrief.setText(good.getImformation());
		tvGoodType.setText(getTypeName(good.getTypeId()));
		tvGoodMarket.setText(good.getMarketId() == 0 ? "无集市信息"
				: getMarketName(good.getMarketId()));
	}
	//初始化数据
	public void initData(){
		good = (Goods) getIntent().getSerializableExtra("good");
		goodUrl = getIntent().getStringExtra("url");
		
		//获得订单信息
		HttpUtils hu = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("goodId", good.getId()+ "");
		String url = headUrl + "/GetGoodOrderServlet";
		hu.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Toast.makeText(SendGoodsActivity.this, "网络错误!-订单",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				if(arg0.result != null && !"null".equals(arg0.result)){
					Gson gson = new Gson();
					Orders order = gson.fromJson(arg0.result,Orders.class);
					//获得收货人信息
					HttpUtils http = new HttpUtils();
					RequestParams rp = new RequestParams();
					rp.addBodyParameter("addressId", order.getAddId() + "");
					String addressurl = headUrl + "/GetAddressServlet";
					http.send(HttpRequest.HttpMethod.POST, addressurl, rp, new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Toast.makeText(SendGoodsActivity.this, "网络错误!-地址",
									Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							if(arg0.result != null && !"null".equals(arg0.result)){
								Gson gs = new Gson();
								Address add = gs.fromJson(arg0.result, Address.class);
								tvReceiveName.setText(add.getName());
								tvReceivePhone.setText(add.getPhone());
								tvReceiveAddress.setText(add.getAddress());
							}
						}
					});
					
				}
			}
		});
	}
	//界面点击事件
	public void onclick(View v) {
		switch (v.getId()) {
		case R.id.iv_imformation:
			if(flag){
				flag = false;
				tvHint.setVisibility(View.INVISIBLE);
			}else{
				flag = true;
				tvHint.setVisibility(View.VISIBLE);				
			}
			break;
		case R.id.iv_send_return:
			finish();
			break;
		case R.id.btn_send_commit:
			String com = GetExpressageCom.getExpressageCode(etCom.getText()
					.toString().trim());
			String num = etNum.getText().toString().trim();
			// Toast.makeText(this,com+num,Toast.LENGTH_SHORT).show();
			if (com != null && !"".equals(com) && num != null
					&& !"".equals(num)) {
				Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
				// 准备请求参数
				HttpUtils hu = new HttpUtils();
				RequestParams rp = new RequestParams();
				rp.addBodyParameter("goodId", good.getId() + "");
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
	
	// 获取分类名称
		public String getTypeName(int typeId) {
			String typeName = "其他闲置";
			switch (typeId) {
			case 1:
				typeName = "其他闲置";
				break;
			case 2:
				typeName = "手机电脑";
				break;
			case 3:
				typeName = "相机数码";
				break;
			case 4:
				typeName = "书籍文体";
				break;
			case 5:
				typeName = "服装鞋包";
				break;
			case 6:
				typeName = "美容美体";
				break;
			default:
				break;
			}
			return typeName;
		}

		// 获取集市名称
		public String getMarketName(int marketId) {
			String marketName = "";
			for (Markets m : MyApplication.getMarketsList()) {
				if (m.getId() == marketId) {
					marketName = m.getName();
				}
			}
			return marketName;
		}
}
