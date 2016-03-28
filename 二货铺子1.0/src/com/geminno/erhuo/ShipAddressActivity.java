package com.geminno.erhuo;

import java.lang.reflect.Type;

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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class ShipAddressActivity extends Activity implements OnClickListener{

	private TextView shipName;
	private TextView shipPhone;
	private TextView shipdiqu;
	private TextView shipdizhi;
    private Users users;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ship_address);
		//调用setColor()方法,实现沉浸式状态栏
	  	MainActivity.setColor(this, getResources().getColor(R.color.main_red));
	  	create();
	  	showAddress();
	  	
	}
	private void create() {
		shipName = (TextView) findViewById(R.id.tv_ship_name);
		shipPhone = (TextView) findViewById(R.id.et_ship_phone);
		shipdiqu = (TextView) findViewById(R.id.tv_address_qu);
		shipdizhi = (TextView) findViewById(R.id.tv_ship_specific);
		findViewById(R.id.ib_address_return).setOnClickListener(this);
	  	findViewById(R.id.but_address_xin).setOnClickListener(this);
		users = MyApplication.getCurrentUser();
		Log.i("CurrentUser", users.toString());
		String userId=users.getId()+"";
		HttpUtils httpUtils=new HttpUtils();
		RequestParams params=new RequestParams();
		String headUrl = Url.getUrlHead();
		String url = headUrl + "/UserAddressServlet";
		//String url="http://10.201.1.16:8080/secondHandShop/UserAddressServlet";
		params.addQueryStringParameter("userid",userId);
		httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				String result=arg0.result;
				if (users!=null&&!users.equals("null")) {
					Gson gson = new Gson();
					Type type = new TypeToken<Address>(){}.getType();
					Address address =  gson.fromJson(
							result,type);
					MyApplication.setCurUserDefAddress(address);
					shipName.setText(address.getName());
					shipPhone.setText(address.getPhone());
					String shipaddress=address.getAddress().toString();
					if(shipaddress.indexOf("市") != -1){
						shipdiqu.setText(shipaddress.substring(0,shipaddress.indexOf("市"))+"市");						
					}else{
						shipdiqu.setText(shipaddress);
					}
					shipdizhi.setText(shipaddress);
				}
			}
		});
	}
	
	

	private void showAddress() {
		Intent intent=getIntent();
		String name=intent.getStringExtra("name");
		String phone=intent.getStringExtra("phone");
		String diqu=intent.getStringExtra("diqu");
		String dizhi=intent.getStringExtra("dizhi");
		Log.i("cheshi", name+phone+diqu+dizhi);
		if (name!=null&&!name.equals("null")&&phone!=null&&!phone.equals("null")&&diqu!=null&&!diqu.equals("null")&&dizhi!=null&&!dizhi.equals("null")) {
			shipName.setText(name);
			shipPhone.setText(phone);
			shipdiqu.setText(diqu);
			shipdizhi.setText(dizhi);
		}
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_address_return:
			this.finish();
			break;
		case R.id.but_address_xin:
			startActivity(new Intent(this,NewAddressActivity.class));
			break;

		default:
			break;
		}
	}

	
	
}
