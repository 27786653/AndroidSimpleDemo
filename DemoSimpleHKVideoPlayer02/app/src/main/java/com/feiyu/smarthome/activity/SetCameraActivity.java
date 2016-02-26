package com.feiyu.smarthome.activity;

import java.util.Calendar;

import org.MediaPlayer.PlayM4.Player;


import com.feiyu.smarthome.R;
import com.feiyu.smarthome.net.NetData;
import com.feiyu.smarthome.net.NetWork;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CLIENTINFO;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_JPEGPARA;

import com.hikvision.netsdk.RealPlayCallBack;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;


public class SetCameraActivity  extends Activity{

	private ImageView start=null;
	private ImageView capture=null;
	private SurfaceView surfaceView=null;
	private Player 			m_oPlayerSDK			= null;
	private HCNetSDK		m_oHCNetSDK				= null;
	private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
	private final String 	TAG						= "SetCameraActivity";
	private int				m_iLogID				= -1;				// return by NET_DVR_Login_v30
	private int 			m_iPlayID				= -1;				// return by NET_DVR_RealPlay_V30
	private int				m_iPlaybackID			= -1;				// return by NET_DVR_PlayBackByTime	
	//private byte			m_byGetFlag				= 1;				// 1-get net cfg, 0-set net cfg 
	private int				m_iPort					= -1;				// play port
	//private	NET_DVR_NETCFG_V30 NetCfg = new NET_DVR_NETCFG_V30();
	
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.surface);
		if (!initeSdk())
        {
        	this.finish();
        	return;
        }
		login();
		surfaceView=(SurfaceView)this.findViewById(R.id.surfaceView);
		
		start=(ImageView)this.findViewById(R.id.start);
		start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try
				{
						RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();

						int iFirstChannelNo = m_oNetDvrDeviceInfoV30.byStartChan;// get start channel no
						
						Log.i(TAG, "iFirstChannelNo:" +iFirstChannelNo);
						
						NET_DVR_CLIENTINFO ClientInfo = new NET_DVR_CLIENTINFO();
				        ClientInfo.lChannel =  iFirstChannelNo; 	// start channel no + preview channel
				        ClientInfo.lLinkMode = (1<<31);  			// bit 31 -- 0,main stream;1,sub stream
				        										// bit 0~30 -- link type,0-TCP;1-UDP;2-multicast;3-RTP 
				        ClientInfo.sMultiCastIP = null;
				        
						// net sdk start preview
				        m_iPlayID = m_oHCNetSDK.NET_DVR_RealPlay_V30(m_iLogID, ClientInfo, fRealDataCallBack, true);
						if (m_iPlayID < 0)
						{
						 	Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + m_oHCNetSDK.NET_DVR_GetLastError());
						 	return;
						}
						
						Log.i(TAG, "NetSdk Play sucess ***********************3***************************");
											
						start.setImageResource(R.drawable.playback_fullscreen_play_sel);
				}
				catch (Exception err)
				{
					Log.e(TAG, "error: " + err.toString());
				}
			}
		});
		
		capture=(ImageView)this.findViewById(R.id.capture);
		capture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//new HikCameraFunction().capture();
				try
				{
					NET_DVR_JPEGPARA jpeg=new NET_DVR_JPEGPARA();
					 jpeg.wPicSize=0;
					 jpeg.wPicQuality=1;

					 Calendar calCurrent=Calendar.getInstance();

					 String currentTime=""+calCurrent.get(Calendar.DATE)+calCurrent.get(Calendar.MONTH)+calCurrent.get(Calendar.YEAR)+calCurrent.get(Calendar.HOUR)+calCurrent.get(Calendar.MINUTE)+calCurrent.get(Calendar.SECOND);
					 String picName="/sdcard/"+currentTime+".bmp";
					 //m_oHCNetSDK.NET_DVR_CaptureJPEGPicture(m_iLogID, m_oNetDvrDeviceInfoV30.byStartChan, jpeg, "/sdcard/Capture.bmp");
					 m_oHCNetSDK.NET_DVR_CaptureJPEGPicture(m_iLogID, m_oNetDvrDeviceInfoV30.byStartChan, jpeg, picName);
				}
				catch (Exception err)
				{
					Log.e(TAG, "error: " + err.toString());
				}
			}
		});
	}
	private boolean initeSdk()
	{
		// get an instance and init net sdk
		m_oHCNetSDK = new HCNetSDK();
    	if (null == m_oHCNetSDK)
    	{
    		Log.e(TAG, "m_oHCNetSDK new is failed!");
    		return false;
    	}
    	
    	if (!m_oHCNetSDK.NET_DVR_Init())
    	{
    		Log.e(TAG, "HCNetSDK init is failed!");
    		return false;
    	}
    	
    	// init player
    	m_oPlayerSDK = Player.getInstance();
    	if (m_oPlayerSDK == null)
    	{
    		Log.e(TAG,"PlayCtrl getInstance failed!");
    		return false;
    	}
    	
    	return true;
	}
	private ExceptionCallBack getExceptiongCbf()
	{
	    ExceptionCallBack oExceptionCbf = new ExceptionCallBack()
        {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle)
            {
            	;// you can add process here
            }
        };
        return oExceptionCbf;
	}
	private void login(){
		try
		{
				m_iLogID = loginDevice();
				ExceptionCallBack oexceptionCbf = getExceptiongCbf();
				Log.i(TAG, "Login sucess ****************************1***************************");
		}
		catch (Exception err)
		{
			Log.e(TAG, "error: " + err.toString());
		}
	}
	private void stopPlay()
	{
		if ( m_iPlayID < 0)
		{
			Log.e(TAG, "m_iPlayID < 0");
			return;
		}
		
		//  net sdk stop preview
		if (!m_oHCNetSDK.NET_DVR_StopRealPlay(m_iPlayID))
		{
			Log.e(TAG, "StopRealPlay is failed!Err:" + m_oHCNetSDK.NET_DVR_GetLastError());
			return;
		}
		
		// player stop play
		if (!m_oPlayerSDK.stop(m_iPort)) 
        {
            Log.e(TAG, "stop is failed!");
            return;
        }	
		
		if(!m_oPlayerSDK.closeStream(m_iPort))
		{
            Log.e(TAG, "closeStream is failed!");
            return;
        }
		if(!m_oPlayerSDK.freePort(m_iPort))
		{
            Log.e(TAG, "freePort is failed!");
            return;
        }
		m_iPort = -1;
		// set id invalid
		m_iPlayID = -1;		
	}
	private RealPlayCallBack getRealPlayerCbf()
	{
	    RealPlayCallBack cbf = new RealPlayCallBack()
        {
             public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize)
             {
            	// player channel 1
            	 SetCameraActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
             }
        };
        return cbf;
	}

	
	private int loginDevice()
	{
		// get instance
		m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
		int iLogID = m_oHCNetSDK.NET_DVR_Login_V30("61.145.102.128", 42745, "admin", "adminghostghost", m_oNetDvrDeviceInfoV30);
		return iLogID;
	}
	/**
     * @fn processRealData
     * @author huyf
     * @brief process real data
     * @param iPlayViewNo - player channel [in]
     * @param iDataType	  - data type [in]
     * @param pDataBuffer - data buffer [in]
     * @param iDataSize   - data size [in]
     * @param iStreamMode - stream mode [in]
     * @param NULL [out]
     * @return NULL
     */
	public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode)
	{
		int i = 0;
	  ///  Log.i(TAG, "iPlayViewNo:" + iPlayViewNo + "iDataType:" + iDataType + "iDataSize:" + iDataSize);
	    try
        {
	    	switch (iDataType)
	    	{
	    		case HCNetSDK.NET_DVR_SYSHEAD:
	    			if(m_iPort >= 0)
	    			{
	    				break;
	    			}
	    			m_iPort = m_oPlayerSDK.getPort();
	    			if(m_iPort == -1)
	    			{
	    				Log.e(TAG, "getPort is failed!");
	    				break;
	    			}
	    			if (iDataSize > 0)
	    			{
	    				if (!m_oPlayerSDK.setStreamOpenMode(m_iPort, iStreamMode))  //set stream mode
	    				{
	    					Log.e(TAG, "setStreamOpenMode failed");
	    					break;
	    				}
	    				if(!m_oPlayerSDK.setSecretKey(m_iPort, 1, "ge_security_3477".getBytes(), 128))
	    				{
	    					Log.e(TAG, "setSecretKey failed");
	    					break;
	    				}
	    				if (!m_oPlayerSDK.openStream(m_iPort, pDataBuffer, iDataSize, 2*1024*1024)) //open stream
	    				{
	    					Log.e(TAG, "openStream failed");
	    					break;
	    				}

	    				if (!m_oPlayerSDK.play(m_iPort, surfaceView.getHolder().getSurface()))
	    				{
	    					Log.e(TAG, "play failed");
	    					break;
	    				}
	    			}
	    			break;
	    		case HCNetSDK.NET_DVR_STREAMDATA:
	    		case HCNetSDK.NET_DVR_STD_AUDIODATA:
	    		case HCNetSDK.NET_DVR_STD_VIDEODATA:
	    			if (iDataSize > 0 && m_iPort != -1)
	    			{
	    				for(i = 0; i < 400; i++)
	    				{
	    					if (m_oPlayerSDK.inputData(m_iPort, pDataBuffer, iDataSize))
		    				{
		    					break;
		    				}
	    					Thread.sleep(10);
	    				}
	    				if(i == 400)
	    				{
	    					Log.e(TAG, "inputData failed");
	    				}
	    			}
	    			break;
	    		default:
	    			break;
	    	}
        }
        catch (Exception e)
        {
            Log.e(TAG, "processRealData Exception!err:" + e.toString());
        }
	}

	public void Cleanup()
    {
        // release player resource

        m_oPlayerSDK.freePort(m_iPort);
        
        // release net SDK resource
	    m_oHCNetSDK.NET_DVR_Cleanup();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
         switch (keyCode)
         {
         case KeyEvent.KEYCODE_BACK:
        	  stopPlay();
        	  Cleanup();
              android.os.Process.killProcess(android.os.Process.myPid());
              break;
         default:
              break;
         }
     
         return true;
    }
}