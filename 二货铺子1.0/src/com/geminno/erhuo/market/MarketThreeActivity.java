package com.geminno.erhuo.market;

import com.geminno.erhuo.MainActivity;
import com.geminno.erhuo.R;

import android.content.Context;
import android.os.Bundle;

public class MarketThreeActivity extends MarketBaseActivity {
	
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_market);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		context = this;
		this.initView(3, 2, context);
	}

}
