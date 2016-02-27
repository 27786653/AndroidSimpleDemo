package com.yuanhai.test.More_Fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.yuanhai.test.R;
import com.yuanhai.test.UserInfo;

/**
 * 选项点击后跳转至此（webView展示页面）
 */
public class MoreIteamActivity extends 	Activity implements OnClickListener{
	private WebView webview;
	private String urlRoot;
	private String phoneNumber = "";
	private String title;
	private static final String TAG = MoreIteamActivity.class.getSimpleName();
	private static final String APP_CACAHE_DIRNAME = "/webcache";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_webview);
        
		TextView tv = (TextView)findViewById(R.id.more_item_name);
		title = getIntent().getStringExtra("name");
		tv.setText(title);
		
        findViewById(R.id.more_top_back).setOnClickListener(this);
        findViewById(R.id.more_call).setOnClickListener(this);
        findViewById(R.id.more_call).setVisibility(View.INVISIBLE);
        
        webview = (WebView) findViewById(R.id.more_layout_webview);
        webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setUseWideViewPort(true); 
		webview.getSettings().setLoadWithOverviewMode(true);

		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.getSettings().setBuiltInZoomControls(true);//support zoom
		urlRoot = getIntent().getStringExtra("url");
		phoneNumber = getPhoneNumberFromUrl(urlRoot);
		String phoneNum= UserInfo.getLoginName(MoreIteamActivity.this);
		if(urlRoot.contains("WY001"))
		{
			urlRoot="http://pws.yuhi.com.cn/WyNotice/wyNoticeInfo";
		}
		else if(urlRoot.contains("WY002")) {
			urlRoot = "http://pws.yuhi.com.cn/WyNotice/FeiYongInfo?phoneNum="+phoneNum;
		}
		webview.loadUrl(urlRoot);
		webview.setWebViewClient(new MoreWebViewClient());
		WebSettings set = webview.getSettings();
		set.setUseWideViewPort(true);
		set.setLoadWithOverviewMode(true);
	}
	private void goBack()
	{
		String url = webview.getUrl();
		if(url == null)
			finish();
		final String rootFlag = "root=yuanhai";
		if(urlRoot.equalsIgnoreCase(url) || url.indexOf(rootFlag) != -1)
			finish();
		else
			webview.goBack();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.more_top_back:
			//goBack();
			finish();
			break;
		case R.id.more_call:
			
			Intent phoneIntent = new Intent(
					 "android.intent.action.CALL",
					 Uri.parse("tel:" + phoneNumber)); 
			startActivity(phoneIntent);
			break;
		}
	}
	
	private String getPhoneNumberFromUrl(String url)
	{
		final String telFlag = "&tel=";
		int index = url.indexOf(telFlag);
		if(index != -1)
		{
			int endIndex = url.indexOf("&", index+telFlag.length());
			if(endIndex == -1)
			{
				return url.substring(index+telFlag.length());
			}
			else
			{
				return url.substring(index+telFlag.length(), endIndex);
			}
		}
		else
			return "";
	}
	
	private class MoreWebViewClient extends WebViewClient {  
	   	 @Override 
	        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
	   		 	view.loadUrl(url);  
	            return true;  
			}
	   	 
	   	 @Override
	        public void onPageFinished(WebView view, String url) {
	   		 	phoneNumber = getPhoneNumberFromUrl(url);
	   		 	if(phoneNumber.length() != 0)
	   		 		findViewById(R.id.more_call).setVisibility(View.VISIBLE);
	   		 	else
	   		 		findViewById(R.id.more_call).setVisibility(View.INVISIBLE);
	            super.onPageFinished(view, url);
	        }
		}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK) { 
	    	goBack();
	        return true;
	    } 
	    return super.onKeyDown(keyCode, event);
	}

//	private void initWebView( WebView mWebView) {
//
//		mWebView.getSettings().setJavaScriptEnabled(true);
//		mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//		mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
//		// 开启 DOM storage API 功能
//		mWebView.getSettings().setDomStorageEnabled(true);
//		//开启 database storage API 功能
//		mWebView.getSettings().setDatabaseEnabled(true);
//		String cacheDirPath = getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
////      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
//		Log.i(TAG, "cacheDirPath=" + cacheDirPath);
//		//设置数据库缓存路径
//		mWebView.getSettings().setDatabasePath(cacheDirPath);
//		//设置  Application Caches 缓存目录
//		mWebView.getSettings().setAppCachePath(cacheDirPath);
//		//开启 Application Caches 功能
//		mWebView.getSettings().setAppCacheEnabled(true);
//	}


}
