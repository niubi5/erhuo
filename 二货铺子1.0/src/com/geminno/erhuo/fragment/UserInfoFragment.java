package com.geminno.erhuo.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.geminno.erhuo.EditUserInfoActivity;
import com.geminno.erhuo.LoginActivity;
import com.geminno.erhuo.MyApplication;
import com.geminno.erhuo.MyGoodsActivity;
import com.geminno.erhuo.PostageActivity;
import com.geminno.erhuo.R;
import com.geminno.erhuo.SheZhiActivity;
import com.geminno.erhuo.ShipAddressActivity;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.ImageLoader;

@SuppressLint("CutPasteId")
public class UserInfoFragment extends BaseFragment implements OnClickListener {

	private LinearLayout userInfo;
	private ImageView ivHead;
	private LinearLayout linearshezhi;
	private LinearLayout linearyoufei;
	private LinearLayout address;
	private LinearLayout share;
	private Users users;
	private TextView loginState;
	private LinearLayout linearfenxiang;

	private Button btnSelling;
	private Button btnSold;
	private Button btnBought;
	private Button btnDonate;
	private Button btnCollec;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_userinfo_page, null);

		return view;
	}

	@Override
	protected void initData() {
		initEvent();
	}

	@Override
	protected void initEvent() {
		userInfo.setOnClickListener(this);
		ivHead.setOnClickListener(this);
		linearshezhi.setOnClickListener(this);
		linearyoufei.setOnClickListener(this);
		address.setOnClickListener(this);
		linearfenxiang.setOnClickListener(this);

		btnSelling.setOnClickListener(this);
		btnSold.setOnClickListener(this);
		btnBought.setOnClickListener(this);
		btnDonate.setOnClickListener(this);
		btnCollec.setOnClickListener(this);
	}

	@Override
	protected void initView() {
		userInfo = (LinearLayout) getView().findViewById(
				R.id.userinfo_container);
		ivHead = (ImageView) getView().findViewById(R.id.userinfo_iv_header);
		linearshezhi = (LinearLayout) getView().findViewById(
				R.id.setting_container);
		linearyoufei = (LinearLayout) getView().findViewById(
				R.id.postage_container);
		address = (LinearLayout) getView().findViewById(R.id.address_container);
		linearfenxiang = (LinearLayout) getView().findViewById(
				R.id.share_container);
		share = (LinearLayout) getView().findViewById(R.id.share_container);
		loginState = (TextView) getView().findViewById(
				R.id.userinfo_login_state);
		btnSelling = (Button) getView().findViewById(R.id.userinfo_btn_selling);
		btnSold = (Button) getView().findViewById(R.id.userinfo_btn_sold);
		btnBought = (Button) getView().findViewById(R.id.userinfo_btn_bought);
		btnDonate = (Button) getView().findViewById(R.id.userinfo_btn_donate);
		btnCollec = (Button) getView().findViewById(R.id.userinfo_btn_favorite);
		initData();
		users = MyApplication.getCurrentUser();
		if (users != null && users.getName() != null) {
			loginState.setText(users.getName());
			// 设置头像
			if (users.getPhoto() != null &&!users.getPhoto().equals("")) {
				com.nostra13.universalimageloader.core.ImageLoader
						.getInstance().displayImage(users.getPhoto(), ivHead);
			} else {
				ivHead.setImageResource(R.drawable.header_default);
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getActivity(), MyGoodsActivity.class);
		int btnId = -1;
		switch (v.getId()) {
		case R.id.userinfo_container:
			// 跳转到EditUserInfoActivity
			Log.i("onClick", "userinfo_container");
			if (users != null) {
				startActivity(new Intent(getActivity(),
						EditUserInfoActivity.class));
			} else {
				Toast.makeText(getActivity(), "请登录！", Toast.LENGTH_SHORT)
						.show();
			}
			break;

		case R.id.share_container:
			showShare();
			break;
		case R.id.userinfo_iv_header:
			Log.i("onClick", "userinfo_btn_herder");
			// Users users=MyApplication.getCurrentUser();
			if (users != null && !users.equals("null")) {
				startActivity(new Intent(getActivity(),
						EditUserInfoActivity.class));
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}

			break;
		case R.id.setting_container:
			Log.i("onClick", "setting_container");
			startActivity(new Intent(getActivity(), SheZhiActivity.class));
			break;
		case R.id.postage_container:
			Log.i("onClick", "postage_container");

			startActivity(new Intent(getActivity(), PostageActivity.class));
			break;
		case R.id.address_container:
			if (users != null) {
				startActivity(new Intent(getActivity(),
						ShipAddressActivity.class));
			} else {
				Toast.makeText(getActivity(), "请登录！", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.userinfo_btn_selling:
			if (users != null) {
				btnId = 0;
				intent.putExtra("btnId", btnId);
				startActivity(intent);
			} else {
				Toast.makeText(getActivity(), "请登录！", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.userinfo_btn_sold:
			if (users != null) {
				btnId = 1;
				intent.putExtra("btnId", btnId);
				startActivity(intent);
			} else {
				Toast.makeText(getActivity(), "请登录！", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.userinfo_btn_bought:
			if (users != null) {
				btnId = 2;
				intent.putExtra("btnId", btnId);
				startActivity(intent);
			} else {
				Toast.makeText(getActivity(), "请登录！", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.userinfo_btn_donate:
			if (users != null) {
				btnId = 3;
				intent.putExtra("btnId", btnId);
				startActivity(intent);
			} else {
				Toast.makeText(getActivity(), "请登录！", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.userinfo_btn_favorite:
			if (users != null) {
				btnId = 4;
				intent.putExtra("btnId", btnId);
				startActivity(intent);
			} else {
				Toast.makeText(getActivity(), "请登录！", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
	}

	private void showShare() {
		ShareSDK.initSDK(getActivity());
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
		oks.show(getActivity());
	}
}
