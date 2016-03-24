package com.geminno.erhuo;

import com.geminno.erhuo.entity.Users;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditUserInfoActivity extends Activity implements OnClickListener {

	private TextView save;
	private ImageView editHeader;
	private EditText nickName;
	private ImageView male;
	private ImageView female;
	// private EditText address;
    private TextView phone;
    TextView userreturn;
	private int sex;
    private Users users;
  //  private ImageView male;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_user_info);
		initView();
		userShow();
	}
	
	private void userShow(){
		users = MyApplication.getCurrentUser();
		nickName.setText(users.getName());
	    if(users.getSex()==1){
	    	male.setSelected(true);
	    }else {
			female.setSelected(true);
		}
	    phone.setText(users.getIdentity());
	    
	}

	private void initView() {
		save = (TextView) findViewById(R.id.tv_infodata_ok);
		editHeader = (ImageView) findViewById(R.id.edit_header);
		nickName = (EditText) findViewById(R.id.et_infodata_nickname);
		//男点击按钮
		male = (ImageView) findViewById(R.id.chose_male);
		//女点击按钮
		female = (ImageView) findViewById(R.id.chose_female);
		// address = (EditText) findViewById(R.id.et_infodata_address);
	    phone = (TextView) findViewById(R.id.tv_infodata_phone);
		userreturn=(TextView) findViewById(R.id.ib_infodata_return);
		save.setOnClickListener(this);
		editHeader.setOnClickListener(this);
		nickName.setOnClickListener(this);
		male.setOnClickListener(this);
		female.setOnClickListener(this);
		userreturn.setOnClickListener(this);
		// address.setOnClickListener(this);
		// phone.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_infodata_ok:
			
			break;
		 case R.id.edit_header:
		
		 break;
		case R.id.et_infodata_nickname:
			

			break;

		// case R.id.tv_infodata_ok:
		//
		// break;
		// case R.id.edit_header:
		//
		// break;
		// case R.id.et_infodata_nickname:
		//
		// break;
		case R.id.ib_infodata_return:
			this.finish();
			break;
		case R.id.chose_male:
			male.setSelected(true);
			female.setSelected(false);
			sex = 0;
			break;
		case R.id.chose_female:
			male.setSelected(false);
			female.setSelected(true);
			sex = 1;
			break;
		// case R.id.tv_infodata_ok:
		//
		// break;
		// case R.id.tv_infodata_ok:
		//
		// break;
		}
	}

}
