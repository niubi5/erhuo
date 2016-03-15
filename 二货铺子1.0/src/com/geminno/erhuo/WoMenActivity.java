package com.geminno.erhuo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class WoMenActivity extends Activity {

	ImageView returnImageView;
	ImageView xuanze;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wo_men);
		returnImageView=(ImageView) findViewById(R.id.ib_women_return);
	    xuanze=(ImageView) findViewById(R.id.ib_xuanze);
	}
	
	public void onclick(View v){
		Intent intent=new Intent(this,SheZhiActivity.class);
		startActivity(intent);
	}
	
	public void click(View v){
		AlertDialog.Builder builder=new Builder(this);
		final String[] items=new String[]{
			"分享",
			"在浏览器中打开",
				
		};
		builder.setSingleChoiceItems(items, -1, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(WoMenActivity.this, "你选择的是："+items[arg1], 0).show();
				arg0.dismiss();
			}
		});
		builder.show();
	}

}
