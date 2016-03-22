package com.geminno.erhuo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ClassificationActivity extends Activity {

	 private Spinner classificationSpinner = null;  //分类
	 private Spinner sortSpinner = null;     //排序
	 private Spinner screenSpinner = null;    //筛选
	 String iphone=null;
	 ArrayAdapter<String> classificationAdapter = null;  //分类适配器
	 ArrayAdapter<String> sortAdapter = null;    //排序适配器
	 ArrayAdapter<String> screenAdapter = null;    //筛选适配器
	 String[] classif=null;
     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_classification);
		//调用setColor()方法,实现沉浸式状态栏
	  	MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		
        Intent intent=getIntent();
        iphone=intent.getStringExtra("iphone");
        Log.i("result", "iphone:"+iphone);
        setSpinner();
        TextView tView=(TextView) findViewById(R.id.tv_fenlei);
        tView.setText(iphone);
        
	}
	
	   private void setSpinner(){
		   
		   if(iphone.equals("苹果手机")){
		       classif=new String[]{iphone,"全部分类","平板电脑","笔记本","小米","数码3c","卡劵","美容美体","箱包","其他",};
		   }else if (iphone.equals("平板电脑")) {
			   classif=new String[]{iphone,"全部分类","苹果手机","笔记本","小米","数码3c","卡劵","美容美体","箱包","其他",};
		   }else if (iphone.equals("笔记本") ){
			   classif=new String[]{iphone,"全部分类","苹果手机","平板电脑","小米","数码3c","卡劵","美容美体","箱包","其他",};
		   }else if (iphone.equals("小米") ){
			   classif=new String[]{iphone,"全部分类","苹果手机","平板电脑","笔记本","数码3c","卡劵","美容美体","箱包","其他",};
		   }else if (iphone.equals("数码3c") ){
			   classif=new String[]{iphone,"全部分类","苹果手机","平板电脑","笔记本","小米","卡劵","美容美体","箱包","其他",};
		   }else if (iphone.equals("卡券") ){
			   classif=new String[]{iphone,"全部分类","苹果手机","平板电脑","笔记本","小米","数码3c","美容美体","箱包","其他",};
		   }else if (iphone.equals("箱包") ){
			   classif=new String[]{iphone,"全部分类","苹果手机","平板电脑","笔记本","小米","数码3c","美容美体","卡劵","其他",};
		   }else if (iphone.equals("美容美体") ){
			   classif=new String[]{iphone,"全部分类","苹果手机","平板电脑","笔记本","小米","数码3c","卡劵","箱包","其他",};
		   }
		   
		   String[] sort=new String[]{"排序","默认排序","最新发布","离我最近","价格最低","价格最高"};
		   String[] screen=new String[]{"选择价格","0~100元","100~200元","200~300元","300~400元","400~500元","500以上"};
		   classificationSpinner=(Spinner) findViewById(R.id.spinner1);
		   sortSpinner=(Spinner) findViewById(R.id.spinner2);
		   screenSpinner=(Spinner) findViewById(R.id.spinner3);
		   
		   //绑定适配器
		   classificationAdapter=new ArrayAdapter<String>(this,
				   R.layout.spinner_search, classif);
		   classificationSpinner.setAdapter(classificationAdapter);
		   classificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		   //默认第一个
		   classificationSpinner.setSelection(0,true);
		   
		 //绑定适配器
		   sortAdapter=new ArrayAdapter<String>(this,
				   R.layout.spinner_search, sort);
		   sortSpinner.setAdapter(sortAdapter);
		   sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		   
		   //默认第一个
		   sortSpinner.setSelection(0,true);
		   
		 //绑定适配器
		   screenAdapter=new ArrayAdapter<String>(this,
				   R.layout.spinner_search, screen);
		   screenSpinner.setAdapter(screenAdapter);
		   screenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		   //默认第一个
		   screenSpinner.setSelection(0,true);
		   
	   }
}
