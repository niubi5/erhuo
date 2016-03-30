package com.geminno.erhuo;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
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
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class WoMenActivity extends Activity {

	ImageView returnImageView;
	ImageView xuanze;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wo_men);
		returnImageView = (ImageView) findViewById(R.id.ib_women_return);
		xuanze = (ImageView) findViewById(R.id.ib_xuanze);
	}

	public void onclick(View v) {
		Intent intent = new Intent(this, SheZhiActivity.class);
		startActivity(intent);
	}

	public void click(View v) {
		AlertDialog.Builder builder = new Builder(this);
		final String[] items = new String[] { "分享", "在浏览器中打开",

		};
		builder.setSingleChoiceItems(items, -1, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				if (items[arg1].equals("分享")) {
					showShare();
				}
//				Toast.makeText(WoMenActivity.this, "你选择的是：" + items[arg1], 0)
//						.show();
				
				
				arg0.dismiss();
			}
		});
		builder.show();
	}
	private void showShare() {
		ShareSDK.initSDK(WoMenActivity.this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("二货铺子");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("nb四人组制作！必属精品！");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(WoMenActivity.this);
	}

}
