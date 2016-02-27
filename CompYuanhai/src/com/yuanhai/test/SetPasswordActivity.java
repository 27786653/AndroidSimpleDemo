package com.yuanhai.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.yhzl.utility.IGlobalSetting;
import com.yuanhai.test.R;
import com.yuanhai.test.LocusPassWordView.OnCompleteListener;
import com.yuanhai.util.StringUtil;

import com.yuanhai.appShows.MainApplication;

public class SetPasswordActivity extends Activity {
	private LocusPassWordView lpwv;
	private String password;
	private boolean needverify = true;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setpass);
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
		lpwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
				password = mPassword;
				if (needverify) {
					if (lpwv.verifyPassword(mPassword)) {
						showToast("密码输入正确,请记住新密码!");
						lpwv.clearPassword();
						IGlobalSetting setting = ((MainApplication)getApplication()).getGlobalSetting();
						setting.setFirstRunFlag(0);
						Intent();
						needverify = false;
					} else {
						showToast("错误的密码,请重新输入!");
						lpwv.clearPassword();
						password = "";
					}
				}
			}
		});

		OnClickListener mOnClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tvSave:
					if (StringUtil.isNotEmpty(password)) {
						lpwv.resetPassWord(password);
						lpwv.clearPassword();
						showToast("格式符合，需再输入一次");
						SetPasswordActivity.this.finish();
						Intent intent  = new Intent();
						startActivity(new Intent(SetPasswordActivity.this,
						SetPasswordActivity.class));
					} else {
						lpwv.clearPassword();
						showToast("密码不能为空,请输入密码.");
					}
					break;
				case R.id.tvReset:
					lpwv.clearPassword();
					break;
				}
			}
		};
		Button buttonSave = (Button) this.findViewById(R.id.tvSave);
		buttonSave.setOnClickListener(mOnClickListener);
		Button tvReset = (Button) this.findViewById(R.id.tvReset);
		tvReset.setOnClickListener(mOnClickListener);
		// 如果密码为空,直接输入密码
		if (lpwv.isPasswordEmpty()) {
			this.needverify = false;
			showToast("请输入密码");
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	private void Intent(){
	SetPasswordActivity.this.finish();
	Intent intent  = new Intent();
	startActivity(new Intent(SetPasswordActivity.this,
			MainActivity.class));
}
}
