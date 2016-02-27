package com.yuanhai.test.Info_Fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yhzl.utility.VideoGLSurfaceView;
import com.yuanhai.test.R;

public class CameraView extends RelativeLayout {
	private boolean isSelected = false;
	private GestureDetector gestureDetector;
	private DoubleTapListener doubleTapListener;
	private boolean isFullScreen = false;
	private VideoGLSurfaceView openglView;
	private VideoPlayActivity videoPlayActivity;
	private int cameraIndex;
	
	public CameraView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		FrameLayout.inflate(context, R.layout.camera_view, this);
		
		openglView = (VideoGLSurfaceView)findViewById(R.id.video_view);
		
		setWillNotDraw(false);
		
		SampleGuestListener gestureListener = new SampleGuestListener();  
		gestureDetector = new GestureDetector(gestureListener); 
		doubleTapListener = new DoubleTapListener();
		gestureDetector.setOnDoubleTapListener(doubleTapListener);
	}
	
	public VideoGLSurfaceView getOpenglView()
	{
		return openglView;
	}
	
	public void startLoading(){
		ImageView loadingViw = (ImageView)findViewById(R.id.video_loading);
		loadingViw.setVisibility(View.VISIBLE);
		AnimationDrawable anim = (AnimationDrawable)loadingViw.getBackground();
        anim.start();
	}
	public void stopLoading(){
		ImageView loadingViw = (ImageView)findViewById(R.id.video_loading);
		AnimationDrawable anim = (AnimationDrawable)loadingViw.getBackground();
        anim.stop();
		loadingViw.setVisibility(View.GONE);
	}
		
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		return gestureDetector.onTouchEvent(event); 
	}
	 
	public void setIsSelected(boolean selected)
	{
		isSelected = selected;
		invalidate();
	}	
	
	@Override
	public void onDraw(Canvas canvas)
	{ 
		super.onDraw(canvas);
		
		Paint paint = new Paint(); 
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float)1);
		if(isSelected)
			paint.setColor(Color.RED); 
		else
			paint.setARGB(255, 95, 118, 133); 
		float width = getWidth()-0.5f;
		float height = getHeight()-0.5f;
        canvas.drawLine(0.5f, 0.5f, width, 0.5f, paint);
        canvas.drawLine(0.5f, 0.5f, 0.5f, height, paint);
        canvas.drawLine(width, 0.5f, width, height, paint);
        canvas.drawLine(0.5f, height, width, height, paint);
	}
	
	public void setIsFullScreen(boolean isFull){
		isFullScreen = isFull;
	}
	public boolean isFullScreen(){
		return isFullScreen;
	}
	
	private class DoubleTapListener implements OnDoubleTapListener
	{
		@Override
		public boolean onDoubleTap(MotionEvent arg0) {
			if(isFullScreen)
			{
				isFullScreen = false;
			}
			else
			{
				isFullScreen = true;
			}
			videoPlayActivity.gotoFullScreen(cameraIndex, isFullScreen);
			return true;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return false;
		}
	}
	
	private class SampleGuestListener implements OnGestureListener 
	{
		@Override
		public boolean onDown(MotionEvent arg0) {
			return true;
		}


		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,	float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			videoPlayActivity.onCameraViewClicked(cameraIndex);
			return true;
		} 
	}
	
	public void setCameraInfo(VideoPlayActivity activity, int indexCamera)
	{
		videoPlayActivity = activity;
		cameraIndex = indexCamera;
	}
}
