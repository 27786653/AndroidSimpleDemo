package com.yuanhai.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserInfo {
	
	static private String getSharedPreferencesValue(Context context, String Name, String key, String defaultValue)
	{
		SharedPreferences shareInfo ;
		shareInfo = context.getSharedPreferences(Name, Context.MODE_PRIVATE);
		String value = shareInfo.getString(key, defaultValue);
		return value;
	}
	
	static private void saveSharedPreferencesValue(Context context, String Name, String key, String value)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(Name, Context.MODE_PRIVATE);

		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		
		editor.commit();
	}
	
	static public String getAdCache(Context context)
	{
		return getSharedPreferencesValue(context, "Cache", "AD_XML", "");
	}
	
	static public void saveAdCache(Context context, String xml)
	{
		saveSharedPreferencesValue(context, "Cache", "AD_XML", xml);
	}
	
	static public String getLiveVideoCache(Context context)
	{
		return getSharedPreferencesValue(context, "Cache", "LiveVideo_XML", "");
	}
	
	static public void saveLiveVideoCache(Context context, String xml)
	{
		saveSharedPreferencesValue(context, "Cache", "LiveVideo_XML", xml);
	}
	
	static public String getNoticeCache(Context context)
	{
		return getSharedPreferencesValue(context, "Cache", "NoticeXML", "");
	}
	
	static public void saveNoticeCache(Context context, String xml)
	{
		saveSharedPreferencesValue(context, "Cache", "NoticeXML", xml);
	}
	
	static public String getLoginName(Context context)
	{
		return getSharedPreferencesValue(context, "UserInfo", "loginName", "");
	}
	
	static public String getPwd(Context context)
	{
		return getSharedPreferencesValue(context, "UserInfo", "pwd", "");
	}
	
	static public String getUserId(Context context)
	{
		return getSharedPreferencesValue(context, "UserInfo", "userId", "");
	}
	
	static public String getOrgId(Context context)
	{
		return getSharedPreferencesValue(context, "UserInfo", "orgId", "");
	}
	
	static public void saveInfo(Context context, String loginName, String pwd, String userId, String orgId)
	{
		saveSharedPreferencesValue(context, "UserInfo", "loginName", loginName);
		saveSharedPreferencesValue(context, "UserInfo", "pwd", pwd);
		saveSharedPreferencesValue(context, "UserInfo", "userId", userId);
		saveSharedPreferencesValue(context, "UserInfo", "orgId", orgId);
	}
	
	static public String getSelectedVideo(Context context){
		return getSharedPreferencesValue(context, "LiveVideo", "Selected", "");
	}
	
	static public void saveSelectedVideo(Context context, String selectedVideo)
	{
		saveSharedPreferencesValue(context, "LiveVideo", "Selected", selectedVideo);
	}
	
	static public final String LOGIN_TYPE_PWD = "0";
	static public final String LOGIN_TYPE_GESTURE = "1";
	
	static public void setLoginType(Context context, String Type)
	{
		saveSharedPreferencesValue(context, "UserInfo", "LoginType", Type);
	}
	
	static public String getLoginType(Context context)
	{
		return getSharedPreferencesValue(context, "UserInfo", "LoginType", LOGIN_TYPE_GESTURE);
	}
	
	static public void setFirstRun(Context context, boolean isFirst)
	{
		if(isFirst)
			saveSharedPreferencesValue(context, "UserInfo", "FirstRun", "1");
		else
			saveSharedPreferencesValue(context, "UserInfo", "FirstRun", "0");
	}
	
	static public boolean isFirstRun(Context context)
	{
		String isFirst = getSharedPreferencesValue(context, "UserInfo", "FirstRun", "1");
		return isFirst.equals("1");
	}
	
	static public String getWorkListCache(Context context)
	{
		return getSharedPreferencesValue(context, "Cache", "WorkList_XML", "");
	}
	
	static public void saveWorkListCache(Context context, String xml)
	{
		saveSharedPreferencesValue(context, "Cache", "WorkList_XML", xml);
	}
	
	static public String getMeListCache(Context context)
	{
		return getSharedPreferencesValue(context, "Cache", "MeList_XML", "");
	}
	
	static public void saveMeListCache(Context context, String xml)
	{
		saveSharedPreferencesValue(context, "Cache", "MeList_XML", xml);
	}
}

