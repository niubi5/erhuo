package com.geminno.erhuo.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Donates;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Helps;
import com.geminno.erhuo.utils.MyAdapter;
import com.geminno.erhuo.utils.ViewHolder;
import com.geminno.erhuo.view.RefreshListView;
import com.geminno.erhuo.view.RefreshListView.OnRefreshCallBack;

public class DonationFragment extends BaseFragment implements OnClickListener{
	private Context context;
	private List<Donates> listDonations = new ArrayList<Donates>();
	private List<Helps> listHelps = new ArrayList<Helps>();
	private MyAdapter<Donates> DonatesAdapter;
	private MyAdapter<Helps> HelpsAdapter;
	private RefreshListView rlvDonate;
	private Handler handler;
	private Button btnDonate;
	private Button btnHelp;
	
	public DonationFragment(Context context) {
		super();
		this.context = context;
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_donation, null);

		return view;
	}
	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		Helps h1 = new Helps();
		h1.setTitle("御寒衣物");
		h1.setPubTime("2016-02-01 10:05:30");
		h1.setDetail("山区的孩子们缺少冬季御寒衣物");

		
		Helps h2 = new Helps();
		h2.setTitle("御寒衣物");
		h2.setPubTime("2016-02-01 10:05:30");
		h2.setDetail("山区的孩子们缺少冬季御寒衣物");
		
		Helps h3 = new Helps();
		h3.setTitle("御寒衣物");
		h3.setPubTime("2016-02-01 10:05:30");
		h3.setDetail("山区的孩子们缺少冬季御寒衣物");
		
		listHelps.add(h1);
		listHelps.add(h2);
		listHelps.add(h3);
		
		
		Donates d1 = new Donates();
		d1.setTitle("书籍");
		d1.setBrief("旧的学习资料及故事书");
		d1.setDonTime("2016-03-19 08:15:12");
		d1.setLogisticsCom("圆通物流");
		d1.setLogisticsNum("45456456465483131");
		
		Donates d2 = new Donates();
		d2.setTitle("书籍");
		d2.setBrief("旧的学习资料及故事书");
		d2.setDonTime("2016-03-19 08:15:12");
		d2.setLogisticsCom("圆通物流");
		d2.setLogisticsNum("45456456465483131");
		
		Donates d3 = new Donates();
		d3.setTitle("书籍");
		d3.setBrief("旧的学习资料及故事书");
		d3.setDonTime("2016-03-19 08:15:12");
		d3.setLogisticsCom("圆通物流");
		d3.setLogisticsNum("45456456465483131");
		listDonations.add(d1);
		listDonations.add(d2);
		listDonations.add(d3);
		
		rlvDonate.setAdapter(DonatesAdapter);
		initEvent();
	}

	@Override
	protected void initEvent() {
		btnDonate.setOnClickListener(this);
		btnHelp.setOnClickListener(this);
	}

	@Override
	protected void initView() {
		rlvDonate = (RefreshListView) getView().findViewById(R.id.rlv_donate);
		btnDonate = (Button) getView().findViewById(R.id.btn_donate_donation);
		btnHelp = (Button) getView().findViewById(R.id.btn_donate_help);
		
		handler = new Handler();
		rlvDonate.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						rlvDonate.completeRefresh();
					}
				}, 2000);
			}

			@Override
			public void onPull() {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						rlvDonate.completePull();
					}
				}, 2000);
			}
		});
		
		DonatesAdapter = new MyAdapter<Donates>(context, listDonations,
				R.layout.mygoods_donate_donation_item) {
			@Override
			public void convert(ViewHolder holder, Donates t) {
				holder.setText(R.id.tv_donate_donation_name, t.getTitle());
				holder.setText(R.id.tv_donate_donation_brief, t.getBrief());
				holder.setText(R.id.tv_donate_donation_wuliu_name, t.getLogisticsCom());
				holder.setText(R.id.tv_donate_donation_wuliu_num, t.getLogisticsNum());
				holder.setText(R.id.tv_donate_donation_time,t.getDonTime());
			}
		};
		HelpsAdapter = new MyAdapter<Helps>(context,listHelps,R.layout.mygoods_donate_help_item) {
			@Override
			public void convert(ViewHolder holder, Helps t) {
				holder.setText(R.id.tv_donate_help_name, t.getTitle());
				holder.setText(R.id.tv_donate_help_time, t.getPubTime());
				holder.setText(R.id.tv_donate_help_brief, t.getDetail());
			}
		};
		initData();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_donate_donation:
			rlvDonate.setAdapter(DonatesAdapter);
			break;
		case R.id.btn_donate_help:
			rlvDonate.setAdapter(HelpsAdapter);
			break;
		default:
			break;
		}
		
	}

}
