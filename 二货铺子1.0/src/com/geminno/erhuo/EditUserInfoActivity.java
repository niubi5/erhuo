package com.geminno.erhuo;

import com.geminno.erhuo.entity.Address;
import com.geminno.erhuo.entity.Url;
import com.geminno.erhuo.entity.Users;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditUserInfoActivity extends Activity implements OnClickListener {

	private TextView save;
	private ImageView editHeader;
	private EditText nickName;
	private ImageView male;
	private ImageView female;
    private EditText address;
    private TextView phone;
    ImageView userreturn;
	private int sex;
    private Users users;
  //  private ImageView male;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_user_info);
		users = MyApplication.getCurrentUser();
		initView();
		if (users!=null&&!users.equals("null")) {
			userShow();
			if(users.getIdentity()!=null){
				sendAddress();
			}
		}
		Log.i("cheshi", "users"+users);
		
	}
	
	private void userShow(){
		
		if(users.getName()!=null){
		  nickName.setText(users.getName());
		}
		String sex=users.getSex()+"";
		if(sex!=null&&users.getSex()==1){
		    	male.setSelected(true);
		}else if(sex!=null){
				female.setSelected(true);
		}
		if (users.getIdentity()!=null) {
			phone.setText(users.getIdentity());
		}
	    
	    
	}

	private void initView() {
		//保存
		save = (TextView) findViewById(R.id.tv_infodata_ok);
		//头像
		editHeader = (ImageView) findViewById(R.id.edit_header);
		//昵称
		nickName = (EditText) findViewById(R.id.et_infodata_nickname);
		//男点击按钮
		male = (ImageView) findViewById(R.id.chose_male);
		//女点击按钮
		female = (ImageView) findViewById(R.id.chose_female);
		//地址
	    address = (EditText) findViewById(R.id.et_infodata_address);
	    //电话
	    phone = (TextView) findViewById(R.id.tv_infodata_phone);
	    //返回
		userreturn= (ImageView) findViewById(R.id.ib_infodata_return);
		save.setOnClickListener(this);
		editHeader.setOnClickListener(this);
		nickName.setOnClickListener(this);
		male.setOnClickListener(this);
		female.setOnClickListener(this);
		userreturn.setOnClickListener(this);
	    address.setOnClickListener(this);
		// phone.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//保存
		case R.id.tv_infodata_ok:
			String name=nickName.getText().toString();
			
			break;
		 case R.id.edit_header:
		
		 break;
		case R.id.et_infodata_nickname:
			

			break;

		// case R.id.tv_infodata_ok:
		//
		// break;
		// case R.id.edit_header:
		//
		// break;
		// case R.id.et_infodata_nickname:
		//
		// break;
		case R.id.ib_infodata_return:
			this.finish();
			break;
		case R.id.chose_male:
			male.setSelected(true);
			female.setSelected(false);
			sex = 0;
			break;
		case R.id.chose_female:
			male.setSelected(false);
			female.setSelected(true);
			sex = 1;
			break;
		// case R.id.tv_infodata_ok:
		//
		// break;
		// case R.id.tv_infodata_ok:
		//
		// break;
		}
	}
	
	private void sendAddress() {
		String phone=users.getIdentity();
		HttpUtils http=new HttpUtils();
		RequestParams params=new RequestParams();
		params.addQueryStringParameter("phone",phone);
		// 服务器路径
//		String headUrl = Url.getUrlHead();
//		String url = headUrl + "/UserAddressServlet";
		String url="http://10.201.1.16:8080/secondHandShop/UserAddressServlet";
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				String result=arg0.result;
				Log.i("cheshi", "地址："+result);
				if(result!=null&&!result.equals("null")){
					Gson gson = new Gson();
					Address usersAddress =  gson.fromJson(
							result, Address.class);
					address.setText(usersAddress.getAddress());
				}else {
					address.setText("未设置地址");
				}
			}
		});
	}

}
