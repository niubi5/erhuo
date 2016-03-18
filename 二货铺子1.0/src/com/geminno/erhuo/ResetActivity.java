package com.geminno.erhuo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ResetActivity extends Activity implements OnClickListener{

	

	EditText etpwd;
	EditText etPwdagain;
	Button butreset;
	ImageView ivreset;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_reset);
		 //调用setColor()方法,实现沉浸式状态栏

	  	MainActivity.setColor(this, getResources().getColor(R.color.login_background));
	    etpwd=(EditText) findViewById(R.id.et_pwd_reset);
	    etPwdagain=(EditText) findViewById(R.id.et_pwd_resetagain);
	    butreset=(Button) findViewById(R.id.btn_reset);
	    ivreset=(ImageView) findViewById(R.id.iv_return_reset);
	    ivreset.setOnClickListener(this);
	    butreset.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_reset:
			String pwd=etpwd.getText().toString();
			String pwdagain=etPwdagain.getText().toString();
			if(pwd==null){
				toast("请输入密码");
				return;
			}else if (pwdagain==null) {
				toast("请再次输入密码");
				return;
			}else if (pwd!=pwdagain&&!pwd.equals(pwdagain)) {
				toast("两次输入密码不一样,请输入正确密码");
				return;
			}else {
				Intent intent=new Intent(this,LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.iv_return_reset:
			this.finish();
			break;

		default:
			break;
		}
		
		
	}
	
	private void toast(final String str) {

        runOnUiThread(new Runnable() {

            @Override

            public void run() {

                Toast.makeText(ResetActivity.this, str, Toast.LENGTH_SHORT).show();

            }

        });

    }

	

	
}
