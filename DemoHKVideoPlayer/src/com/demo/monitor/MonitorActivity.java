package com.demo.monitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 主线程
 */
public class MonitorActivity extends Activity implements OnClickListener {

	private TextView tv_Loading;
	private SurfaceView sf_VideoMonitor;
	private View layout;
	private EditText ip;
	private EditText port;
	private EditText userName;
	private EditText passWord;
	private EditText channel;

	private Button start, set, stop;

	private final StartRenderingReceiver receiver = new StartRenderingReceiver();
	/**
	 * 返回标记
	 */
	private boolean backflag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		// 设置用于发广播的上下文
		HC_DVRManager.getInstance().setContext(getApplicationContext());
		initView();
	}

	private DeviceBean getDeviceBean() {
		SharedPreferences sharedPreferences = this.getSharedPreferences(
				"dbinfo", 0);
		String ip = sharedPreferences.getString("ip", "");
		String port = sharedPreferences.getString("port", "");
		String userName = sharedPreferences.getString("userName", "");
		String passWord = sharedPreferences.getString("passWord", "");
		String channel = sharedPreferences.getString("channel", "");
		DeviceBean bean = new DeviceBean();
		 bean.setIP("61.145.102.128");
		 bean.setPort("42745");
		 bean.setUserName("admin");
		 bean.setPassWord("adminghostghost");
		 bean.setChannel("1");
//		bean.setIP(ip);
//		bean.setPort(port);
//		bean.setUserName(userName);
//		bean.setPassWord(passWord);
//		bean.setChannel(channel);
		return bean;
	}

	// 向系统中存入devicebean的相关数据
	public void setDBData(String ip, String port, String userName,
			String passWord, String channel) {
		SharedPreferences sharedPreferences = this.getSharedPreferences(
				"dbinfo", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("ip", ip);
		editor.putString("port", port);
		editor.putString("userName", userName);
		editor.putString("passWord", passWord);
		editor.putString("channel", channel);
		editor.commit();
	}

	protected void startPlay() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(HC_DVRManager.ACTION_START_RENDERING);
		filter.addAction(HC_DVRManager.ACTION_DVR_OUTLINE);
		registerReceiver(receiver, filter);

		tv_Loading.setVisibility(View.VISIBLE);
		tv_Loading.setText(getString(R.string.tv_connect_cam));
		if (backflag) {
			backflag = false;
			new Thread() {
				@Override
				public void run() {
					HC_DVRManager.getInstance().setSurfaceHolder(
							sf_VideoMonitor.getHolder());
					HC_DVRManager.getInstance().realPlay();
				}
			}.start();
		} else {
			new Thread() {
				@Override
				public void run() {
					HC_DVRManager.getInstance().setDeviceBean(getDeviceBean());
					HC_DVRManager.getInstance().setSurfaceHolder(
							sf_VideoMonitor.getHolder());
					HC_DVRManager.getInstance().initSDK();
					HC_DVRManager.getInstance().loginDevice();
					HC_DVRManager.getInstance().realPlay();
				}
			}.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		new Thread() {
			@Override
			public void run() {
				HC_DVRManager.getInstance().logoutDevice();
				HC_DVRManager.getInstance().freeSDK();
			}
		}.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new Thread() {
				@Override
				public void run() {
					HC_DVRManager.getInstance().stopPlay();
				}
			}.start();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.start:
			startPlay();
			break; 
		case R.id.stop:
			HC_DVRManager.getInstance().stopPlay();
			break;
		case R.id.set:
			setPlayer();
			break;
		}
	}

	public void setPlayer() {
		LayoutInflater inflater = getLayoutInflater();
		layout = inflater.inflate(R.layout.alert,(ViewGroup) findViewById(R.id.alert));
		ip = (EditText) layout.findViewById(R.id.ip);
		port = (EditText) layout.findViewById(R.id.port);
		userName = (EditText) layout.findViewById(R.id.userName);
		passWord = (EditText) layout.findViewById(R.id.passWord);
		channel = (EditText) layout.findViewById(R.id.channel);
		DeviceBean db = getDeviceBean();
		ip.setText(db.getIP());
		port.setText(db.getPort());
		userName.setText(db.getUserName());
		passWord.setText(db.getPassWord());
		channel.setText(db.getChannel());

		new AlertDialog.Builder(this).setTitle("设置").setView(layout)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setDBData(ip.getText().toString(), port.getText()
								.toString(), userName.getText().toString(),
								passWord.getText().toString(), channel
										.getText().toString());
					}
				}).setNegativeButton("取消", null).show();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		// 获取手机分辨率
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		tv_Loading = (TextView) findViewById(R.id.tv_Loading);
		sf_VideoMonitor = (SurfaceView) findViewById(R.id.sf_VideoMonitor);

		start = (Button) findViewById(R.id.start);
		start.setOnClickListener(this);
		stop = (Button) findViewById(R.id.stop);
		stop.setOnClickListener(this);
		set = (Button) findViewById(R.id.set);
		set.setOnClickListener(this);

		LayoutParams lp = sf_VideoMonitor.getLayoutParams();
		lp.width = dm.widthPixels - 30;
		lp.height = lp.width / 16 * 9;
		sf_VideoMonitor.setLayoutParams(lp);
		tv_Loading.setLayoutParams(lp);
		Log.d("DEBUG", "视频窗口尺寸：" + lp.width + "x" + lp.height);

		sf_VideoMonitor.getHolder().addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.d("DEBUG", getLocalClassName() + " surfaceDestroyed");
				sf_VideoMonitor.destroyDrawingCache();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Log.d("DEBUG", getLocalClassName() + " surfaceCreated");
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Log.d("DEBUG", getLocalClassName() + " surfaceChanged");
			}
		});

	}

	// 广播接收器
	private class StartRenderingReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (HC_DVRManager.ACTION_START_RENDERING.equals(intent.getAction())) {
				tv_Loading.setVisibility(View.GONE);
			}
			if (HC_DVRManager.ACTION_DVR_OUTLINE.equals(intent.getAction())) {
				tv_Loading.setText(getString(R.string.tv_connect_cam_error));
			}
		}
	}

}
