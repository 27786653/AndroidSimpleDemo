package com.yuanhai.test.Info_Fragment;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CLIENTINFO;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.RealPlayCallBack;
import com.yuanhai.test.R;

import org.MediaPlayer.PlayM4.Player;

/**
 * Created by 李森林 on 2016/2/24.
 */
public class VideoPageFrament extends Fragment {

    private Context activity;
    private View rootView;

    private TextView tv_Loading;
    private SurfaceView sf_VideoMonitor;
    private Button start, stop;

    private String TAG="Video";

    public VideoPageFrament(DeviceBean deviceBean,int channel){
        this.bean=deviceBean;
        this.channel=channel;
    }



    /**
     * 返回标记
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        rootView = inflater.inflate(R.layout.hkvideoplayer_fragment, container,
                false);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        //当前fragment显示时为true,不显示为false

        if(isVisibleToUser) {
            startplay();
        }

    }

    @Override
    public void onStart() {
        initView();
        System.out.println("开始播放");
        super.onStart();
    }
    private Player 			m_oPlayerSDK			= null;
    private HCNetSDK		m_oHCNetSDK				= null;
    private int				m_iLogID				= -1;				// return by NET_DVR_Login_v30
    private int 			m_iPlayID				= -1;				// return by NET_DVR_RealPlay_V30
    private int				m_iPort					= -1;				// play port
    private boolean initeSdk() {
        // get an instance and init net sdk
        m_oHCNetSDK = new HCNetSDK();
        if (null == m_oHCNetSDK)
        {
            return false;
        }
        if (!m_oHCNetSDK.NET_DVR_Init())
        {
            return false;
        }

        // init player
        m_oPlayerSDK = Player.getInstance();
        if (m_oPlayerSDK == null)
        {
            return false;
        }

        return true;
    }//初始化SDK
    private void login(){
        try
        {
            m_iLogID = loginDevice();
            ExceptionCallBack oexceptionCbf = getExceptiongCbf();
            Log.i("ss", "Login sucess ****************************1***************************");
        }
        catch (Exception err)
        {
            Log.e("", "error: " + err.toString());
        }
    }
    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack()
        {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle)
            {
                ;// you can add process here
            }
        };
        return oExceptionCbf;
    }
    private int loginDevice() {
        // get instance
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
//        int iLogID = m_oHCNetSDK.NET_DVR_Login_V30("61.145.102.128", 42745, "admin", "adminghostghost", m_oNetDvrDeviceInfoV30);
        int iLogID = m_oHCNetSDK.NET_DVR_Login_V30(bean.getIP(), Integer.valueOf(bean.getPort()), "admin", "adminghostghost", m_oNetDvrDeviceInfoV30);
        System.out.println("下面是设备信息************************");
        System.out.println("userId=" + m_iLogID);
        System.out.println("通道开始=" + m_oNetDvrDeviceInfoV30.byStartChan);
        System.out.println("通道个数=" + m_oNetDvrDeviceInfoV30.byChanNum);
        System.out.println("设备类型=" + m_oNetDvrDeviceInfoV30.byDVRType);
        System.out.println("ip通道个数=" + m_oNetDvrDeviceInfoV30.byIPChanNum);
        return iLogID;

    }
    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack()
        {
            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize)
            {
                // player channel 1
                VideoPageFrament.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }
    public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        int i = 0;
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
                        if (!m_oPlayerSDK.openStream( m_iPort, pDataBuffer, iDataSize, 2*1024*1024)) //open stream
                        {
                            Log.e(TAG, "openStream failed");
                            break;
                        }

                        if (!m_oPlayerSDK.play(m_iPort, sf_VideoMonitor.getHolder().getSurface()))
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
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        catch (Exception e)
        {
        }
    }


    public void stopPlay() {
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
    public void Cleanup() {
        // release player resource

        m_oPlayerSDK.freePort(m_iPort);

        // release net SDK resource
        m_oHCNetSDK.NET_DVR_Cleanup();
    }
    public int channel; //当前视频通道


    //异步处理请求视频资源
    class myAsynctask extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getActivity(), "加载成功", Toast.LENGTH_SHORT).show();
            rootView.findViewById(R.id.tv_Loading).setVisibility(View.INVISIBLE);
            this.cancel(true);

        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                final RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
                int iFirstChannelNo = Integer.valueOf(channel);// get start channel no
                final NET_DVR_CLIENTINFO ClientInfo = new NET_DVR_CLIENTINFO();
                ClientInfo.lChannel =  iFirstChannelNo; 	// start channel no + preview channel
                ClientInfo.lLinkMode = (1<<31);  			// bit 31 -- 0,main stream;1,sub stream
                // bit 0~30 -- link type,0-TCP;1-UDP;2-multicast;3-RTP
                ClientInfo.sMultiCastIP = null;

                // net sdk start preview
                m_iPlayID = m_oHCNetSDK.NET_DVR_RealPlay_V30(m_iLogID, ClientInfo, fRealDataCallBack, true);
                if (m_iPlayID < 0)
                {
                    Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + m_oHCNetSDK.NET_DVR_GetLastError());
                }
            }
            catch (Exception err)
            {
            }
            return null;
        }
    }
    public void startplay(){
        new myAsynctask().execute();
    }


    /**
     * 初始化
     */
    private void initView() {
              start= (Button) rootView.findViewById(R.id.start);
        if (!initeSdk()) {
            return;
        }

        login();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startplay();
            }
        });
        sf_VideoMonitor = (SurfaceView) rootView.findViewById(R.id.sf_VideoMonitor);
//        sf_VideoMonitor.setBackgroundColor(Color.TRANSPARENT);
        sf_VideoMonitor.setZOrderOnTop(true);
        sf_VideoMonitor.getHolder().setFormat(PixelFormat.TRANSPARENT);
        sf_VideoMonitor.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                sf_VideoMonitor.destroyDrawingCache();
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });
    }

   public DeviceBean bean;//定义设备实体类

    @Override
    public void onPause() {
//        stopPlay();
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        this.stopPlay();
        this.Cleanup();
        super.onDestroy();
    }
}
