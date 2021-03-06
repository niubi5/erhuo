package com.geminno.erhuo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.geminno.erhuo.adapter.FolderAdapter;
import com.geminno.erhuo.utils.Bimp;
import com.geminno.erhuo.utils.PublicWay;
import com.geminno.erhuo.utils.Res;

/**
 * 这个类主要是用来进行显示包含图片的文件夹
 * 
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:48:06
 */
public class ImageFile extends Activity {

	private FolderAdapter folderAdapter;
	private Button bt_cancel;
	private Context mContext;
	private String activity;
	private Intent intent;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(Res.getLayoutID("plugin_camera_image_file"));
		PublicWay.activityList.add(this);
		mContext = this;
		intent = getIntent();
		activity = intent.getStringExtra("activity");
		bt_cancel = (Button) findViewById(Res.getWidgetID("cancel"));
		bt_cancel.setOnClickListener(new CancelListener());
		GridView gridView = (GridView) findViewById(Res
				.getWidgetID("fileGridView"));
		TextView textView = (TextView) findViewById(Res
				.getWidgetID("headerTitle"));
		textView.setText(Res.getString("photo"));
		folderAdapter = new FolderAdapter(this,activity);
		gridView.setAdapter(folderAdapter);
	}

	private class CancelListener implements OnClickListener {// 取消按钮的监听
		public void onClick(View v) {
			// 清空选择的图片
			//Bimp.tempSelectBitmap.clear();
			Intent intent = new Intent();
			if(activity.equals("publishGoodsActivity")){
				Log.i("chengxingen", "one"+"," + activity);
				intent.setClass(mContext, PublishGoodsActivity.class);
			}else if(activity.equals("donateRequestActivity")){
				Log.i("chengxingen", "two" + "," + activity);
				intent.setClass(mContext, DonateRequestActivity.class); 	
			}
			/**@heikki 16.08
			 * */
//			intent.setClass(mContext, AlbumActivity.class);
			ImageFile.this.finish();
			startActivity(intent);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			if(activity.equals("publishGoodsActivity")){
				Log.i("chengxingen", "one"+"," + activity);
				intent.setClass(mContext, PublishGoodsActivity.class);
			}else if(activity.equals("donateRequestActivity")){
				Log.i("chengxingen", "two" + "," + activity);
				intent.setClass(mContext, DonateRequestActivity.class); 	
			}
//			intent.setClass(mContext, AlbumActivity.class);
////			ImageFile.this.finish();
			startActivity(intent);
		}

		return true;
	}

}
