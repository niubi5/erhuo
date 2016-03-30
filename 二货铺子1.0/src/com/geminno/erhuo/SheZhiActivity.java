package com.geminno.erhuo;

import java.util.ArrayList;

import org.apache.http.client.UserTokenHandler;

import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.ActivityCollector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class SheZhiActivity extends BaseActivity implements OnClickListener {

	LinearLayout linxiugaimima;
	LinearLayout linyijianfankui;
	LinearLayout linguanyuwomen;
	LinearLayout linjianchagenxin;
	LinearLayout linqingchuhuancun;
	ImageView imageshezh;
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_she_zhi);
		linxiugaimima = (LinearLayout) findViewById(R.id.lin_xiugai_mima);
		linyijianfankui = (LinearLayout) findViewById(R.id.lin_yijian_fankui);
		linguanyuwomen = (LinearLayout) findViewById(R.id.lin_guanyu_women);
		linjianchagenxin = (LinearLayout) findViewById(R.id.lin_jiancha_gengxin);
		linqingchuhuancun = (LinearLayout) findViewById(R.id.lin_qingchu_huancun);
		imageshezh = (ImageView) findViewById(R.id.ib_shezhi);
		button = (Button) findViewById(R.id.but_tuichu);
		linxiugaimima.setOnClickListener(this);
		linyijianfankui.setOnClickListener(this);
		linjianchagenxin.setOnClickListener(this);
		linguanyuwomen.setOnClickListener(this);
		linqingchuhuancun.setOnClickListener(this);
		imageshezh.setOnClickListener(this);
		button.setOnClickListener(this);
		// 调用setColor()方法,实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.lin_xiugai_mima:
			Intent intent1 = new Intent(this, ZhaoHuiActivity.class);
			startActivity(intent1);
			break;
		case R.id.lin_yijian_fankui:
			Intent intent2 = new Intent(this, LoginActivity.class);
			startActivity(intent2);
			break;
		case R.id.lin_guanyu_women:
			Intent intent3 = new Intent(this, WoMenActivity.class);
			startActivity(intent3);
			break;
		case R.id.lin_jiancha_gengxin:

			Toast.makeText(this, "当前已是最新版本", Toast.LENGTH_SHORT).show();

			break;
		case R.id.lin_qingchu_huancun:

			break;

		case R.id.ib_shezhi:
			this.finish();
			break;

		case R.id.but_tuichu:
			// 通过设定SharedPreferences中的用户名键值为空，返回登录界面，可以进行注销操作
			SharedPreferences preferences = getSharedPreferences("userInfo",MODE_PRIVATE);
			// 从SharedPreferences中读取用户名
			SharedPreferences.Editor editor = preferences.edit();
			editor.clear().commit();
			Users users=null;
            MyApplication.setUsers(users);
            MyApplication.setCurUserDefAddress(null);
			startActivity(new Intent(this, LoginActivity.class));
			ActivityCollector.finishAll();
			break;

		default:
			break;
		}
	}	
}
