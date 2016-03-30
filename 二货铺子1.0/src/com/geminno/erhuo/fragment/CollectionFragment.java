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
import android.view.ViewGroup;

import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.utils.MyAdapter;
import com.geminno.erhuo.utils.ViewHolder;
import com.geminno.erhuo.view.RefreshListView;
import com.geminno.erhuo.view.RefreshListView.OnRefreshCallBack;

public class CollectionFragment extends BaseFragment {
	private Context context;
	private List<Goods> listCollec = new ArrayList<Goods>();
	private MyAdapter<Goods> adapter;
	private RefreshListView rlvCollec;
	private Handler handler;
	
	public CollectionFragment(Context context) {
		super();
		this.context = context;
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_collec, null);

		return view;
	}
	@Override
	protected void initData() {
		// state:1在售中，2:未发货，3:已发货，4:已完成
				Goods g1 = new Goods();
				g1.setName("笔记本1");
				g1.setSoldPrice(100);
				g1.setBuyPrice(100);
				g1.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
						+ "【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
				g1.setTypeId(1);
				g1.setMarketId(1);
				g1.setState(2);

				Goods g2 = new Goods();
				g2.setName("笔记本2");
				g2.setSoldPrice(200);
				g2.setBuyPrice(200);
				g2.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
						+ "【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
				g2.setTypeId(1);
				g2.setMarketId(1);
				g2.setState(3);

				Goods g3 = new Goods();
				g3.setName("笔记本3");
				g3.setSoldPrice(300);
				g3.setBuyPrice(300);
				g3.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
						+ "【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
				g3.setTypeId(1);
				g3.setMarketId(1);
				g3.setState(4);

				listCollec.add(g1);
				listCollec.add(g2);
				listCollec.add(g3);
				rlvCollec.setAdapter(adapter);

	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		rlvCollec = (RefreshListView) getView().findViewById(R.id.rlv_collec);
		handler = new Handler();
		rlvCollec.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						rlvCollec.completeRefresh();
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
						rlvCollec.completePull();
					}
				}, 2000);
			}
		});
		adapter = new MyAdapter<Goods>(context, listCollec,
				R.layout.goods_item) {
			@Override
			public void convert(ViewHolder holder, Goods t) {
				holder.setText(R.id.user_name, "erhuo8315");
				holder.setText(R.id.goods_name, t.getName());
				holder.setText(R.id.goods_price, "¥ " + t.getSoldPrice());
				holder.setText(R.id.goods_pubtime, "2016-03-31");
				holder.setText(R.id.goods_info, t.getImformation());
			}
		};
		initData();

	}

}
