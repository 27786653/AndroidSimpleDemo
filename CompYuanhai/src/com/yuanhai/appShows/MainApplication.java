package com.yuanhai.appShows;

import android.content.Context;

import com.baidu.frontia.FrontiaApplication;
import com.yhzl.utility.Camera;
import com.yhzl.utility.IGlobalSetting;
import com.yhzl.utility.IPushMessageListener;
import com.yhzl.utility.LiveVideo;
import com.yhzl.utility.UtilityFactory;
import com.yuanhai.test.FlashActivity;
import com.yuanhai.test.Info_Fragment.NoticeIteamActivity;

public class MainApplication extends FrontiaApplication{

	private IGlobalSetting globalSetting;
	private NoticeIteamActivity noticeIteamActivity;
	private  Context newcontext;
	
	public IGlobalSetting getGlobalSetting(){
		return globalSetting;
	}
	
	public void setNoticeIteamActivity(NoticeIteamActivity activity)
	{
		noticeIteamActivity = activity;
	}
	public NoticeIteamActivity getNoticeIteamActivity()
	{
		return noticeIteamActivity;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		globalSetting = UtilityFactory.CreateGlobalSetting();
		String packageName = getPackageName();
		globalSetting.init(this, packageName);
		LiveVideo.static_init();
		Camera.static_init();

		//application run by notification clicked,
		UtilityFactory.setPushMessageListener(new IPushMessageListener() {
			@Override
			public void onNotificationClicked(Context arg0, String arg1,
											  String arg2, String arg3) {
				FlashActivity.setNotificationClicked(true);
			}
		});

	}



}
