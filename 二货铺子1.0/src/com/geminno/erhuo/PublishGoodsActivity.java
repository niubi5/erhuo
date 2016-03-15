package com.geminno.erhuo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geminno.erhuo.utils.MyAdapter;
import com.geminno.erhuo.utils.ViewHolder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Spinner;

public class PublishGoodsActivity extends Activity implements OnClickListener {
	private ImageView ivback;
	private Spinner typeSpinner;
	// private List<String> typeList;
	List<Map<String, Object>> typeList;
	// 选择图片相关
	public static final int SELECT_PIC = 11;
	public static final int TAKE_PHOTO = 12;
	public static final int CROP_PHOTO = 13;
	private ImageView ivSelect;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_publish_goods);
		// 调用MainActivity的setColor()方法，实现沉浸式状态栏
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		
		typeSpinner = (Spinner) findViewById(R.id.spn_types);
		ivback = (ImageView) findViewById(R.id.iv_pub_return);
		ivSelect = (ImageView) findViewById(R.id.iv_img_select);
		ivSelect.setOnClickListener(this);
		ivback.setOnClickListener(this);
		typeList = getspinner3data();
		//通用适配器
		MyAdapter<Map<String, Object>> myadapter = new MyAdapter<Map<String, Object>>(
				this, typeList, R.layout.spinner_item) {
			@Override
			public void convert(ViewHolder holder, Map<String, Object> t) {
				holder.setImageResource(R.id.iv_type_inco, (int) t.get("inco"));
				holder.setText(R.id.tv_type_name, (String) t.get("name"));
			}
		};
		typeSpinner.setAdapter(myadapter);
	}

	// 分类下拉列表数据
	public static List<Map<String, Object>> getspinner3data() {
		List<Map<String, Object>> spinnerdata = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("inco", R.drawable.buyingbook);
		map.put("name", "书籍文体");
		spinnerdata.add(map);

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("inco", R.drawable.buyingclothes);
		map2.put("name", "服装鞋靴");
		spinnerdata.add(map2);

		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("inco", R.drawable.buyingphone);
		map3.put("name", "手机电脑");
		spinnerdata.add(map3);

		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("inco", R.drawable.buyingother);
		map4.put("name", "其他二手");
		spinnerdata.add(map4);

		return spinnerdata;

	}
	//界面点击响应事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_pub_return:
			this.finish();
			break;
		case R.id.iv_img_select:
			break;

		default:
			break;
		}

	}
}
