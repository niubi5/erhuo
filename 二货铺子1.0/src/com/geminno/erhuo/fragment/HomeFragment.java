package com.geminno.erhuo.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.geminno.erhuo.MainActivity;
import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Markets;
import com.geminno.erhuo.utils.HomePageAdapter;
import com.geminno.erhuo.view.ImageCycleView;
import com.geminno.erhuo.view.RefreshListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class HomeFragment extends BaseFragment {
	
	private ImageCycleView mAdView;
	private View  convertView;
	private RefreshListView refreshListView;
	private List<Markets> listMarkets = new ArrayList<Markets>();
	private List<Goods> listGoods = new ArrayList<Goods>();
	
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

	}

	@Override
	protected void initEvent() {
		
	}

	@Override
	protected void initView() {
		refreshListView = (RefreshListView) getView().findViewById(R.id.refreshListView);
	}

}
