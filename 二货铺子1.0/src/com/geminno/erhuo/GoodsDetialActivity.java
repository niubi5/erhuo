package com.geminno.erhuo;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.geminno.erhuo.GoodsDetialActivity.AsyncImageLoader.ImageCallback;
import com.geminno.erhuo.entity.Goods;
import com.geminno.erhuo.entity.Users;



public class GoodsDetialActivity extends Activity {
	private ViewPager viewPager;
	private ArrayList<View> pageview;
	
	// 图片的地址，这里可以从服务器获取
    String[] urls = new String[]{
             
            "http://a.hiphotos.baidu.com/image/pic/item/3bf33a87e950352ad6465dad5143fbf2b2118b6b.jpg",
            "http://a.hiphotos.baidu.com/image/pic/item/c8177f3e6709c93d002077529d3df8dcd0005440.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/7aec54e736d12f2ecc3d90f84dc2d56285356869.jpg",
            "http://e.hiphotos.baidu.com/image/pic/item/9c16fdfaaf51f3de308a87fc96eef01f3a297969.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/f31fbe096b63f624b88f7e8e8544ebf81b4ca369.jpg",
            "http://h.hiphotos.baidu.com/image/pic/item/11385343fbf2b2117c2dc3c3c88065380cd78e38.jpg",
            "http://c.hiphotos.baidu.com/image/pic/item/3801213fb80e7bec5ed8456c2d2eb9389b506b38.jpg"
     
    };
    
	private ImageView image;
    private View item ;
    private MyAdapter adapter ;
    private ImageView[] indicator_imgs = new ImageView[7];//存放引到图片数组
    private LayoutInflater inflater;
    //
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置无标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_goods_detial);
		MainActivity.setColor(this, getResources().getColor(R.color.main_red));
		viewPager = (ViewPager) findViewById(R.id.vp_goods_images);
		List<View> list = new ArrayList<View>();
		inflater = LayoutInflater.from(this);
		 /**
         * 创建多个item （每一条viewPager都是一个item）
         * 从服务器获取完数据（如文章标题、url地址） 后，再设置适配器
         */
        for (int i = 0; i < 7; i++) {
            item = inflater.inflate(R.layout.goods_images_item, null);
            list.add(item);
        }
        //创建适配器， 把组装完的组件传递进去
        adapter = new MyAdapter(list);
        viewPager.setAdapter(adapter);
 
        //绑定动作监听器：如翻页的动画
        viewPager.setOnPageChangeListener(new MyListener());
         
        initIndicator();
        //获得当前商品的id
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Users user = (Users) bundle.getSerializable("user");
        Goods goods = (Goods) bundle.getSerializable("goods");
        List<String> urls = bundle.getStringArrayList("urls");
        Log.i("erhuo", user.getName());
        Log.i("erhuo", goods.getName());
        Log.i("erhuo", urls.size() + "");
	}
	//点击事件
	public void onclick(View v){
		switch (v.getId()) {
		case R.id.iv_share_report:
			showPopupWindow(v);
			break;
		case R.id.iv_detial_return:
			finish();
		case R.id.btn_buy:
			Intent intent = new Intent(this,BuyGoodsActivity.class);
			startActivity(intent);
		default:
			break;
		}
	}
	//显示popupwindow
    private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.pop_window, null);
        // 设置按钮的点击事件
        TextView tvShare = (TextView) contentView.findViewById(R.id.tv_share);
        TextView tvReport = (TextView) contentView.findViewById(R.id.tv_report);
        tvShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(GoodsDetialActivity.this, "分享",
                        Toast.LENGTH_SHORT).show();
            }
        });
        tvReport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(GoodsDetialActivity.this, "举报",
                        Toast.LENGTH_SHORT).show();
			}
		});

        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.background_grey));

        // 设置好参数之后再show
        //popupWindow.showAsDropDown(view);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

    }
	
	 /**
     * 初始化引导图标
     * 动态创建多个小圆点，然后组装到线性布局里
     */
    private void initIndicator(){
         
        ImageView imgView;
        View v = findViewById(R.id.indicator);// 线性水平布局，负责动态调整导航图标
         
        for (int i = 0; i < 7; i++) {
            imgView = new ImageView(this);
            LinearLayout.LayoutParams params_linear = new LinearLayout.LayoutParams(10,10);
            params_linear.setMargins(7, 10, 7, 10);
            imgView.setLayoutParams(params_linear);
            indicator_imgs[i] = imgView;
             
            if (i == 0) { // 初始化第一个为选中状态
                 
                indicator_imgs[i].setBackgroundResource(R.drawable.round_point);
            } else {
                indicator_imgs[i].setBackgroundResource(R.drawable.round_point_normal);
            }
            ((ViewGroup)v).addView(indicator_imgs[i]);
        }
         
    }
    /**
     * 适配器，负责装配 、销毁  数据  和  组件 。
     */
    private class MyAdapter extends PagerAdapter {
 
        private List<View> mList;
 
         
        private AsyncImageLoader asyncImageLoader;
         
        public MyAdapter(List<View> list) {
            mList = list;
            asyncImageLoader = new AsyncImageLoader();  
        }
 
         
         
        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mList.size();
        }
 
         
        /**
         * Remove a page for the given position.
         * 滑动过后就销毁 ，销毁当前页的前一个的前一个的页！
         * instantiateItem(View container, int position)
         * This method was deprecated in API level . Use instantiateItem(ViewGroup, int)
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            container.removeView(mList.get(position));
             
        }
 
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0==arg1;
        }
 
         
        /**
         * Create the page for the given position.
         */
        @SuppressLint("NewApi") @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            Drawable cachedImage = asyncImageLoader.loadDrawable(
                    urls[position], new ImageCallback() {
 
                        @SuppressLint("NewApi") public void imageLoaded(Drawable imageDrawable,
                                String imageUrl) {
 
                            View view = mList.get(position);
                            image = ((ImageView) view.findViewById(R.id.iv_goods_image));
                            image.setBackground(imageDrawable);
                            container.removeView(mList.get(position));
                            container.addView(mList.get(position));
                            // adapter.notifyDataSetChanged();
 
                        }
                    });
 
            View view = mList.get(position);
            image = ((ImageView) view.findViewById(R.id.iv_goods_image));
            image.setBackground(cachedImage);
 
            container.removeView(mList.get(position));
            container.addView(mList.get(position));
            // adapter.notifyDataSetChanged();
                 
 
            return mList.get(position);
 
        }
    }
    
    /**
     * 动作监听器，可异步加载图片
     *
     */
    private class MyListener implements OnPageChangeListener{
 
        @Override
        public void onPageScrollStateChanged(int state) {
            // TODO Auto-generated method stub
            if (state == 0) {
                //new MyAdapter(null).notifyDataSetChanged();
            }
        }
 
         
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
             
        }
 
        @Override
        public void onPageSelected(int position) {
             
            // 改变所有导航的背景图片为：未选中
            for (int i = 0; i < indicator_imgs.length; i++) {
                 
                indicator_imgs[i].setBackgroundResource(R.drawable.round_point_normal);
                 
            }
             
            // 改变当前背景图片为：选中
            indicator_imgs[position].setBackgroundResource(R.drawable.round_point);
        }
         
         
    }
     
     
 
    /**
     * 异步加载图片
     */
    static class AsyncImageLoader {
 
        // 软引用，使用内存做临时缓存 （程序退出，或内存不够则清除软引用）
        private HashMap<String, SoftReference<Drawable>> imageCache;
 
        public AsyncImageLoader() {
            imageCache = new HashMap<String, SoftReference<Drawable>>();
        }
 
        /**
         * 定义回调接口
         */
        public interface ImageCallback {
            public void imageLoaded(Drawable imageDrawable, String imageUrl);
        }
 
         
        /**
         * 创建子线程加载图片
         * 子线程加载完图片交给handler处理（子线程不能更新ui，而handler处在主线程，可以更新ui）
         * handler又交给imageCallback，imageCallback须要自己来实现，在这里可以对回调参数进行处理
         *
         * @param imageUrl ：须要加载的图片url
         * @param imageCallback：
         * @return
         */
        public Drawable loadDrawable(final String imageUrl,
                final ImageCallback imageCallback) {
             
            //如果缓存中存在图片  ，则首先使用缓存
            if (imageCache.containsKey(imageUrl)) {
                SoftReference<Drawable> softReference = imageCache.get(imageUrl);
                Drawable drawable = softReference.get();
                if (drawable != null) {
                    imageCallback.imageLoaded(drawable, imageUrl);//执行回调
                    return drawable;
                }
            }
 
            /**
             * 在主线程里执行回调，更新视图
             */
            final Handler handler = new Handler() {
                public void handleMessage(Message message) {
                    imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
                }
            };
 
             
            /**
             * 创建子线程访问网络并加载图片 ，把结果交给handler处理
             */
            new Thread() {
                @Override
                public void run() {
                    Drawable drawable = loadImageFromUrl(imageUrl);
                    // 下载完的图片放到缓存里
                    imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
                    Message message = handler.obtainMessage(0, drawable);
                    handler.sendMessage(message);
                }
            }.start();
             
            return null;
        }
 
         
        /**
         * 下载图片  （注意HttpClient 和httpUrlConnection的区别）
         */
        public Drawable loadImageFromUrl(String url) {
 
            try {
                HttpClient client = new DefaultHttpClient();
                client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000*15);
                HttpGet get = new HttpGet(url);
                HttpResponse response;
 
                response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
 
                    Drawable d = Drawable.createFromStream(entity.getContent(),
                            "src");
 
                    return d;
                } else {
                    return null;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        //清除缓存
        public void clearCache() {
 
            if (this.imageCache.size() > 0) {
 
                this.imageCache.clear();
            }
 
        }
}
	
}
