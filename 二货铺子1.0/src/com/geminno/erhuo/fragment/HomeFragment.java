package com.geminno.erhuo.fragment;

import com.geminno.erhuo.R;
import com.geminno.erhuo.view.ImageCycleView;
import com.geminno.erhuo.view.RefreshListView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends BaseFragment {
	
	private ImageCycleView mAdView;
	View  convertView;
	private RefreshListView refreshListView;
	
	public HomeFragment(){
		
	}
	
	public ImageCycleView getmAdView() {
		return mAdView;
	}
	
	public RefreshListView getRefreshListView(){
		return refreshListView;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		convertView = inflater.inflate(R.layout.fragment_main_page, null);
		return convertView;
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
//		mAdView = (ImageCycleView) getView().findViewById(R.id.ad_view);
		refreshListView = (RefreshListView) getView().findViewById(R.id.refreshListView);
	}

}
