package com.yuanhai.test.Info_Fragment;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.yuanhai.test.R;
import com.yuanhai.test.UserInfo;
import com.yuanhai.util.Tools;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhzl.utility.Camera;
import com.yhzl.utility.ICameraCallBack;
import com.yhzl.utility.ILiveVideo;
import com.yhzl.utility.IVideoResult;
import com.yhzl.utility.UtilityFactory;
import com.yhzl.utility.VideoGLSurfaceView;

public class AdActivity extends Activity implements OnClickListener, IVideoResult {
	private MyHandler videoResultHandler;
	private int resultRefresh = 0;
	private String xmlResultRefresh;
	private ArrayList<LiveAd> liveAdArray = new ArrayList<LiveAd>();
	private static final int VIDEO_BK_ID = 500; //begin id for video background
	private static final int VIDEO_PLAY_VIEW_ID	= VIDEO_BK_ID * 2; //begin id for video opengl view
	
	private static final int VIDEO_CALL_BACK_CONNECT = 0;
	private static final int VIDEO_CALL_BACK_REFRESH = 1;
	
	//video status
	private static final int VIDEO_NONE = 0;
	private static final int VIDEO_CONNECTING = 1;
	private static final int VIDEO_PLAYING = 2;
	
	//live video
	private class LiveAd
	{
		public String name;
		public String url;
		public String id;
		RelativeLayout video_view;
		Camera camera;
		CameraCallBack cameraCallBack;
		public int video_status;
	};
	
	//if app is not active, delay 60 second, stop play all video
	private final int INACTIVE_STOP_DELAY_SECONDS = 60*1000;
	private boolean isActived = false;
	private Handler handlerOnStopDelay = new Handler();
	private Runnable runnableOnStopDelay = new Runnable() {
	    @Override
	    public void run() {
	    	if(isActived == false)
	    	{
	    		for(int i=0; i<liveAdArray.size(); i++)
	    		{
	    			stopVideo(i);			
	    		}
	    	}
	    	handlerOnStopDelay.removeCallbacks(runnableOnStopDelay); 
	    }
	};
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ad_activity);
		
		findViewById(R.id.top_back).setOnClickListener(this);
		findViewById(R.id.top_refresh).setOnClickListener(this);
		
		xmlResultRefresh = UserInfo.getAdCache(this);
		if(xmlResultRefresh.length() != 0)
			createAdSubViews();
		
		videoResultHandler = new MyHandler(this);
		
		ILiveVideo lv = UtilityFactory.CreateLiveVideo();
		int result = lv.refreshVideo(UserInfo.getUserId(this), UserInfo.getPwd(this), ILiveVideo.FILM_VIDEO, this);
		if(result != 0){
			Toast.makeText(this, 
					getResources().getString(R.string.play_video_fail),
					Toast.LENGTH_SHORT).show();
		}
		Tools.addLoading(this);
	}
	
	private void playVideo(int index)
	{
		if(index < 0 || index >= liveAdArray.size())
			return;
		LiveAd ad = liveAdArray.get(index);
		if(ad.video_status == VIDEO_NONE)
		{
			int result = ad.camera.play(ad.url, false, false);
			if(result == 1){
				ad.video_status = VIDEO_CONNECTING;
				ad.video_view.findViewById(index).setVisibility(View.GONE);
				
				ImageView loadingViw = (ImageView)ad.video_view.findViewById(R.id.ad_video_loading);
				loadingViw.setVisibility(View.VISIBLE);
				AnimationDrawable anim = (AnimationDrawable)loadingViw.getBackground();
		        anim.start();
			}
			else
			{
				Toast.makeText(this, 
					getResources().getString(R.string.play_video_fail),
					Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void stopVideo(int index)
	{
		if(index < 0 || index >= liveAdArray.size())
			return;
		LiveAd ad = liveAdArray.get(index);
		ad.camera.stop();
		if(ad.video_status == VIDEO_PLAYING)
		{
			ad.video_status = VIDEO_NONE;
			ad.video_view.findViewById(index+VIDEO_PLAY_VIEW_ID).setVisibility(View.INVISIBLE);
			ad.video_view.findViewById(index+VIDEO_BK_ID).setVisibility(View.VISIBLE);
		}
	}
	void backToMain(){
		for(int i=0; i<liveAdArray.size(); i++)
		{
			LiveAd ad = liveAdArray.get(i);
			ad.camera.stop();			
			ad.camera.destroy();
		}
		liveAdArray.clear();
		finish();
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.top_back:
			backToMain();
			break;
		case R.id.top_refresh:
			ILiveVideo lv = UtilityFactory.CreateLiveVideo();
			int result = lv.refreshVideo(UserInfo.getUserId(this), UserInfo.getPwd(this), ILiveVideo.FILM_VIDEO, this);
			if(result != 0){
				Toast.makeText(this, 
						getResources().getString(R.string.play_video_fail),
						Toast.LENGTH_SHORT).show();
			}
			Tools.addLoading(this);
			break;
		default:
			if(liveAdArray.size()> 0 ){
				if(id < liveAdArray.size() + VIDEO_BK_ID){ //click to play video
					if(id >= VIDEO_BK_ID)
						id -= VIDEO_BK_ID;
					
					LiveAd ad = liveAdArray.get(id);
					if(ad.video_status == VIDEO_NONE){
						playVideo(id);
					}
				}else if(id < liveAdArray.size() + VIDEO_PLAY_VIEW_ID){ //click to stop play video
					id -= VIDEO_PLAY_VIEW_ID;
					LiveAd ad = liveAdArray.get(id);
					if(ad.video_status == VIDEO_PLAYING){
						stopVideo(id);
					}
				}
			}
			break;
		}
	}	 

	//video connect completed, if success then start play video 
	private void onVideoConnectResult(int index, int connectResult)
	{
		if(index < 0 || index >= liveAdArray.size())
			return;
		LiveAd ad = liveAdArray.get(index);
		
		ImageView loadingViw = (ImageView)ad.video_view.findViewById(R.id.ad_video_loading);
		AnimationDrawable anim = (AnimationDrawable)loadingViw.getBackground();
        anim.stop();
		loadingViw.setVisibility(View.GONE);
		
		ad.video_view.findViewById(index).setVisibility(View.VISIBLE);
		
		if(connectResult == 0)
		{
			Toast.makeText(this, 
					getResources().getString(R.string.play_video_fail),
					Toast.LENGTH_SHORT).show();
			
			ad.video_status = VIDEO_NONE;
			return;
		}
		
		Toast.makeText(this, 
				getResources().getString(R.string.play_video_success),
				Toast.LENGTH_SHORT).show();		
		ad.video_view.findViewById(index+VIDEO_BK_ID).setVisibility(View.INVISIBLE);
		ad.video_view.findViewById(index+VIDEO_PLAY_VIEW_ID).setVisibility(View.VISIBLE);
		
		ad.video_status = VIDEO_PLAYING;
	}
	
	//video refresh completed, recreate all video view
	public void onVideoRefreshResult(){
		Tools.removeLoading(this);
		if(resultRefresh != 0){
			Toast.makeText(this, 
					getResources().getString(R.string.ad_failed),
					Toast.LENGTH_SHORT).show();
			return;
		}
		UserInfo.saveAdCache(this, xmlResultRefresh);
		createAdSubViews();
	}
	
	//main thread message handler
	static class MyHandler extends Handler {
		private AdActivity adActivity;
		MyHandler(AdActivity activity){
			adActivity = activity;
		}
		@Override
		public void handleMessage(Message msg) {
			
			Bundle b = msg.getData();
        	int type = b.getInt("type");
        	
        	if(type == VIDEO_CALL_BACK_REFRESH){ 
        		adActivity.onVideoRefreshResult();
        	}
        	else if(type == VIDEO_CALL_BACK_CONNECT){
        		int index = b.getInt("index");
        		int connectResult = b.getInt("connect_result");
        		adActivity.onVideoConnectResult(index, connectResult);
        	}
		}
	}

	//save refresh result, and send refresh result to main thread
	@Override
	public void onVideoRefreshResult(int result, String xmlResult) {
		resultRefresh = result;
		xmlResultRefresh = xmlResult;
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putInt("type", VIDEO_CALL_BACK_REFRESH);
        msg.setData(b);
		videoResultHandler.sendMessage(msg);
	};
	
	//remove all video video view,if video is playing then stop it
	private void removeAllVideoView(){
		LinearLayout parentView = (LinearLayout)findViewById(R.id.ad_Layout);
		for(int i=0; i<liveAdArray.size(); i++)
		{
			LiveAd ad = liveAdArray.get(i);
			stopVideo(i);
			ad.camera.destroy();
			parentView.removeView(ad.video_view);
		}
		liveAdArray.clear();
	}
	
	//create video view from xml
	private void createAdSubViews(){
		removeAllVideoView();
		//parsy xml to create video view
		try {
			
			InputStream inputStream = new ByteArrayInputStream(xmlResultRefresh.getBytes());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			Element root = document.getDocumentElement();
			
			LinearLayout parentView = (LinearLayout)findViewById(R.id.ad_Layout);
			LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			NodeList videos = root.getElementsByTagName("video");  
			for(int i=0;i<videos.getLength();i++){
				Element video = (Element)videos.item(i);
				LiveAd ad = new LiveAd();
				ad.name = video.getAttribute("name");
				ad.url = video.getAttribute("url");
				ad.id = video.getAttribute("id");
				ad.video_status = VIDEO_NONE;
				
				RelativeLayout video_view = (RelativeLayout)layoutInflater.inflate(R.layout.ad_video, null);
				TextView nameTextView = (TextView)video_view.findViewById(R.id.ad_video_text);
				nameTextView.setText(ad.name);
				ad.video_view = video_view;
				
				RelativeLayout videoBk = (RelativeLayout)video_view.findViewById(R.id.ad_video_bk);
				videoBk.setId(VIDEO_BK_ID + i);
				videoBk.setOnClickListener(this);
				
				Button videoPlay = (Button)video_view.findViewById(R.id.ad_video_play);
				videoPlay.setId(i);
				videoPlay.setOnClickListener(this);
				
				video_view.findViewById(R.id.ad_video_loading).setVisibility(View.GONE);
				
				//create video player, and bind opengl view to player
				ad.cameraCallBack = new CameraCallBack(i);
				ad.camera = new Camera(ad.cameraCallBack);
				VideoGLSurfaceView glView = (VideoGLSurfaceView)video_view.findViewById(R.id.ad_video_view);
				ad.camera.bindView(glView);
				ad.camera.setVideoTimeStrategy(Camera.TIME_STAMP_STRATEGY);
				
				glView.setId(VIDEO_PLAY_VIEW_ID+i);
				glView.setOnClickListener(this);
				
				parentView.addView(video_view);
				liveAdArray.add(ad);
			}
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK){ 
	    	backToMain();
	    	return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPushVideoResult(int result, String videoId, int sessionId, String videoUrl) {
		
	}
	
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
