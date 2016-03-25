package com.geminno.erhuo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewAddressActivity extends Activity implements OnClickListener{
	
    private EditText etxianshi;  //定位显示
    private EditText etname;//收货人
    private EditText etnewphone;//收货人电话
    private EditText etdiqu;//地区
    private EditText etdizhi;//详细地址
	private String address;
    //private Button butshiyong;//使用按钮
    //private TextView tvdingwei;//定位按钮
    //private Button butbaochun;//保存按钮
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_address);
	    create();
	}
   
	private void create() {
		etxianshi=(EditText) findViewById(R.id.et_newaddress_xianshi);
		etname=(EditText) findViewById(R.id.et_newaddress_name);
		etnewphone=(EditText) findViewById(R.id.et_newphone);
		etdiqu=(EditText) findViewById(R.id.et_newaddress_diqu);
		etdizhi=(EditText) findViewById(R.id.et_dizhi);
		findViewById(R.id.but_shiyong).setOnClickListener(this);
		findViewById(R.id.but_baochun).setOnClickListener(this);
		findViewById(R.id.tv_dingwei).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		//定位
		case R.id.tv_dingwei:
			address = MyApplication.getLocation().getAddrStr().toString();
			if (address!=null) {
				etxianshi.setText(address);
			}
			break;
		//保存信息	
        case R.id.but_baochun:
			if (etname.getText()!=null&&etnewphone!=null&&etdiqu.getText()!=null&&etdizhi!=null) {
				
			}else {
				Toast.makeText(this, "请输入完整信息！", Toast.LENGTH_SHORT).show();
			}
			break;
		//使用定位了的信息	
        case R.id.but_shiyong:
        	if (address!=null) {
				String newaddress=address.substring(0,address.indexOf("市"));
				etdiqu.setText(newaddress);
			}
        	break;
		default:
			break;
		}
		
	}

	
	
	
	
	
}
