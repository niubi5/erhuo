package com.geminno.erhuo.fragment;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.UserInfoProvider;
import io.rong.imlib.model.UserInfo;

import java.util.List;

import com.geminno.erhuo.R;
import com.geminno.erhuo.adapter.MessagePageAdapter;
import com.geminno.erhuo.entity.Messages;
import com.geminno.erhuo.utils.Friend;
import com.geminno.erhuo.view.PullToFreshListView;
import com.geminno.erhuo.view.PullToFreshListView.OnPullTofreshCallBack;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageFragment extends BaseFragment{

	private View view;
	private Context context;
	private List<Messages> message;
	private PullToFreshListView pullToFreshListView;
	private Handler handler = new Handler();
	private List<Friend> userIdList;//
	

	public MessageFragment(Context context) {
		this.context = context;
		//RongIM.setUserInfoProvider(this, true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_message_page, null);
		return view;
	}

	@Override
	protected void initView() {
		pullToFreshListView = (PullToFreshListView) view
				.findViewById(R.id.message_refreshListView);
		initData();// 初始化数据操作
		pullToFreshListView.setOnPullToFresh(new OnPullTofreshCallBack() {
			
			@Override
			public void onRefresh() {
				handler.postDelayed(new Runnable(){

					@Override
					public void run() {
						// 刷新操作
						pullToFreshListView.completeRefresh();
					}
					
				}, 2000);
			}
		});
	}

	@Override
	protected void initData() {
		// ---------------------- 
		MessagePageAdapter adapter = new MessagePageAdapter(context, message, pullToFreshListView);
		pullToFreshListView.setAdapter(adapter);
	}

	@Override
	protected void initEvent() {
		
	}
}
