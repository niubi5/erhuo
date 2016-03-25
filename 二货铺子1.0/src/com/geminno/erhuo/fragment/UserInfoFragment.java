package com.geminno.erhuo.fragment;

import com.geminno.erhuo.EditUserInfoActivity;
import com.geminno.erhuo.LoginActivity;
import com.geminno.erhuo.MainActivity;
import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.PostageActivity;
import com.geminno.erhuo.R;
import com.geminno.erhuo.SheZhiActivity;
import com.geminno.erhuo.ShipAddressActivity;
import com.geminno.erhuo.entity.Users;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class UserInfoFragment extends BaseFragment implements OnClickListener {

	private LinearLayout userInfo;
	private Button btnHead;
	private LinearLayout linearshezhi;
	private LinearLayout linearyoufei;
	private LinearLayout address;
	private Users users;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_userinfo_page, null);
		
		return view;
	}

	@Override
	protected void initData() {
		initEvent();
	}

	@Override
	protected void initEvent() {
		userInfo.setOnClickListener(this);
		btnHead.setOnClickListener(this);
		linearshezhi.setOnClickListener(this);
		linearyoufei.setOnClickListener(this);
		address.setOnClickListener(this);
	}

	@Override
	protected void initView() {
		userInfo = (LinearLayout) getView().findViewById(
				R.id.userinfo_container);
		btnHead = (Button) getView().findViewById(R.id.userinfo_btn_herder);
	    linearshezhi=(LinearLayout) getView().findViewById(R.id.setting_container);
	    linearyoufei=(LinearLayout) getView().findViewById(R.id.postage_container);
	    address=(LinearLayout) getView().findViewById(R.id.address_container);
	    initData();
	    users = MyApplication.getCurrentUser();
	    if(users!=null){
	    	btnHead.setText(users.getName());
	    }
	    }

	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userinfo_container:
			// 跳转到EditUserInfoActivity
			Log.i("onClick", "userinfo_container");
			startActivity(new Intent(getActivity(), EditUserInfoActivity.class));
			break;
		case R.id.userinfo_btn_herder:
			Log.i("onClick", "userinfo_btn_herder");
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case R.id.setting_container:
			Log.i("onClick", "setting_container");
			startActivity(new Intent(getActivity(), SheZhiActivity.class));
			break;
		case R.id.postage_container:
			Log.i("onClick", "postage_container");

			startActivity(new Intent(getActivity(),PostageActivity.class));
			break;	
		case R.id.address_container:
			startActivity(new Intent(getActivity(),ShipAddressActivity.class));
			break;
			

//			startActivity(new Intent(getActivity(), PostageActivity.class));
//			break;
		}
	}

}
