package com.geminno.erhuo.market;

import android.content.Context;
import android.os.Bundle;

import com.geminno.erhuo.R;

public class MarketFourActivity extends MarketBaseActivity {
	
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_market);
		context = this;
		this.initView(4, 3, context);
	}

}
