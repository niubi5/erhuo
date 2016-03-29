package com.geminno.erhuo;

import java.lang.reflect.Type;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.geminno.erhuo.adapter.HomePageAdapter.ViewHolderType;
import com.geminno.erhuo.entity.Address;
import com.geminno.erhuo.entity.Users;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class ShipAddressActivity extends Activity implements OnClickListener {
	private Context context;
	private Users users;
	private ListView linship;
	private ViewHolderType viewHolderType = null;
	private Address address;
	private List<Address> listad;
	private String name;
	private String phone;
	private String diqu;
	private String dizhi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ship_address);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		context = this;
		findViewById(R.id.ib_address_return).setOnClickListener(this);
		findViewById(R.id.but_address_xin).setOnClickListener(this);
		users = MyApplication.getCurrentUser();
		Log.i("cheshi", "用户:" + users.toString());
		linship = (ListView) findViewById(R.id.list_ship);
		if (users!=null) {
			create();
		}
		// showAddress();
		
	}
	
	class MyAdapter extends BaseAdapter {
		 List<Address> listad;
		 Context context;
		
		 public MyAdapter(List<Address> listad,Context context) {
			this.listad=listad;
			this.context=context;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listad.size();
		}
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return listad.get(arg0);
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
			ViewHolder viewHolder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(context)
						.inflate(R.layout.ship_address_layout, null);
				viewHolder.shipName = (TextView) convertView.findViewById(R.id.tv_ship_name);
				viewHolder.shipPhone = (TextView) convertView.findViewById(R.id.et_ship_phone);
				viewHolder.shipdiqu = (TextView) convertView.findViewById(R.id.tv_address_qu);
				viewHolder.shipdizhi = (TextView) convertView.findViewById(R.id.tv_ship_specific);
				viewHolder.shipmoren=(ImageView) convertView.findViewById(R.id.iv_moren);
				convertView.setTag(viewHolder);
				Log.i("cheshi", "集合"+listad);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Log.i("cheshi", "昵称"+listad.get(position));
			viewHolder.shipName.setText(listad.get(position).getName());
			viewHolder.shipPhone.setText(listad.get(position).getPhone());
			String moren=listad.get(position).getIsdefault().toString();
			if (moren.equals("yes")) {
				viewHolder.shipmoren.setVisibility(viewHolder.shipmoren.VISIBLE);
			}else {
				viewHolder.shipmoren.setVisibility(viewHolder.shipmoren.INVISIBLE);
			}
			String shipaddress = listad.get(position).getAddress().toString();
			if (shipaddress.indexOf("市") != -1) {
				viewHolder.shipdiqu.setText(shipaddress.substring(0,
						shipaddress.indexOf("市"))
						+ "市");
			} else {
				viewHolder.shipdiqu.setText(shipaddress);
			}
			viewHolder.shipdizhi.setText(shipaddress);
//			name = viewHolder.shipName.getText().toString();
//			phone = viewHolder.shipPhone.getText().toString();
//			diqu = viewHolder.shipdiqu.getText().toString();
//			dizhi = viewHolder.shipdizhi.getText().toString();
			return convertView;
		}

	}

	private void create() {
		
		Log.i("cheshi", "进来的users"+users.toString());
		String userId=users.getId()+"";
		HttpUtils httpUtils=new HttpUtils();
		RequestParams params=new RequestParams();
//		String headUrl = Url.getUrlHead();
//		String url = headUrl + "/UserAddressServlet";
		String url="http://10.201.1.16:8080/secondHandShop/AddressListServlet";
		params.addQueryStringParameter("curUserId",userId);
		httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("cheshi", "请求失败");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				String result=arg0.result;
				if (result!=null&&!result.equals("null")) {
					Gson gson = new Gson();
					Type type = new TypeToken<List<Address>>(){}.getType();
					listad = gson.fromJson(
							result,type);
					Log.i("cheshi", "地址对象："+listad);
					//MyApplication.setCurUserDefAddress(address);
					linship.setAdapter(new MyAdapter(listad, context));
					//Item点击事件
					linship.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?>parent, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							Log.i("cheshi", "点击事件，跳转");
							Intent intent=new Intent(ShipAddressActivity.this,NewAddressActivity.class);
							intent.putExtra("name", listad.get(position).getName());
							intent.putExtra("phone", listad.get(position).getPhone());
							String shipaddress = listad.get(position).getAddress().toString();
							String diqu=null;
							if (shipaddress.indexOf("市") != -1) {
								 diqu=shipaddress.substring(0,shipaddress.indexOf("市"))+ "市";
							} else {
							     diqu=shipaddress;
							}
							intent.putExtra("diqu", diqu);
							intent.putExtra("dizhi", shipaddress);
							Log.i("cheshi", "跳转传值:"+name+phone+diqu+dizhi);
							startActivity(intent);
						}
					});
				}
			}
		});
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

	public class ViewHolder {
		TextView shipName;
		TextView shipPhone;
		TextView shipdiqu;
		TextView shipdizhi;
		ImageView shipmoren;
	}

}
