package com.geminno.erhuo.fragment;

import java.util.ArrayList;
import java.util.List;

import com.geminno.erhuo.R;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.utils.MyAdapter;
import com.geminno.erhuo.utils.ViewHolder;
import com.geminno.erhuo.view.RefreshListView;
import com.geminno.erhuo.view.RefreshListView.OnRefreshCallBack;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class SellingFragment extends BaseFragment {
	private Context context;
	private List<Goods> listSelling = new ArrayList<Goods>();
	private MyAdapter<Goods> adapter;
	private RefreshListView rlvSelling;
	private Handler handler;
	
	public SellingFragment(Context context) {
		this.context = context;
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_selling, null);
		return view;
	}
	@Override
	protected void initData() {
		Goods g1 = new Goods();
		g1.setName("笔记本");
		g1.setSoldPrice(100);
		g1.setBuyPrice(200);
		g1.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
+"【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
		g1.setTypeId(1);
		g1.setMarketId(1);
		
		Goods g2 = new Goods();
		g2.setName("笔记本");
		g2.setSoldPrice(100);
		g2.setBuyPrice(200);
		g2.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
+"【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
		g2.setTypeId(1);
		g2.setMarketId(1);
		
		Goods g3 = new Goods();
		g3.setName("笔记本");
		g3.setSoldPrice(100);
		g3.setBuyPrice(200);
		g3.setImformation("惠普(HP)WASD 暗影精灵 15.6英寸游戏笔记本(i5-6300HQ 4G 1TB+128G SSD GTX950M 4G独显 FHD IPS屏 Win10"
+"【手机端抢4999】搭载第六代skylake处理器！更快更强！开机飞快，让你一开始你就赢了");
		g3.setTypeId(1);
		g3.setMarketId(1);
		
		listSelling.add(g1);
		listSelling.add(g2);
		listSelling.add(g3);
		rlvSelling.setAdapter(adapter);

	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		rlvSelling = (RefreshListView) getView().findViewById(R.id.rlv_selling);
		handler = new Handler();
		rlvSelling.setOnRefreshCallBack(new OnRefreshCallBack() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						rlvSelling.completeRefresh();
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
						rlvSelling.completePull();
					}
				}, 2000);
			}
		});
		adapter = new MyAdapter<Goods>(context, listSelling,R.layout.mygoods_selling_item) {

			@Override
			public void convert(ViewHolder holder, Goods t) {
				holder.setText(R.id.tv_selling_time, "剩余展示时间90天");
				holder.setText(R.id.tv_selling_name, t.getName());
				holder.setText(R.id.tv_selling_price, "¥ "+t.getSoldPrice());
				holder.setText(R.id.tv_selling_buyprice, "原价¥ "+t.getBuyPrice());
				//tv.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG ); 
				holder.setText(R.id.tv_selling_brief, t.getImformation());
				holder.setText(R.id.tv_selling_type, "手机电脑");
				holder.setText(R.id.tv_selling_market, "数码市场");
			}
		};
		initData();

	}

}
