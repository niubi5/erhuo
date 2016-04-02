package com.geminno.erhuo;

import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.view.RoundCornerImageView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class OrderDetialActivity extends Activity {
	private Goods good;
	private String goodUrl;
	private String orderJson;
	
	//界面控件
	private RoundCornerImageView rciv;
	private TextView tvGoodName;
	private TextView tvGoodPrice;
	private TextView tvGoodOldPrice;
	private TextView tvGoodBrief;
	private TextView tvGoodType;
	private TextView tvGoodMarket;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_order_detial);
		
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));	
		
		initData();
		initView();
	}
	
	//初始化界面参数
	public void initView(){
		//初始化控件
		rciv = (RoundCornerImageView) findViewById(R.id.rciv_order_detial);
		tvGoodName = (TextView) findViewById(R.id.tv_order_detial_name);
		tvGoodPrice = (TextView) findViewById(R.id.tv_order_detial_price);
		tvGoodOldPrice = (TextView) findViewById(R.id.tv_order_detial_buyprice);
		tvGoodBrief = (TextView) findViewById(R.id.tv_order_detial_brief);
		tvGoodType = (TextView) findViewById(R.id.tv_order_detial_type);
		tvGoodMarket = (TextView) findViewById(R.id.tv_myorder_detial_market);
		
		ImageLoader.getInstance().displayImage(goodUrl, rciv);
		tvGoodName.setText(good.getName());
		tvGoodPrice.setText(good.getSoldPrice()+"");
		tvGoodOldPrice.setText(good.getBuyPrice()+"");
		tvGoodBrief.setText(good.getImformation());
		tvGoodType.setText(getTypeName(good.getTypeId()));
		tvGoodMarket.setText(good.getMarketId() == 0 ? "无集市信息": getMarketName(good.getMarketId()));
	}
	//初始化数据
	public void initData(){
		good = (Goods) getIntent().getSerializableExtra("good");
		goodUrl = getIntent().getStringExtra("url");
		
		//发送请求获取订单详情
		HttpUtils hu = new HttpUtils();
		RequestParams rp = new RequestParams();
		rp.addBodyParameter("goodId",good.getId()+"");
		String headUrl = Url.getHeikkiUrlHead();
		String url = headUrl+"";
		/**
		 * @heikki 04.01 22.34
		 * */
		//hu.send(HttpRequest.HttpMethod.POST, url, callBack)
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
