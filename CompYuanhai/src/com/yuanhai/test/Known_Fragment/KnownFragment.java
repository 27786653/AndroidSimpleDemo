package com.yuanhai.test.Known_Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.yuanhai.appShows.MainApplication;
import com.yuanhai.test.FlashActivity;
import com.yuanhai.test.R;
import com.yuanhai.util.Tools;

@SuppressLint("SetJavaScriptEnabled")
public class KnownFragment extends Fragment implements OnClickListener{
	private WebView webview;
	private View rootView;
	private String URL;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView= inflater.inflate(R.layout.known_webview, container,false);
		//导航栏知识库
		webview=(WebView)rootView.findViewById(R.id.known_layout_webview);
		
		webview.setWebViewClient(new HelloWebViewClient ()); 
		MainApplication app = (MainApplication)getActivity().getApplication();
		URL = app.getGlobalSetting().getKnowledgeUrl();
		webview.loadUrl(URL);
		
		rootView.findViewById(R.id.known_top_back).setOnClickListener(this);
		rootView.findViewById(R.id.known_top_info).setOnClickListener(this);
		  
		//开启js支持
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setUseWideViewPort(true); 
		webview.getSettings().setLoadWithOverviewMode(true); 
		HelloWebViewClient webviewclient = new HelloWebViewClient();
		webview.setWebViewClient(webviewclient);
		return rootView;
	}
	
	private void showActionBtn(int visibility)
	{
		rootView.findViewById(R.id.known_top_back).setVisibility(visibility);
		rootView.findViewById(R.id.known_top_info).setVisibility(visibility);
	}
	
	 //注册web客户端  
    private class HelloWebViewClient extends WebViewClient {  
		 @Override 
		 public boolean shouldOverrideUrlLoading(WebView view, String url) { 
			Tools.addLoading(getActivity());
			webview.loadUrl(url);
			return true;
		}
    	public void onPageFinished(WebView view, String url){
    		if(URL.equals(url)){
				showActionBtn(View.GONE);
			}else{
				showActionBtn(View.VISIBLE);
			}
    		Tools.removeLoading(getActivity());
    	}
    	@Override
        public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
   		 Tools.removeLoading(getActivity());
   	 	}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.known_top_back:
			webview.goBack();
			break;
		case R.id.known_top_info:
			webview.loadUrl(URL);
			break;
		default:
			break;
		}
	}
	public boolean onBackKeyDown()
	{
		if(URL.equals(webview.getUrl())){
			return false;
		}
		else
		{
			webview.goBack();
			return true;
		}
	}
}