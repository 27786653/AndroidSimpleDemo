package com.yuanhai.test.Info_Fragment;

import java.util.ArrayList;
import com.yhzl.utility.Camera;
import com.yhzl.utility.ICameraCallBack;
import com.yhzl.utility.ILiveVideo;
import com.yhzl.utility.IVideoResult;
import com.yhzl.utility.UtilityFactory;
import com.yuanhai.test.R;
import com.yuanhai.test.UserInfo;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class VideoPlayActivity extends Activity implements OnClickListener, IVideoResult {

	private static final int CAMERA_STATUS_STOP = 0;
	private static final int CAMERA_STATUS_PRE_PLAY = 1;
	private static final int CAMERA_STATUS_CONNECTTING = 2;
	private static final int CAMERA_STATUS_PLAYING = 3;
	
	// camera video information
	private class VideoCamera {
		public String name;
		public String url;
		public String id;
		public Camera camera;
		public CameraCallBack cameraCallBack;
		public CameraView view;
		public int status;
		public int sessionID;
	};
	
	private static final int VIDEO_CALL_BACK_CONNECT = 0;
	private static final int VIDEO_CALL_BACK_PREPLAY = 1;
	// all live video array
	private ArrayList<VideoCamera> videoArray = new ArrayList<VideoCamera>();
	
	//camera view id
	private int[] viewIDs;
	
	private int selectedCamera = 0;

	private MyHandler videoResultHandler = new MyHandler(this);
	
	//if app is not active, delay 60 second, stop play all video
	private final int INACTIVE_STOP_DELAY_SECONDS = 60*1000;
	private boolean isActived = false;
	private Handler handlerOnStopDelay = new Handler();
	private Runnable runnableOnStopDelay = new Runnable() {
	    @Override
	    public void run() {
	    	if(isActived == false)
	    		stopAllVideo();
	    	handlerOnStopDelay.removeCallbacks(runnableOnStopDelay); 
	    }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_play);
		
		findViewById(R.id.top_back).setOnClickListener(this);
		findViewById(R.id.play_button).setOnClickListener(this);
		findViewById(R.id.stop_button).setOnClickListener(this);
		findViewById(R.id.snapshot_button).setOnClickListener(this);
		findViewById(R.id.full_play_button).setOnClickListener(this);
		findViewById(R.id.full_stop_button).setOnClickListener(this);
		findViewById(R.id.full_snapshot_button).setOnClickListener(this);
		findViewById(R.id.full_screen_button).setOnClickListener(this);
		findViewById(R.id.cancel_full_screen_button).setOnClickListener(this);
		
		findViewById(R.id.full_screen_bar).getBackground().setAlpha(80);
		findViewById(R.id.full_screen_bar).setVisibility(View.GONE);
		createCameraWithIntent();
	}
	
	public void gotoFullScreen(int index, boolean isFull)
	{
		if(videoArray.size() == 0)
			return;
		if(isFull)
		{
			findViewById(R.id.video_play_top_bar).setVisibility(View.GONE);
			findViewById(R.id.video_play_buttom_bar).setVisibility(View.GONE);
			
			for(int i=0; i<videoArray.size(); i++){
				if(i != index){
					VideoCamera camera = videoArray.get(i);
					camera.view.setVisibility(View.GONE);
					camera.view.getOpenglView().setVisibility(View.GONE);
				}
			}
			
			int viewID = videoArray.get(index).view.getId();
			if(viewID == R.id.video_view0 || viewID == R.id.video_view1){
				findViewById(R.id.video_view_line1).setVisibility(View.GONE);
			}else{
				findViewById(R.id.video_view_line0).setVisibility(View.GONE);
			}
			
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			Toast toast;
			toast = Toast.makeText(this, R.string.full_screen_tip, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, toast.getXOffset()/2, toast.getYOffset()/2); 
			toast.show();
		}
		else
		{
			findViewById(R.id.video_play_top_bar).setVisibility(View.VISIBLE);
			findViewById(R.id.video_play_buttom_bar).setVisibility(View.VISIBLE);
			for(int i=0; i<videoArray.size(); i++){
				if(i != index){
					VideoCamera camera = videoArray.get(i);
					camera.view.setVisibility(View.VISIBLE);
					if(camera.camera.isPlay() && camera.status == CAMERA_STATUS_PLAYING)
						camera.view.getOpenglView().setVisibility(View.VISIBLE);
				}
			}
			int viewID = videoArray.get(index).view.getId();
			if(viewID == R.id.video_view0 || viewID == R.id.video_view1){
				if(videoArray.size() > 1)
					findViewById(R.id.video_view_line1).setVisibility(View.VISIBLE);
			}else{
				findViewById(R.id.video_view_line0).setVisibility(View.VISIBLE);
			}
			
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			findViewById(R.id.full_screen_bar).setVisibility(View.INVISIBLE);
		}
		videoArray.get(index).view.setIsFullScreen(isFull);
	}
	//layout camera video view
	private void layoutByVideoCount(int count)
	{
		switch(count)
		{
		case 1:
			viewIDs = new int [1];
			viewIDs[0] = R.id.video_view0;
			findViewById(R.id.video_view_line1).setVisibility(View.GONE);
			findViewById(R.id.video_view1).setVisibility(View.GONE);
			findViewById(R.id.video_view2).setVisibility(View.GONE);
			findViewById(R.id.video_view3).setVisibility(View.GONE);
			break;
		case 2:
			viewIDs = new int [2];
			viewIDs[0] = R.id.video_view0;
			viewIDs[1] = R.id.video_view2;
			findViewById(R.id.video_view1).setVisibility(View.GONE);
			findViewById(R.id.video_view3).setVisibility(View.GONE);
			break;
		case 3:
			viewIDs = new int [3];
			viewIDs[0] = R.id.video_view0;
			viewIDs[1] = R.id.video_view1;
			viewIDs[2] = R.id.video_view2;
			LinearLayout l = (LinearLayout)findViewById(R.id.video_view_line0);
			l.setOrientation(LinearLayout.VERTICAL);
			l = (LinearLayout)findViewById(R.id.video_view_line1);
			l.setLayoutParams(
	                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,   
	                                              LayoutParams.MATCH_PARENT,
	                                              2)
	        );
			findViewById(R.id.video_view3).setVisibility(View.GONE);
			break;
		default:
			viewIDs = new int [4];
			viewIDs[0] = R.id.video_view0;
			viewIDs[1] = R.id.video_view1;
			viewIDs[2] = R.id.video_view2;
			viewIDs[3] = R.id.video_view3;
			break;
		}
	}
	
	private void stopPlayCamera(VideoCamera camera){
		camera.camera.stop();
		if(camera.status == CAMERA_STATUS_PLAYING){
			if(camera.sessionID != 0 ){
				ILiveVideo lv = UtilityFactory.CreateLiveVideo();
				lv.stopPushVieo(camera.sessionID);
				camera.sessionID = 0;
			}
			camera.view.getOpenglView().setVisibility(View.INVISIBLE);
		}
		if(camera.status == CAMERA_STATUS_PRE_PLAY || camera.status == CAMERA_STATUS_CONNECTTING)
			camera.view.stopLoading();
		
		camera.status = CAMERA_STATUS_STOP;
	}
	
	private void snapshot(VideoCamera camera){
		if(camera.camera.isPlay() && camera.status == CAMERA_STATUS_PLAYING){
			Camera.SnapshotResult  sr = camera.camera.snapshot();
			
			Toast toast;
			if(sr.result == Camera.SnapshotResult.SNAPSHOT_SUCCESSED)
			{
				String text = getString(R.string.snapshot_successed) + sr.path;
				toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
			}
			else
			{
				toast = Toast.makeText(this, R.string.snapshot_failed, Toast.LENGTH_LONG);
			}
			toast.setGravity(Gravity.CENTER, toast.getXOffset()/2, toast.getYOffset()/2); 
			toast.show();
		}
	}
	
	private void playCamera(VideoCamera camera){
		if(camera.status != CAMERA_STATUS_STOP)
			return;
		ILiveVideo lv = UtilityFactory.CreateLiveVideo();
		int result = lv.pushVideo(UserInfo.getUserId(this), UserInfo.getPwd(this),
				camera.id, this);
		if(result != 0)
			Toast.makeText(this, 
				"'" + camera.name + "'" + getResources().getString(R.string.play_video_fail),
				Toast.LENGTH_SHORT).show();
		else
		{
			camera.status = CAMERA_STATUS_PRE_PLAY;
			camera.view.startLoading();
		}
	}

	private void createCameraWithIntent() {
		Intent intent = getIntent();
		int count = intent.getIntExtra("count", 1);
		layoutByVideoCount(count);
		
		for (int i = 0; i < count; i++) {
			VideoCamera camera = new VideoCamera();
			camera.id = intent.getStringExtra("id" + i);
			camera.name = intent.getStringExtra("name" + i);
			videoArray.add(camera);
			
			//create video player, and bind opengl view to player
			camera.cameraCallBack = new CameraCallBack(i);
			camera.camera = new Camera(camera.cameraCallBack);
			CameraView view = (CameraView)findViewById(viewIDs[i]);
			camera.camera.bindView(view.getOpenglView());
			view.setCameraInfo(this, i);
			camera.view = view;
			camera.status = CAMERA_STATUS_STOP;
			camera.sessionID = 0;
			
			if(i==0)
				view.setIsSelected(true);

			playCamera(camera);
		}
	}

	@Override
	public void onPushVideoResult(int result, String videoId, int sessionId, String videoUrl) {
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putInt("result", result);
		b.putString("videoID", videoId);
		b.putString("url", videoUrl);
		b.putInt("session_id", sessionId);
		b.putInt("type", VIDEO_CALL_BACK_PREPLAY);
        msg.setData(b);  
		videoResultHandler.sendMessage(msg);
	}

	@Override
	public void onVideoRefreshResult(int arg0, String arg1) {
		
	}
	
	public void onPreplayVideo(Bundle b){
		int result = b.getInt("result");
		String vid = b.getString("videoID");
		for (int i = 0; i < videoArray.size(); i++) {
			VideoCamera camera = videoArray.get(i);
			if(camera.id.equals(vid) && camera.status == CAMERA_STATUS_PRE_PLAY){
				if(result == 0){
					camera.url = b.getString("url");
					result = camera.camera.play(camera.url, false, true);
					if(result == 1)
					{
						camera.sessionID = b.getInt("session_id");
						camera.status = CAMERA_STATUS_CONNECTTING;
						return;
					}
				}
				camera.status = CAMERA_STATUS_STOP;
				camera.view.stopLoading();
				Toast.makeText(this, 
						"'" + camera.name + "'" + getResources().getString(R.string.play_video_fail),
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void onConnectVideo(Bundle b){
		int connectResult = b.getInt("connect_result");
		int index = b.getInt("index");
		if(index <0 || index >= videoArray.size())
			return;
		VideoCamera camera = videoArray.get(index);
		if(camera.status == CAMERA_STATUS_CONNECTTING){
			camera.view.stopLoading();
			if(connectResult == 0)
			{
				camera.status = CAMERA_STATUS_STOP;
				Toast.makeText(this, 
						"'" + camera.name + "'" + getResources().getString(R.string.play_video_fail),
						Toast.LENGTH_SHORT).show();
				return;
			}	
			camera.status = CAMERA_STATUS_PLAYING;
			camera.view.getOpenglView().setVisibility(View.VISIBLE);
		}
	}
	
	//main thread message handler
	static class MyHandler extends Handler {
		private VideoPlayActivity playActivity;
		MyHandler(VideoPlayActivity activity){
			playActivity = activity;
		}
		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
        	int type = b.getInt("type");
        	if(type == VIDEO_CALL_BACK_PREPLAY){ 
        		playActivity.onPreplayVideo(b);
        	}
        	else if(type == VIDEO_CALL_BACK_CONNECT){
        		playActivity.onConnectVideo(b);
        	}
		}
	}
	
	private class CameraCallBack implements ICameraCallBack
	{
		public int index;
		
		public CameraCallBack(int index)
		{
			this.index = index;
		}
		@Override
		public void onConnectResult(int result) {
			//send connect result to main thread
			Message msg = new Message(); 
			Bundle b = new Bundle();
			b.putInt("connect_result", result);
			b.putInt("index", index);
			b.putInt("type", VIDEO_CALL_BACK_CONNECT);
	        msg.setData(b);  
	        videoResultHandler.sendMessage(msg);
		}
	}
	
	void stopAllVideo(){
		for(int i=0; i<videoArray.size(); i++){
			VideoCamera camera = videoArray.get(i);
			stopPlayCamera(camera);
		}
	}
	void backToMain(){
		stopAllVideo();
		for(int i=0; i<videoArray.size(); i++){
			VideoCamera camera = videoArray.get(i);
			camera.camera.destroy();
		}
		videoArray.clear();
		finish();
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.top_back:
			backToMain();
			break;
		case R.id.play_button:
		case R.id.full_play_button:
			playCamera(videoArray.get(selectedCamera));
			break;
		case R.id.stop_button:
		case R.id.full_stop_button:
			stopPlayCamera(videoArray.get(selectedCamera));
			break;
		case R.id.snapshot_button:
		case R.id.full_snapshot_button:
			snapshot(videoArray.get(selectedCamera));
			break;
		case R.id.full_screen_button:
			gotoFullScreen(selectedCamera, true);
			break;
		case R.id.cancel_full_screen_button:
			gotoFullScreen(selectedCamera, false);
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK){ 
	    	if(videoArray.get(selectedCamera).view.isFullScreen())
	    		gotoFullScreen(selectedCamera, false);
	    	else
	    		backToMain();
	    	return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void onCameraViewClicked(int index)
	{
		if(selectedCamera == index )
		{
			if(videoArray.get(selectedCamera).view.isFullScreen()){
				handlerFullScreenBarShow.postDelayed(runnableFullScreenBarShow, 3000);
				findViewById(R.id.full_screen_bar).setVisibility(View.VISIBLE);
			}
		}
		else
		{
			videoArray.get(selectedCamera).view.setIsSelected(false);
			videoArray.get(index).view.setIsSelected(true);
			selectedCamera = index;
		}
	}
	
	//full screen bar auto hide
	private Handler handlerFullScreenBarShow = new Handler();
	private Runnable runnableFullScreenBarShow = new Runnable() {
	    @Override
	    public void run() {
	    	findViewById(R.id.full_screen_bar).setVisibility(View.GONE);
	    	handlerFullScreenBarShow.removeCallbacks(runnableFullScreenBarShow); 
	    }
	};
	
	@Override
    protected void onStop()
	{
		super.onStop();
		isActived = false;
		//delay INACTIVE_STOP_DELAY_SECONDS seconds, auto stop all video
		handlerOnStopDelay.postDelayed(runnableOnStopDelay, INACTIVE_STOP_DELAY_SECONDS);
	}
	@Override
    protected void onResume()
	{
		super.onResume();
		isActived = true;
	}
}
