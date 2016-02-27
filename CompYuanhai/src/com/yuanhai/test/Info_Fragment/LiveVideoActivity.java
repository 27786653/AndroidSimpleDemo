package com.yuanhai.test.Info_Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhzl.utility.ILiveVideo;
import com.yhzl.utility.IVideoResult;
import com.yhzl.utility.UtilityFactory;
import com.yuanhai.test.R;
import com.yuanhai.test.UserInfo;
import com.yuanhai.util.Tools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
/**
 * 主界面园区实况
 */
public class LiveVideoActivity extends Activity implements OnClickListener, IVideoResult {
	private MyHandler videoResultHandler;
	private int resultRefresh = 0;
	private String xmlResultRefresh;
	private static final int VIDEO_VIEW_BGGIN_ID = 500;
	private static final int MAX_PLAY_VIDEO_COUNT = 4;
	
	//all live video array
	private ArrayList<LiveVideo> liveVideoArray = new ArrayList<LiveVideo>();
	
	//live video
	private class LiveVideo
	{
		public String name;
		public String url;
		public String id;
		public RelativeLayout view;
		public boolean selected;
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.live_video_activity);
		
		findViewById(R.id.top_back).setOnClickListener(this);
		findViewById(R.id.top_refresh).setOnClickListener(this);
		findViewById(R.id.top_play).setOnClickListener(this);
		
		videoResultHandler = new MyHandler(this);
		Tools.addLoading(this);
		
		//use local cache xml string to create live video view 
		xmlResultRefresh = UserInfo.getLiveVideoCache(this);
		CreateLiveVideoView();
		
		//refresh xml string from server
		ILiveVideo lv = UtilityFactory.CreateLiveVideo();
		//lv.refreshVideo(UserInfo.getUserId(this), UserInfo.getPwd(this), ILiveVideo.CAMERA_VIDEO, this);
		
		lv.refreshVideo("1", "1", ILiveVideo.CAMERA_VIDEO, this);
	}

	@Override
	public void onVideoRefreshResult(int result, String xmlResult) {
		resultRefresh = result;
		xmlResultRefresh = xmlResult;
		Message msg = new Message();
		videoResultHandler.sendMessage(msg);
	}
	
	private void playVideo()
	{
		int count = getSelectedVideoCount();
		if(count == 0){
			Toast.makeText(this, 
					getResources().getString(R.string.please_select_video),
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent = new Intent(this, VideoPlayActivity.class);
		intent.putExtra("count", count);
		
		int index = 0;
		for(int i=0; i<liveVideoArray.size(); i++){
			LiveVideo lv = liveVideoArray.get(i);
			if(lv.selected)
			{
				intent.putExtra("id" + index, lv.id);
				intent.putExtra("name" + index, lv.name);
				index++;
			}
		}
		
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.top_play:
			playVideo();
			break;
		case R.id.top_back:
			finish();
			break;
		case R.id.top_refresh:
			Tools.addLoading(this);
			ILiveVideo lv = UtilityFactory.CreateLiveVideo();
			//lv.refreshVideo(UserInfo.getUserId(this), UserInfo.getPwd(this), ILiveVideo.CAMERA_VIDEO, this);
			lv.refreshVideo("1", "1", ILiveVideo.CAMERA_VIDEO, this);
			break;
		default:
			if(id >= VIDEO_VIEW_BGGIN_ID && id < liveVideoArray.size() + VIDEO_VIEW_BGGIN_ID){
				LiveVideo video = liveVideoArray.get(id-VIDEO_VIEW_BGGIN_ID);
				if(video.selected){
					ImageView imgView = (ImageView)video.view.findViewById(R.id.video_select);
					imgView.setBackgroundResource(R.drawable.unselect);
					video.selected = false;
				}
				else{
					
					if(getSelectedVideoCount() == MAX_PLAY_VIDEO_COUNT){
						Toast.makeText(this, 
								getResources().getString(R.string.live_can_not_selected),
								Toast.LENGTH_SHORT).show();
						return;
					}
					
					ImageView imgView = (ImageView)video.view.findViewById(R.id.video_select);
					imgView.setBackgroundResource(R.drawable.selected);
					video.selected = true;
				}
				updateSelectedVideo();
			}
			break;
		}
	}
	//remove all video video view,
	private void removeAllVideoView(){
		LinearLayout parentView = (LinearLayout)findViewById(R.id.video_Layout);
		for(int i=0; i<liveVideoArray.size(); i++){
			LiveVideo lv = liveVideoArray.get(i);
			parentView.removeView(lv.view);
		}
		liveVideoArray.clear();
	}
	
	private void onVideoRefreshResult(){
		Tools.removeLoading(this);
		if(resultRefresh != 0){
			Toast.makeText(this, 
					getResources().getString(R.string.live_failed),
					Toast.LENGTH_SHORT).show();
			return;
		}
		UserInfo.saveLiveVideoCache(this, xmlResultRefresh);
		CreateLiveVideoView();
	}
	
	private void CreateLiveVideoView()
	{
		removeAllVideoView();
		
		//parsy xml to create video view
		try {
			
			InputStream inputStream = new ByteArrayInputStream(xmlResultRefresh.getBytes());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			Element root = document.getDocumentElement();
			
			LinearLayout parentView = (LinearLayout)findViewById(R.id.video_Layout);
			LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			NodeList videos = root.getElementsByTagName("video");  
			for(int i=0;i<videos.getLength();i++){
				Element video = (Element)videos.item(i);
				LiveVideo lv = new LiveVideo();
				lv.name = video.getAttribute("name");
				lv.url = video.getAttribute("url");
				lv.id = video.getAttribute("id");
				lv.selected = false;
				
				RelativeLayout view = (RelativeLayout)layoutInflater.inflate(R.layout.live_video_item, null);
				TextView tv = (TextView)view.findViewById(R.id.video_text);
				tv.setText(lv.name);
				
				lv.view = view;
				lv.selected = isVideoPreviousSelected(lv.id);
				ImageView imgView = (ImageView)view.findViewById(R.id.video_select);
				if(lv.selected)
					imgView.setBackgroundResource(R.drawable.selected);
				else					
					imgView.setBackgroundResource(R.drawable.unselect);
				
				view.setOnClickListener(this);
				view.setId(VIDEO_VIEW_BGGIN_ID+i);
				
				parentView.addView(view);
				liveVideoArray.add(lv);
			}
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//main thread message handler
	static class MyHandler extends Handler {
		private LiveVideoActivity liveVideoActivity;
		MyHandler(LiveVideoActivity activity){
			liveVideoActivity = activity;
		}
		@Override
		public void handleMessage(Message msg) {
			liveVideoActivity.onVideoRefreshResult();
		}
	}
	
	private int getSelectedVideoCount(){
		int count = 0;
		for(int i=0; i<liveVideoArray.size(); i++){
			LiveVideo lv = liveVideoArray.get(i);
			if(lv.selected)
				count++;
		}
		return count;
	}
	
	private void updateSelectedVideo(){
		String selected = new String();
		for(int i=0; i<liveVideoArray.size(); i++){
			LiveVideo lv = liveVideoArray.get(i);
			if(lv.selected)
			{
				selected += lv.id;
				selected += ":" ;
			}
		}
		UserInfo.saveSelectedVideo(this, selected);
	}
	private boolean isVideoPreviousSelected(String videoId){
		String selected = UserInfo.getSelectedVideo(this);
		
		String arr[] = selected.split(":"); 
		for(int i=0; i<arr.length; i++){
			if(arr[i].equals(videoId))
				return true;
		}
		return false;
	}

	@Override
	public void onPushVideoResult(int result, String videoId, int sessionId, String videoUrl) {
		// TODO Auto-generated method stub
		
	}
}
