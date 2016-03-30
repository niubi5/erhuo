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

public class BoughtFragment extends BaseFragment {
	private Context context;
	private List<Goods> listBought = new ArrayList<Goods>();
	private MyAdapter<Goods> adapter;
	private RefreshListView rlvBought;
	private Handler handler;

	public BoughtFragment(Context context) {
		super();
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bought, null);

		return view;
	}

	@Override
	protected void initData() {
		// state:1未发货，2:运输中，3:已完成
		Goods g1 = new Goods();
		g1.setName("笔记本4");
		g1.setSoldPrice(100);
		g1.setBuyPrice(100);
		g1.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
				+ "【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
		g1.setTypeId(1);
		g1.setMarketId(1);
		g1.setState(1);

		Goods g2 = new Goods();
		g2.setName("笔记本5");
		g2.setSoldPrice(200);
		g2.setBuyPrice(200);
		g2.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
				+ "【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
		g2.setTypeId(1);
		g2.setMarketId(1);
		g2.setState(2);

		Goods g3 = new Goods();
		g3.setName("笔记本6");
		g3.setSoldPrice(300);
		g3.setBuyPrice(300);
		g3.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
				+ "【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
		g3.setTypeId(3);
		g3.setMarketId(1);
		g3.setState(3);

		listBought.add(g1);
		listBought.add(g2);
		listBought.add(g3);
		rlvBought.setAdapter(adapter);

	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		rlvBought = (RefreshListView) getView().findViewById(R.id.rlv_bought);
		handler = new Handler();
		rlvBought.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						rlvBought.completeRefresh();
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
						rlvBought.completePull();
					}
				}, 2000);
			}
		});
		adapter = new MyAdapter<Goods>(context, listBought,
				R.layout.mygoods_bought_item) {
			@Override
			public void convert(ViewHolder holder, Goods t) {
				holder.setText(R.id.tv_bought_name, t.getName());
				holder.setText(R.id.tv_bought_price, "¥ " + t.getSoldPrice());
				holder.setText(R.id.tv_bought_buyprice, "原价¥ " + t.getBuyPrice());
				// tv.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
				holder.setText(R.id.tv_bought_brief, t.getImformation());
				holder.setText(R.id.tv_bought_type, "手机电脑");
				holder.setText(R.id.tv_bought_market, "数码市场");
				Log.i("adapterconvert", t.getState() + "," + t.getName());
				if (t.getState() == 3) {
					//holder.setImageResource(R.id.iv_sold, R.drawable.sold_icon);
					holder.setVisibility(R.id.iv_bought, 1);
					holder.setText(R.id.btn_bought_edit, "已完成");
					//holder.setTextColorColor(R.id.btn_bought_edit, getResources().getColor(R.color.selling_blue));
					holder.setDrawableLeft(R.id.btn_bought_edit, getResources()
							.getDrawable(R.drawable.iconfont_gougou_blue));
				} else if (t.getState() == 1) {
					holder.setText(R.id.btn_bought_edit, "提醒发货");
					holder.setDrawableLeft(R.id.btn_bought_edit, getResources()
							.getDrawable(R.drawable.iconfont_tixing));
				} else if (t.getState() == 2) {
					holder.setText(R.id.btn_bought_edit, "运输中");
					holder.setDrawableLeft(R.id.btn_bought_edit, getResources()
							.getDrawable(R.drawable.iconfont_wuliu));
				}
			}
		};
		initData();

	}

}
