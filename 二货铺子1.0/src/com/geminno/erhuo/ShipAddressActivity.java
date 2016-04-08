package com.geminno.erhuo;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.adapter.HomePageAdapter.ViewHolderType;
import com.geminno.erhuo.entity.Address;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.Url;
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
	private List<Address> listad;	
	public static Activity shipAddressActivity;
	private String jumpActivity = null;
	private List<Address> addresseslist=new ArrayList<Address>();
	private Address addOnclick;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ship_address);
		shipAddressActivity = this;
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		context = this;
		Intent intent = getIntent();
		//Log.i("jumpActivity", getIntent().getStringExtra("jumpActivity"));
		//Toast.makeText(this, getIntent().getStringExtra("jumpActivity"), 1).show();
		if(intent.getStringExtra("jumpActivity") != null){
			
			jumpActivity = intent.getStringExtra("jumpActivity");			
		}
		findViewById(R.id.ib_address_return).setOnClickListener(this);
		findViewById(R.id.but_address_xin).setOnClickListener(this);
		users = MyApplication.getCurrentUser();
		Log.i("cheshi", "用户:" + users.toString());
		linship = (ListView) findViewById(R.id.list_ship);
		if (users!=null) {
			create();
		}
	  	
	}

	
	class MyAdapter extends BaseAdapter {
		 List<Address> listad;
		 Context context;
		private String addressid=null;
		private String listgetname;
		private String listgetphone;
		private String listgetid;
		private String listgetaddress;
		
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// 解析布局文件
			if (addresseslist.isEmpty()) {
				addresseslist.add(listad.get(position));
			}else {
				for(Address  adds:addresseslist){
					if (adds.getId()!=listad.get(position).getId()) {
						addresseslist.add(listad.get(position));
					}
				}
			}
			
			ViewHolder viewHolder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(context)
						.inflate(R.layout.ship_address_layout, null);
				viewHolder.shipName = (TextView) convertView.findViewById(R.id.tv_ship_name);
				viewHolder.shipPhone = (TextView) convertView.findViewById(R.id.et_ship_phone);
				viewHolder.shipdizhi = (TextView) convertView.findViewById(R.id.tv_address_qu);
				viewHolder.shipmoren=(ImageView) convertView.findViewById(R.id.iv_moren);
				viewHolder.shipbianji=(Button)convertView.findViewById(R.id.ship_bianji);
				viewHolder.shipdelete=(Button) convertView.findViewById(R.id.btn_ship_delete);
				convertView.setTag(viewHolder);
				Log.i("cheshi", "集合"+listad);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Log.i("cheshi", "昵称"+listad.get(position));
			addressid = listad.get(position).getId()+"";
			listgetname = listad.get(position).getName();
			listgetphone = listad.get(position).getPhone();
			listgetaddress = listad.get(position).getAddress().toString();
			viewHolder.shipName.setText(listgetname);
			viewHolder.shipPhone.setText(listgetphone);
			addOnclick = addresseslist.get(position);
			viewHolder.shipbianji.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(ShipAddressActivity.this,NewAddressActivity.class);
					intent.putExtra("name", addOnclick.getName());
					intent.putExtra("phone", addOnclick.getPhone());
					intent.putExtra("id", addOnclick.getId());
					intent.putExtra("Address", addOnclick.getAddress());
					
					Log.i("cheshi", "取出id:"+listgetid);
					startActivity(intent);
					
				}
			});
			viewHolder.shipdelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					HttpUtils httpUtils=new HttpUtils();
					httpUtils.configCurrentHttpCacheExpiry(0);
					RequestParams params=new RequestParams();
				    String headUrl = Url.getUrlHead();
				    String url = headUrl + "/DeleteAddressServlet";
					params.addQueryStringParameter("addressid", addOnclick.getId()+"");
					httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							String result=arg0.result;
							if (result.equals("ok")) {
								Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
							    create();
							}
						}
					});
				}
			});
			String moren=listad.get(position).getIsdefault().toString();
			if (moren.equals("yes")) {
				viewHolder.shipmoren.setVisibility(viewHolder.shipmoren.VISIBLE);
			}else {
				viewHolder.shipmoren.setVisibility(viewHolder.shipmoren.INVISIBLE);
			}
			viewHolder.shipdizhi.setText(listgetaddress);
			return convertView;
		}

	}

	private void create() {
		String userId=users.getId()+"";
		HttpUtils httpUtils=new HttpUtils();
		httpUtils.configCurrentHttpCacheExpiry(0);
		RequestParams params=new RequestParams();
		String headUrl = Url.getUrlHead();
		String url = headUrl + "/AddressListServlet";
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
					Log.i("CurAddress", result);
//					
					Log.i("cheshi", "地址对象："+listad);
					//MyApplication.setCurUserDefAddress(address);
					linship.setAdapter(new MyAdapter(listad, context));
					//Item点击事件
					linship.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?>parent, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							//Toast.makeText(shipAddressActivity, jumpActivity, Toast.LENGTH_SHORT).show();
							if(jumpActivity == null && !"".equals(jumpActivity)){
								Log.i("cheshi", "点击事件，跳转");
							if (listad.get(position).getIsdefault().equals("yes")) {
								MyApplication
								.setCurUserDefAddress(listad.get(position));
							}
//							startActivity(intent);
							}else{
								MyApplication.setUseAddress(listad.get(position));
								finish();
							}
							
						}
					});
				}
			}
		});
	}
	

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
		TextView shipdizhi;
		ImageView shipmoren;
		Button shipdelete;
		Button shipbianji;
		
	}

}
