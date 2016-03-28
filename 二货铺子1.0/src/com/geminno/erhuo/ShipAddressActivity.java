package com.geminno.erhuo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.geminno.erhuo.adapter.HomePageAdapter.ViewHolderType;
import com.geminno.erhuo.entity.Address;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.Url;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.ViewInjectInfo;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ShipAddressActivity extends Activity implements OnClickListener {

	private TextView shipName;
	private TextView shipPhone;
	private TextView shipdiqu;
	private TextView shipdizhi;
	private Users users;
	private ListView linship;
	private ViewHolderType viewHolderType = null;
	private Address address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ship_address);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		findViewById(R.id.ib_address_return).setOnClickListener(this);
		findViewById(R.id.but_address_xin).setOnClickListener(this);
		linship = (ListView) findViewById(R.id.list_ship);
		// showAddress();
		Log.i("cheshi", "地址3:"+address);
	    linship.setAdapter(new MyAdapter());

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// 解析布局文件
			if (convertView == null) {
				View v = LayoutInflater.from(ShipAddressActivity.this).inflate(
						R.layout.ship_address_layout, null);
				shipName = (TextView) findViewById(R.id.tv_ship_name);
				shipPhone = (TextView) findViewById(R.id.et_ship_phone);
				shipdiqu = (TextView) findViewById(R.id.tv_address_qu);
				shipdizhi = (TextView) findViewById(R.id.tv_ship_specific);
				Log.i("cheshi", "地址1:"+address);
				address=MyApplication.getCurUserDefAddress();
				shipName.setText(address.getName());
				shipPhone.setText(address.getPhone());
				String shipaddress = address.getAddress()
						.toString();
				String adsString = shipaddress.substring(0,
						shipaddress.indexOf("市"));
				Log.i("cheshi", "截取地址" + adsString);
				shipdiqu.setText(adsString + "市");
				if (shipaddress.indexOf("市") != -1) {
					shipdiqu.setText(shipaddress.substring(0,
							shipaddress.indexOf("市")) + "市");
				} else {
					shipdiqu.setText(shipaddress);
				}
				shipdizhi.setText(shipaddress);
			}
			return convertView;
		}

	}

	
	// private void showAddress() {
	// Intent intent=getIntent();
	// String name=intent.getStringExtra("name");
	// String phone=intent.getStringExtra("phone");
	// String diqu=intent.getStringExtra("diqu");
	// String dizhi=intent.getStringExtra("dizhi");
	// Log.i("cheshi", name+phone+diqu+dizhi);
	// if
	// (name!=null&&!name.equals("null")&&phone!=null&&!phone.equals("null")&&diqu!=null&&!diqu.equals("null")&&dizhi!=null&&!dizhi.equals("null"))
	// {
	// shipName.setText(name);
	// shipPhone.setText(phone);
	// shipdiqu.setText(diqu);
	// shipdizhi.setText(dizhi);
	// }
	//
	// }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_address_return:
			this.finish();
			break;
		case R.id.but_address_xin:
			startActivity(new Intent(this, NewAddressActivity.class));
			break;

		default:
			break;
		}
	}

}
