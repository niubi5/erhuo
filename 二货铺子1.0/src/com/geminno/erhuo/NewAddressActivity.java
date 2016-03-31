package com.geminno.erhuo;

import com.geminno.erhuo.entity.Address;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.Url;
import com.google.gson.Gson;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewAddressActivity extends Activity implements OnClickListener{
	
    private TextView etxianshi;  //定位显示
    private EditText etname;//收货人
    private EditText etnewphone;//收货人电话
    private EditText etdiqu;//地区
    private EditText etdizhi;//详细地址
    private ImageView ivReturn;
	private String address;
	private String isdefault="no";
	private Users users;
	private String receiptName;
	private String receiptPhone;
	private String receiptdiqu;
	private String receiptdizhi;
	private Boolean flg = true;
	private String id=null;
	private String url;;
    //private Button butshiyong;//使用按钮
    //private TextView tvdingwei;//定位按钮
    //private Button butbaochun;//保存按钮
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_address);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
	    create();
	    getCurAddress();
	}
   
	private void create() {
		etxianshi=(TextView) findViewById(R.id.et_newaddress_xianshi);
		etname=(EditText) findViewById(R.id.et_newaddress_name);
		etnewphone=(EditText) findViewById(R.id.et_newphone);
		etdiqu=(EditText) findViewById(R.id.et_newaddress_diqu);
		etdizhi=(EditText) findViewById(R.id.et_dizhi);
		ivReturn = (ImageView) findViewById(R.id.ib_newaddress_return);
		findViewById(R.id.check_moren).setOnClickListener(this);
		findViewById(R.id.but_shiyong).setOnClickListener(this);
		findViewById(R.id.but_baochun).setOnClickListener(this);
		findViewById(R.id.tv_dingwei).setOnClickListener(this);
		ivReturn.setOnClickListener(this);
		Intent intent=getIntent();
		String name=intent.getStringExtra("name");
		String phone=intent.getStringExtra("phone");
		String diqu=intent.getStringExtra("diqu");
		String dizhi=intent.getStringExtra("dizhi");
		id = intent.getStringExtra("id");
		Log.i("cheshi", "传来的值"+name+phone+diqu+dizhi+id);
		if (name!=null && !name.equals("null")&& phone!=null&&  !phone.equals("null") && diqu!=null&& !diqu.equals("null")&& dizhi!=null&&!dizhi.equals("null")) {
			flg=false;
			etname.setText(name);
			etnewphone.setText(phone);
			etdiqu.setText(diqu);
			etdizhi.setText(dizhi);
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		//定位
		case R.id.tv_dingwei:
//			if (MyApplication.getLocation()!=null) {
//				address = MyApplication.getLocation().getAddrStr().toString();
//				etxianshi.setText(address);
//				Log.i("cheshi", "定位2："+address);
//			}else {
//				etxianshi.setText("定位失败");
//			}
			
			break;
		//保存信息
			
		case R.id.check_moren:
			isdefault="yes";
			break;
        case R.id.but_baochun:
        	users = MyApplication.getCurrentUser();
        	if (users!=null&&!users.equals("null")) {
        		receiptName = etname.getText().toString();
    			receiptPhone = etnewphone.getText().toString().trim();
    			receiptdiqu = etdiqu.getText().toString();
    			receiptdizhi = etdizhi.getText().toString();
    			Log.i("cheshi","收货"+receiptName+ receiptPhone+receiptdiqu+receiptdizhi);
    			if (receiptName.length()!=0 && !receiptName.equals("null") && receiptPhone.length()!=0 && !receiptPhone.equals("null") && receiptdiqu.length()!=0 && !receiptdiqu.equals("null") && receiptdizhi.length()!=0 && !receiptdizhi.equals("null")) {
    				Log.i("cheshi","默认"+isdefault);
    				Address ads=new Address();
    				ads.setUserId(users.getId());
    				Log.i("cheshi", "传来id:"+id);
    				ads.setId(Integer.parseInt(id));
    				ads.setName(receiptName);
    				ads.setPhone(receiptPhone);
    				ads.setAddress(receiptdiqu+receiptdizhi);
    				ads.setIsdefault(isdefault);
    				if(ads.getIsdefault().equals("yes")){
    					MyApplication.setCurUserDefAddress(ads);
    				}
    				String adds=new Gson().toJson(ads);
    				HttpUtils httpUtils=new HttpUtils();
    				RequestParams params=new RequestParams();
    				params.addBodyParameter("address",adds);
    				String headUrl = Url.getUrlHead();
    				if (flg) {
//    					url = headUrl + "/SaveAddressServlet";
        				 url="http://10.201.1.16:8080/secondHandShop/SaveAddressServlet";
					}else {
//					    url = headUrl + "/UpdateAddressServlet";
	    				 url="http://10.201.1.16:8080/secondHandShop/UpdateAddressServlet";
					}
    				
//    				String url="http://10.201.1.16:8080/secondHandShop/SaveAddressServlet";
    				httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(NewAddressActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							String result=arg0.result;
							if (result != null && !result.equals("null")) {
								Toast.makeText(NewAddressActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
								Intent intent=new Intent(NewAddressActivity.this,ShipAddressActivity.class);
//			    				intent.putExtra("name", receiptName);
//			    				intent.putExtra("phone", receiptPhone);
//			    				intent.putExtra("diqu", receiptdiqu);
//			    				intent.putExtra("dizhi", receiptdizhi);
			    				startActivity(intent);
			    				ShipAddressActivity.shipAddressActivity.finish();
			    				finish();
							}
						}
					});
    				
    			}else {
    				Toast.makeText(this, "请输入完整信息！", Toast.LENGTH_SHORT).show();
    			}
			}else {
				Toast.makeText(this,"您还未登陆", Toast.LENGTH_SHORT).show();
			}
        	
			break;
		//使用定位了的信息	
        case R.id.but_shiyong:
        	if (address!=null) {
				String newaddress=address.substring(0,address.indexOf("市"));
				etdiqu.setText(newaddress+"市");
				etdizhi.setText(address.substring(address.indexOf("市")+1));
				Log.i("cheshi",newaddress );
			}
        	break;
        case R.id.ib_newaddress_return:
        	finish();
        	break;
		default:
			break;
		}
		
	}
	
	public void getCurAddress(){
		if (MyApplication.getLocation()!=null) {
			address = MyApplication.getLocation().getAddrStr().toString();
			etxianshi.setText(address);
			Log.i("cheshi", "定位2："+address);
		}else {
			etxianshi.setText("定位失败");
		}
	}

	
	
	
	
	
}
