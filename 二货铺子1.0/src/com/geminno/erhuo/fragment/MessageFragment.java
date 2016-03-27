package com.geminno.erhuo.fragment;

import java.util.List;

import com.geminno.erhuo.R;
import com.geminno.erhuo.adapter.MessagePageAdapter;
import com.geminno.erhuo.entity.Messages;
import com.geminno.erhuo.view.RefreshListView;
import com.geminno.erhuo.view.RefreshListView.OnRefreshCallBack;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageFragment extends BaseFragment {

	private View view;
	private Context context;
	private List<Messages> message;
	private RefreshListView refreshListView;
	private Handler handler = new Handler();

	public MessageFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_message_page, null);
		return view;
	}

	@Override
	protected void initView() {
		refreshListView = new RefreshListView(context, false);
		refreshListView = (RefreshListView) view
				.findViewById(R.id.message_refreshListView);
		initData();
		refreshListView.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						refreshListView.completeRefresh();
					}

				}, 2000);
			}

			@Override
			public void onPull() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						refreshListView.completePull();
					}

				}, 2000);
			}
		});
	}

	@Override
	protected void initData() {
		// ---------------------- 
		MessagePageAdapter adapter = new MessagePageAdapter(context, message);
		refreshListView.setAdapter(adapter);
	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub

	}

}
