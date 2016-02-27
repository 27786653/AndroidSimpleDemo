package com.yuanhai.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.yhzl.utility.IGlobalSetting;
import com.yhzl.utility.IPushMessageListener;
import com.yhzl.utility.UtilityFactory;
import com.yuanhai.appShows.MainApplication;
import com.yuanhai.test.Info_Fragment.NoticeActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
/**
 * 首先加载viewPage的FlashActivity
 */
public class FlashActivity extends Activity implements OnViewChangeListener{
	
	private MyScrollLayout mScrollLayout;
	private ImageView[] imgs;
	private int count;
	private int currentItem;
	private RelativeLayout mainRLayout;
	private LinearLayout pointLLayout;
	private LinearLayout leftLayout;
	private LinearLayout rightLayout;
	private LinearLayout animLayout;
	private boolean canSkip = true;
	PackageInfo packageInfo;
	
	private static boolean onNotificationClicked = false;
	private static boolean firstCreate = true;
	private static NoticeActivity noticeActivity = null;
	private static boolean showMainActivity = false;
	
	static public void setNoticeActivity(NoticeActivity activity)
	{
		noticeActivity = activity;
	}	
	static public void setNotificationClicked(boolean clicked)
	{
		onNotificationClicked = clicked;
	}
	static public void onShowInfoActivity(MainActivity activity)
	{
		showMainActivity = true;
		//application run by notification clicked
		if(onNotificationClicked)
		{
			onNotificationClicked = false;
			Intent intent = new Intent(activity, NoticeActivity.class);
			activity.startActivity(intent);
		}
	}
	private boolean ProcessNotification()
	{
		if(!firstCreate && onNotificationClicked)
    	{
    		onNotificationClicked = false;
    		finish();
    		return true;
    	} 
		firstCreate = false;
		
		PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY, "ji8VCgbTvnyGcVam5WCITLsI");
        UtilityFactory.setPushMessageListener( new IPushMessageListener(){
			@Override
			public void onNotificationClicked(Context arg0, String arg1,
					String arg2, String arg3) {
				if(!firstCreate && showMainActivity ){
					if(noticeActivity != null)
					{
						noticeActivity.onNotificationClicked();
					}
					else
					{
						Intent intent = new Intent(FlashActivity.this, NoticeActivity.class);
						startActivity(intent);
					}
				}
				onNotificationClicked = true;
			}
		});
        return false;
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	if(ProcessNotification())
    		return;  
   	    setContentView(R.layout.flash);
        initView();
        
		if(mScrollLayout.getChildCount() == 1){
			TimeThread timeThread = new TimeThread();
			timeThread.start();
		}
    }
    
    private void addScrollSubLayouts()
    {
    	LayoutInflater layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
    	
    	IGlobalSetting setting = ((MainApplication)getApplication()).getGlobalSetting();
    	int count = setting.getLoadImageCount();
    	for(int i=0; i<count; i++)
    	{
    		int rid;
    		if(i == count-1)
    			rid = R.layout.flash_last;
    		else
    			rid = R.layout.flash_normal;
    		
    		RelativeLayout layout = (RelativeLayout)layoutInflater.inflate(rid, null);
			try {
				//初始化动画图片路径
					String imagePath = setting.getLoadImagePath(i);
					FileInputStream fis = new FileInputStream(imagePath);
					Bitmap bitmap = BitmapFactory.decodeStream(fis);
					BitmapDrawable background = new BitmapDrawable(getResources(), bitmap);
					layout.setBackgroundDrawable(background);
				}
			catch (FileNotFoundException e) {
		        	e.printStackTrace();
		        }
			mScrollLayout.addView(layout);
    	}
    }
	private void initView() {
		mScrollLayout  = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		addScrollSubLayouts();
		pointLLayout = (LinearLayout) findViewById(R.id.llayout);
		mainRLayout = (RelativeLayout) findViewById(R.id.mainRLayout);
		findViewById(R.id.startBtn).setOnClickListener(onClick);
		animLayout = (LinearLayout) findViewById(R.id.animLayout);
		leftLayout  = (LinearLayout) findViewById(R.id.leftLayout);
		rightLayout  = (LinearLayout) findViewById(R.id.rightLayout);
		count = mScrollLayout.getChildCount();
		imgs = new ImageView[count];
		for(int i = 0; i< count;i++) {
			ImageView img = new ImageView(this);
			img.setBackgroundResource(R.drawable.page_indicator_bg);
			img.setPadding(5, 5, 5, 5);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); 
			param.setMargins(10, 20, 30, 40); 
			img.setLayoutParams(param); 
			param.gravity = Gravity.CENTER_VERTICAL; 
			pointLLayout.addView(img, param);
			
			imgs[i] = (ImageView) pointLLayout.getChildAt(i);
			imgs[i].setEnabled(true);
			imgs[i].setTag(i);
		}
		currentItem = 0;
		imgs[currentItem].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
	}
	
	private View.OnClickListener onClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.startBtn:
				mScrollLayout.setVisibility(View.GONE);
				pointLLayout.setVisibility(View.GONE);
//				animLayout.setVisibility(View.VISIBLE);
//				mainRLayout.setBackgroundResource(R.drawable.whatsnew_bg);
//				Animation leftOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left);
//				Animation rightOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right);
//				leftLayout.setAnimation(leftOutAnimation);
//				rightLayout.setAnimation(rightOutAnimation);
//				leftOutAnimation.setAnimationListener(new AnimationListener() {
//					@Override
//					public void onAnimationStart(Animation animation) {
//						mainRLayout.setBackgroundColor(getResources().getColor(R.color.bgColor));
//					}
//					@Override
//					public void onAnimationRepeat(Animation animation) {
//					}
//					@Override
//					public void onAnimationEnd(Animation animation) {
						skipPager();
//					}
//				});
				break;
			}
		}
	};
	
	private void skipPager(){
		Intent intent = null;
		if(canSkip){
			canSkip = false;
			leftLayout.setVisibility(View.GONE);
			rightLayout.setVisibility(View.GONE);
			IGlobalSetting setting = ((MainApplication)getApplication()).getGlobalSetting();
			if(setting.isFirstRun()){
				intent = new Intent(FlashActivity.this,LoginActivity.class);
				FlashActivity.this.finish();
				overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
			}else{
				
				intent = new Intent(FlashActivity.this,ShouShiActivity.class);
				FlashActivity.this.finish();
			}
			FlashActivity.this.startActivity(intent);
		}
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(currentItem == mScrollLayout.getChildCount()-1){
				skipPager();
			}
			super.handleMessage(msg);
		}
		
	};
	//设置flash的动画切换侦听
	@Override
	public void OnViewChange(int position) {
		setcurrentPoint(position);
		if(position == mScrollLayout.getChildCount()-1){//假如是在ViewPage的最后一页
			TimeThread timeThread = new TimeThread();
			timeThread.start();
		}
	}
	
	private class TimeThread extends Thread {

		@Override
		public void run() {
			try {
				sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			handler.sendEmptyMessage(1);     //发送空消息
			super.run();
		}
		
	}
	
	private void setcurrentPoint(int position) {
		if(position < 0 || position > count -1 || currentItem == position) {
			return;
		}
		imgs[currentItem].setEnabled(true);
		imgs[position].setEnabled(false);
		currentItem = position;
	}
}