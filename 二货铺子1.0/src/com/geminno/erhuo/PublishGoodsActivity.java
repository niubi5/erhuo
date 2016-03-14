package com.geminno.erhuo;

import java.util.ArrayList;
import java.util.List;

import com.geminno.erhuo.utils.MyAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

public class PublishGoodsActivity extends Activity implements OnClickListener {
	private ImageView ivback;
	private Spinner typeSpinner;
	private ArrayAdapter<String> arrAdapter;
	private List<String> typeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_publish_goods);
		typeSpinner = (Spinner) findViewById(R.id.spn_types);
		ivback = (ImageView) findViewById(R.id.iv_pub_return);
		ivback.setOnClickListener(this);
		
		typeList = new ArrayList<String>();
		typeList.add("手机电脑");
		typeList.add("服装鞋靴");
		typeList.add("书籍文体");
		typeList.add("个护化妆");
		arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,typeList);
		arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(arrAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_pub_return:
			this.finish();
			break;

		default:
			break;
		}
		
	}
}
