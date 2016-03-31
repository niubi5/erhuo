package com.geminno.erhuo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.geminno.erhuo.adapter.RemarkAdapter;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Remark;
import com.geminno.erhuo.entity.Users;
import com.geminno.erhuo.utils.Url;
import com.geminno.erhuo.view.PullUpToLoadListView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

public class CommentActivity extends Activity {
	
    private LinearLayout lincomment;
	private Users users;
	private List<Map<Remark, Users>> listRemarkUsers = new ArrayList<Map<Remark, Users>>();
	private RemarkAdapter remarkAdapter;
	private Context context;
	private Goods goods;
	private PullUpToLoadListView pullUpToLoadListView;
	private ScrollView scroll;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_comment);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		context = this;
		//create();
	}
	
	public void msgComment(View v){
		switch(v.getId()){
		case R.id.message_back:
			finish();
			break;
		}
	}

//	private void create() {
//		users = MyApplication.getCurrentUser();
//		
//		lincomment=(LinearLayout) findViewById(R.id.lin_comment);
//		HttpUtils http = new HttpUtils();
//		String urlHead = Url.getUrlHead();
//		String url = urlHead + "/CommentRemarkServlet";
//		RequestParams params = new RequestParams();
//		params.addQueryStringParameter("userid", users.getId() + "");
//		http.send(HttpRequest.HttpMethod.GET, url, params,
//				new RequestCallBack<String>() {
//
//					@Override
//					public void onFailure(HttpException arg0, String arg1) {
//
//					}
//
//					@Override
//					public void onSuccess(ResponseInfo<String> arg0) {
//						String result = arg0.result;
//						Gson gson = new GsonBuilder()
//								.enableComplexMapKeySerialization().create();
//						List<Map<Remark, Users>> newComments = gson.fromJson(
//								result,
//								new TypeToken<List<Map<Remark, Users>>>() {
//								}.getType());
//						if (!listRemarkUsers.isEmpty()) {
//							listRemarkUsers.clear();
//						}
//						listRemarkUsers.addAll(newComments);// 加到总集合中去
//						if (remarkAdapter == null) {
//							remarkAdapter = new RemarkAdapter(context,
//									listRemarkUsers, goods,
//									pullUpToLoadListView);
//							pullUpToLoadListView.setAdapter(remarkAdapter);
//						} else {
//							Log.i("erhuo", "通知数据源改变");
//							remarkAdapter.notifyDataSetChanged();
//						}
//						// 解决scrollview嵌套listview高度问题.
//						// 获得listview对应的adapter
//						ListAdapter listAdapter = pullUpToLoadListView
//								.getAdapter();
//						int totalHeight = 0;
//						for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
//							View listItem = listAdapter.getView(i, null,
//									pullUpToLoadListView);
//							// 计算子项的宽高
//							listItem.measure(0, 0);
//							// 统计所有子项总高度
//							totalHeight += listItem.getMeasuredHeight();
//						}
//						ViewGroup.LayoutParams params = pullUpToLoadListView
//								.getLayoutParams();
//						// 获取子项间分隔符占用的高度 + 到总高度重去
//						params.height = totalHeight
//								+ (pullUpToLoadListView.getDividerHeight() * (listAdapter
//										.getCount() - 1));
//						// params.height最后得到整个ListView完整显示需要的高度
//						pullUpToLoadListView.setLayoutParams(params);
//						scroll.smoothScrollBy(0, 20);
//					}
//				});
//
//	}
//	
	
	
}
