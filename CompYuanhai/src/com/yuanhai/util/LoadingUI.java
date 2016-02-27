package com.yuanhai.util;

import com.yuanhai.test.R;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingUI extends LinearLayout {

	TextView noticeTV;
	private String noticeStr = "";
	
	public LoadingUI(Context context) {
		super(context);
		
	}
	/**
	 * 这是一个提示效果的View, notice说明你要显示的内容
	 * @param context
	 * @param notice
	 */
	public LoadingUI(Context context, String notice) {
		super(context);
		this.setOrientation(VERTICAL);
		this.setGravity(Gravity.CENTER);
		this.setBackgroundResource(R.drawable.loading);
		noticeStr = notice;
		ProgressBar pb = new ProgressBar(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		pb.setLayoutParams(lp);
		pb.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progerssbar_bg));
		this.setLayoutParams(lp);
		this.addView(pb);
		
		noticeTV = new TextView(context);
		noticeTV.setText(noticeStr);
		noticeTV.setTextColor(Color.WHITE);
		noticeTV.setTextSize(14);
		noticeTV.setLayoutParams(lp);
		this.addView(noticeTV);
	}
	
	/**
     * 处理触碰..
     */
	@Override
	public boolean onTouchEvent(MotionEvent event){
		Log.v("onTouchEvent", "onTouchEvent");
		return true;
	}
	

	public void setTextColor(int color){
		noticeTV.setTextColor(color);
	}
}
