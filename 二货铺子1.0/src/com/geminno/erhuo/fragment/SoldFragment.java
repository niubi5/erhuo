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
import android.widget.ListView;

import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.utils.MyAdapter;
import com.geminno.erhuo.utils.ViewHolder;
import com.geminno.erhuo.view.RefreshListView;
import com.geminno.erhuo.view.RefreshListView.OnRefreshCallBack;

public class SoldFragment extends BaseFragment {

	private Context context;
	private List<Goods> listSold = new ArrayList<Goods>();
	private MyAdapter<Goods> adapter;
	private RefreshListView rlvSold;
	private Handler handler;

	public SoldFragment(Context context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sold, null);
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

		listSold.add(g1);
		listSold.add(g2);
		listSold.add(g3);
		rlvSold.setAdapter(adapter);

	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		rlvSold = (RefreshListView) getView().findViewById(R.id.rlv_sold);
		handler = new Handler();
		rlvSold.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						rlvSold.completeRefresh();
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
						rlvSold.completePull();
					}
				}, 2000);
			}
		});
		adapter = new MyAdapter<Goods>(context, listSold,
				R.layout.mygoods_sold_item) {
			@Override
			public void convert(ViewHolder holder, Goods t) {
				holder.setText(R.id.tv_sold_name, t.getName());
				holder.setText(R.id.tv_sold_price, "¥ " + t.getSoldPrice());
				holder.setText(R.id.tv_sold_buyprice, "原价¥ " + t.getBuyPrice());
				// tv.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
				holder.setText(R.id.tv_sold_brief, t.getImformation());
				holder.setText(R.id.tv_sold_type, "手机电脑");
				holder.setText(R.id.tv_sold_market, "数码市场");
				Log.i("adapterconvert", t.getState() + "," + t.getName());
				if (t.getState() == 4) {
					//holder.setImageResource(R.id.iv_sold, R.drawable.sold_icon);
					holder.setVisibility(R.id.iv_sold, 1);
					holder.setText(R.id.btn_sold_edit, "已完成");
					//holder.setTextColorColor(R.id.btn_bought_edit, getResources().getColor(R.color.selling_blue));
					holder.setDrawableLeft(R.id.btn_sold_edit, getResources()
							.getDrawable(R.drawable.iconfont_gougou_blue));
				} else if (t.getState() == 2) {
					holder.setText(R.id.btn_sold_edit, "去发货");
					holder.setDrawableLeft(R.id.btn_sold_edit, getResources()
							.getDrawable(R.drawable.iconfont_fahuo));
				} else if (t.getState() == 3) {
					holder.setText(R.id.btn_sold_edit, "运输中");
					holder.setDrawableLeft(R.id.btn_sold_edit, getResources()
							.getDrawable(R.drawable.iconfont_wuliu));
				}
			}
		};
		initData();

	}

}
