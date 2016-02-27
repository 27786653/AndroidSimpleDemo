package com.yuanhai.test.Info_Fragment;

import com.yuanhai.test.R;
import com.yuanhai.appShows.MainApplication;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class NoticeIteamActivity extends Activity implements OnClickListener,OnTouchListener{
private WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.notice_iteam_intent);
		findViewById(R.id.notic_top_back).setOnClickListener(this);
		webview = (WebView) findViewById(R.id.wv_webview);
		
		MainApplication app = (MainApplication)getApplicationContext();
		app.setNoticeIteamActivity(this);
		
		webview.loadUrl(getIntent().getStringExtra("html_url"));
		webview.loadUrl(getIntent().getStringExtra("phonehtmlpath"));
		webview.setWebViewClient(new HelloWebViewClient());
		WebSettings set = webview.getSettings();
		set.setUseWideViewPort(true);
		set.setLoadWithOverviewMode(true);
		set.setJavaScriptEnabled(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.notic_top_back:
			MainApplication app = (MainApplication)getApplicationContext();
			app.setNoticeIteamActivity(null);
			finish();
		}
	}
	private class HelloWebViewClient extends WebViewClient {  
   	 @Override 
        public boolean shouldOverrideUrlLoading(WebView view, String url) {  
            view.loadUrl(url);  
            return true;  
		}
	}

	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.notic_top_back:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// 重新设置按下时的背景图片
				((ImageView) v).setImageDrawable(getResources().getDrawable(
						R.drawable.notice_back2));

			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				// 再修改为抬起时的正常图片
				((ImageView) v).setImageDrawable(getResources().getDrawable

				(R.drawable.notice_back));
			}

		}
		return false;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode==KeyEvent.KEYCODE_BACK){
			 MainApplication app = (MainApplication)getApplicationContext();
			 app.setNoticeIteamActivity(null);
			 finish();
			 return true;
		 }
		 return super.onKeyDown(keyCode, event);
	}

}
