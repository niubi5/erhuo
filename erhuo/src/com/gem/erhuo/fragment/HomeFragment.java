package com.gem.erhuo.fragment;

import com.gem.erhuo.R;
import com.gem.erhuo.view.ImageCycleView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends BaseFragment {
	
	private ImageCycleView mAdView;
	View  convertView;
	
	public HomeFragment(){
		
	}
	
	public ImageCycleView getmAdView() {
		return mAdView;
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
		mAdView = (ImageCycleView) getView().findViewById(R.id.ad_view);
	}

}
