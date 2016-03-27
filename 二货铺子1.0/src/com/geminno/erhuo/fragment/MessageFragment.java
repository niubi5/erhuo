package com.geminno.erhuo.fragment;

import com.geminno.erhuo.R;
import com.geminno.erhuo.view.RefreshListView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageFragment extends BaseFragment {

	private View view;
	private RefreshListView refreshListView;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_message_page, null);
		return view;
	}
	
	@Override
	protected void initView() {
		refreshListView = (RefreshListView) view.findViewById(R.id.message_refreshListView);
		
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub

	}


}
