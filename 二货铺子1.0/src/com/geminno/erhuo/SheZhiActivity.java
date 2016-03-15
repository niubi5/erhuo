package com.geminno.erhuo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SheZhiActivity extends Activity implements OnClickListener{

	
	LinearLayout linxiugaimima;
	LinearLayout linyijianfankui;
	LinearLayout linguanyuwomen;
	LinearLayout linjianchagenxin;
	LinearLayout linqingchuhuancun;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_she_zhi);
		linxiugaimima=(LinearLayout) findViewById(R.id.lin_xiugai_mima);
	    linyijianfankui=(LinearLayout) findViewById(R.id.lin_yijian_fankui);
	    linguanyuwomen=(LinearLayout) findViewById(R.id.lin_guanyu_women);
	    linjianchagenxin=(LinearLayout) findViewById(R.id.lin_jiancha_gengxin);
	    linqingchuhuancun=(LinearLayout) findViewById(R.id.lin_qingchu_huancun);	
	    linxiugaimima.setOnClickListener(this);
	    linyijianfankui.setOnClickListener(this);
	    linjianchagenxin.setOnClickListener(this);
	    linguanyuwomen.setOnClickListener(this);
	    linqingchuhuancun.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.lin_xiugai_mima:
			Intent intent1=new Intent(this,LoginActivity.class);
			startActivity(intent1);
			break;
		case R.id.lin_yijian_fankui:
			Intent intent2=new Intent(this,LoginActivity.class);
			startActivity(intent2);
			break;
		case R.id.lin_guanyu_women:
			Intent intent3=new Intent(this,WoMenActivity.class);
			startActivity(intent3);
			break;
		case R.id.lin_jiancha_gengxin:
			
			break;
		case R.id.lin_qingchu_huancun:
			
			break;	

		default:
			break;
		}
	}

	
}
