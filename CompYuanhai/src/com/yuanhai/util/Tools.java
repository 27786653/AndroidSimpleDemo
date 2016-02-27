package com.yuanhai.util;

import com.yuanhai.test.R;

import android.app.Activity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;

public class Tools {
	private static LoadingUI loadingUI;
	private static LoadingUI loadingUIRelative;
	 /**
   	 * 增加loading
   	 */
   	public static void addLoading(Activity activity) {
   		FrameLayout rootView = (FrameLayout) activity.getWindow().getDecorView();
   		if(loadingUI == null){
   			loadingUI = new LoadingUI(activity, activity.getResources().getString(R.string.loading_string));
   			FrameLayout.LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
   	   		loadingUI.setLayoutParams(params);
   		}
   		ViewGroup parent = (ViewGroup)loadingUI.getParent();
   		if(parent != null)
   			parent.removeView(loadingUI);
  		rootView.addView(loadingUI);
   	}
   	
   	/**
   	 * 增加loading
   	 */
   	public static void addLoading(Activity activity, RelativeLayout layout) {
   		if(loadingUIRelative == null){
   			loadingUIRelative = new LoadingUI(activity, activity.getResources().getString(R.string.loading_string));
   			RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
   			params.addRule(RelativeLayout.CENTER_IN_PARENT);
   			loadingUIRelative.setLayoutParams(params);
   		}
   		ViewGroup parent = (ViewGroup)loadingUIRelative.getParent();
   		if(parent != null)
   			parent.removeView(loadingUIRelative);
   		layout.addView(loadingUIRelative);
   	}
   	
   	/**
   	 * 删除loading
   	 */
   	public static void removeLoading(RelativeLayout layout) {
   		if(loadingUIRelative == null)
   			return;
   		layout.removeView(loadingUIRelative);
   	}
   	
   	/**
   	 * 删除loading
   	 */
   	public static void removeLoading(Activity activity) {
   		if(loadingUI == null)
   			return;
   		FrameLayout rootView = (FrameLayout) activity.getWindow().getDecorView();
   		rootView.removeView(loadingUI);
   	}
}
