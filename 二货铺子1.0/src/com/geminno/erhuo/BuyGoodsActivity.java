package com.geminno.erhuo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class BuyGoodsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_buy_goods);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
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

}
