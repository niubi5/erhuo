package com.geminno.erhuo.market;

import com.geminno.erhuo.R;
import com.geminno.erhuo.R.layout;

import android.content.Context;
import android.os.Bundle;

public class MarketThreeActivity extends MarketBaseActivity {
	
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_market);
		context = this;
		this.initView(3, 2, context);
	}

}
