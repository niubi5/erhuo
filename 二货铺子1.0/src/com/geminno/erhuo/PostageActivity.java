package com.geminno.erhuo;

import java.util.ArrayList;
import java.util.Arrays;

import com.geminno.erhuo.view.WheelView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class PostageActivity extends Activity implements View.OnClickListener {

	private static final String TAG = PostageActivity.class.getCanonicalName();
	private static final String[] PLAENTS = new String[] { "北京市", "天津市", "重庆市",
			"上海市", "河北省", "山西省", "辽宁省", "吉林省", "黑龙江省", "江苏省", "浙江省", "安徽省",
			"福建省", "江西省", "山东省", "河南省", "湖北省", "湖南省", "广东省", "海南省", "四川省",
			"贵州省", "云南省", "陕西省", "甘肃省", "青海省", "台湾省", "内蒙古自治区", "广西壮族自治区",
			"西藏自治区", "宁夏回族自治区", "新疆维吾尔自治区", "香港特别行政区", "澳门特别行政区" };
	private static final String[] KG = new String[] { "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "10" };

	private TextView tvjichu;
	private TextView tvmudi;
	private TextView tvkg;
	private TextView tvmoney;
	private static Toast mToast = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_postage);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		findViewById(R.id.lin_mudidi).setOnClickListener(this);
		findViewById(R.id.lin_zhongliang).setOnClickListener(this);
		findViewById(R.id.lin_jichudi).setOnClickListener(this);
		findViewById(R.id.ib_shezhi).setOnClickListener(this);
		tvjichu = (TextView) findViewById(R.id.tv_jichu);
		tvmudi = (TextView) findViewById(R.id.tv_mudidi);
		tvkg = (TextView) findViewById(R.id.et_wuzhong);
		tvmoney = (TextView) findViewById(R.id.tv_money);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.lin_jichudi:
			View otherView = LayoutInflater.from(this).inflate(
					R.layout.wheel_view, null);
			WheelView wheel = (WheelView) otherView
					.findViewById(R.id.wheel_view_wv);
			wheel.setOffset(2);
			wheel.setItems(Arrays.asList(PLAENTS));
			wheel.setSeletion(3);
			wheel.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
				@Override
				public void onSelected(int selectedIndex, String item) {
					Log.i("cheshi", "[Dialog]selectedIndex: " + selectedIndex
							+ ", item: " + item);
					Log.d(TAG, "[Dialog]selectedIndex: " + selectedIndex
							+ ", item: " + item);
					tvjichu.setText(item);
				};
			});
			new AlertDialog.Builder(this).setTitle("选择地址").setView(otherView)
					.setPositiveButton("确定", null).show();
			break;
		case R.id.lin_mudidi:
			View otherView1 = LayoutInflater.from(this).inflate(
					R.layout.wheel_view, null);
			WheelView wheel1 = (WheelView) otherView1
					.findViewById(R.id.wheel_view_wv);
			wheel1.setOffset(2);
			wheel1.setItems(Arrays.asList(PLAENTS));
			wheel1.setSeletion(3);
			wheel1.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
				@Override
				public void onSelected(int selectedIndex, String item) {
					tvmudi.setText(item);
					Log.d(TAG, "[Dialog]selectedIndex: " + selectedIndex
							+ ", item: " + item);
				};

			});
			new AlertDialog.Builder(this).setTitle("选择地址").setView(otherView1)
					.setPositiveButton("确定", null).show();
			break;
		case R.id.lin_zhongliang:
			View otherView2 = LayoutInflater.from(this).inflate(
					R.layout.wheel_view, null);
			WheelView wheel2 = (WheelView) otherView2
					.findViewById(R.id.wheel_view_wv);
			wheel2.setOffset(2);
			wheel2.setItems(Arrays.asList(KG));
			wheel2.setSeletion(3);
			wheel2.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
				@Override
				public void onSelected(int selectedIndex, String item) {
					tvkg.setText(item);
					//
					// if(selectedIndex==5&&selectedIndex==11&&selectedIndex==12){
					// String mo=parseInt(item)*8+10+"";
					// tvmoney.setText(mo);
					// }else if (selectedIndex==30&&selectedIndex==32) {
					//
					// }
					tvmoney.setText("¥"+parseInt(item)+"元");

					Log.i("cheshi", "[Dialog]selectedIndex: " + selectedIndex
							+ ", item: " + item);
					Log.d(TAG, "[Dialog]selectedIndex: " + selectedIndex
							+ ", item: " + item);
				}

				private int parseInt(String item) {
					// TODO Auto-generated method stub
					return Integer.parseInt(item)*8+6;
				};

			});
			new AlertDialog.Builder(this).setTitle("选择重量").setView(otherView2)
					.setPositiveButton("确定", null).show();
			break;

		case R.id.ib_shezhi:
			this.finish();
			break;
		case R.id.lianxikuaidi:
			showToast(PostageActivity.this, "很抱歉，暂时还没有与快递公司合作，您需自行联系快递！", Toast.LENGTH_LONG);
		default:
			break;
		}

	}
	
	public static void showToast(Context context, String text, int duration) {  
        if (mToast == null) {  
            mToast = Toast.makeText(context, text, duration);  
        } else {  
            mToast.setText(text);  
            mToast.setDuration(duration);  
        }  
  
        mToast.show();  
    }

	
}
