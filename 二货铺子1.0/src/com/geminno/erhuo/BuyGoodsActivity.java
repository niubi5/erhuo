package com.geminno.erhuo;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Users;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class BuyGoodsActivity extends Activity {
	private Users user;
	private Goods good;
	private String[] goodUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_buy_goods);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		
		user = (Users) getIntent().getSerializableExtra("user");
		good = (Goods) getIntent().getSerializableExtra("good");
		goodUrl = getIntent().getStringArrayExtra("url");
		initData();
	}
	
	//点击事件
	public void onclick(View v){
		switch (v.getId()) {
		case R.id.iv_buy_return:
			finish();
			break;
		default:
			break;
		}
	}
	//显示数据
	public void initData(){
		ImageView ivGoodImage = (ImageView) findViewById(R.id.iv_buy_good);
		TextView tvGoodName = (TextView) findViewById(R.id.tv_goods_name);
		TextView tvGoodBrief = (TextView) findViewById(R.id.tv_goods_imformation);
		TextView tvGoodPrice = (TextView) findViewById(R.id.tv_goods_price);
		TextView tvGoodPay = (TextView) findViewById(R.id.tv_goods_pay);
		Properties prop = new Properties();
		String headUrl = null;
		try {
			prop.load(PublishGoodsActivity.class.getResourceAsStream("/com/geminno/erhuo/utils/url.properties"));
			headUrl = prop.getProperty("headUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		final String userHeadUrl = headUrl + goodUrl[0];
		ImageLoader.getInstance().displayImage(goodUrl[0], ivGoodImage);
		tvGoodName.setText(good.getName());
		tvGoodBrief.setText(good.getImformation());
		tvGoodPrice.setText("¥ "+good.getSoldPrice());
		tvGoodPay.setText("¥ "+good.getSoldPrice());
	}

}
