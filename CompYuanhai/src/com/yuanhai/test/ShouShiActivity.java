package com.yuanhai.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 手势密码Activity（初次运行跳转调用录入手势密码，其他便会调用输入手势密码进行匹配）
 * 密码正确：跳转MainActivity
 */
public class ShouShiActivity extends Activity {
	private LocusPassWordView lpwv;//自定义的
	private Toast toast;

	private void showToast(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		} else {
			toast.setText(message);
		}

		toast.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.shoushi);
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
		lpwv.setOnCompleteListener(new com.yuanhai.test.LocusPassWordView.OnCompleteListener() {//获取密码
			@Override
			public void onComplete(String mPassword) {
				// 如果密码正确,则进入主页面。
				if (lpwv.verifyPassword(mPassword)) {
					showToast(getResources().getString(R.string.setpassword_right));
					Intent intent = new Intent(ShouShiActivity.this,
							MainActivity.class);
					// 打开新的Activity
					startActivity(intent);
					finish();
				} else {
					showToast(getResources().getString(R.string.setpassword_error));
					lpwv.clearPassword();
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		View noSetPassword = (View) this.findViewById(R.id.tvNoSetPassword);
		TextView toastTv = (TextView) findViewById(R.id.login_toast);
		if (lpwv.isPasswordEmpty()) {
			lpwv.setVisibility(View.GONE);
			noSetPassword.setVisibility(View.VISIBLE);
			toastTv.setText(getResources().getString(R.string.setpassword_text));
			noSetPassword.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(); 
		            intent.setClass(ShouShiActivity.this,SetPasswordActivity.class);
		            ShouShiActivity.this.startActivity(intent);
					finish();
				}
			});
		}
	}

}
