package com.gem.erhuo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.gem.erhuo.EditUserInfoActivity;
import com.gem.erhuo.R;

public class UserInfoFragment extends BaseFragment implements OnClickListener{

	private LinearLayout userInfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_userinfo_page, null);
		return view;
	}

	@Override
	protected void initData() {
		
	}

	@Override
	protected void initEvent() {
		userInfo.setOnClickListener(this);
	}

	@Override
	protected void initView() {
		userInfo = (LinearLayout) getView().findViewById(R.id.userinfo_container);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.userinfo_container:
			// 跳转到EditUserInfoActivity
			startActivity(new Intent(getActivity(), EditUserInfoActivity.class));
			break;
		}
	}

}
