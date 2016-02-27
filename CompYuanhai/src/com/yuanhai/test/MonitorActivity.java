package com.yuanhai.test;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CLIENTINFO;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.RealPlayCallBack;
import com.yuanhai.test.Info_Fragment.DeviceBean;
import com.yuanhai.util.Tools;

import org.MediaPlayer.PlayM4.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李森林 on 2016/2/26.
 */
public class MonitorActivity extends Activity implements View.OnClickListener{

    private Player 			m_oPlayerSDK			= null;
    private HCNetSDK		m_oHCNetSDK				= null;
    private int				m_iLogID				= -1;				// return by NET_DVR_Login_v30
    private int 			m_iPlayID				= -1;				// return by NET_DVR_RealPlay_V30
    private int				m_iPort					= -1;				// play port

    private SurfaceView sf_videoPlayer;
    private LinearLayout btnGroup;
    private ArrayList<Fragment> fragmentsList;
    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30;
    private List<Button> btnList=new ArrayList<Button>();

    private String TAG="VIDEOPLAYER";
    public DeviceBean bean;//定义设备实体类
    private int CurVideo=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.hkvideo_activity);
        bean= (DeviceBean) getIntent().getSerializableExtra("bean");
        if(!initeSdk()){     //初始化设备
            return;
        }
        login();
        initViewPage(); //初始化视图

    }
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
    private void initViewPage() {
       sf_videoPlayer = (SurfaceView) findViewById(R.id.sf_videoPlayer);
         btnGroup= (LinearLayout) findViewById(R.id.btn_group);

        for (int i=1;i<=m_oNetDvrDeviceInfoV30.byChanNum;i++) {
            Button btn=new Button(getApplicationContext());
            btn.setOnClickListener(this);
            btn.setId(i);
            btn.setText("btn" + i);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            if(i!=1)layoutParams.addRule(RelativeLayout.BELOW, i-1);
            btn.setLayoutParams(layoutParams);
            btn.setBackgroundResource(R.drawable.btn_style);
            btnList.add(btn);
            btnGroup.addView(btn);
        }
//        sf_VideoMonitor.setBackgroundColor(Color.TRANSPARENT);
        sf_videoPlayer.setZOrderOnTop(true);
        sf_videoPlayer.getHolder().setFormat(PixelFormat.TRANSPARENT);
        sf_videoPlayer.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                sf_videoPlayer.destroyDrawingCache();
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                startPlayer();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });
    }
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
            Log.e(TAG,"PlayCtrl getInstance failed!");
            return false;
        }
        return true;
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
    @Override
    public void onClick(View v) {
            for (Button btn : btnList) {
                if (btn.getId() == v.getId()) {
                    if(CurVideo != v.getId()) {
                        MonitorActivity.this.stopPlay();
                        sf_videoPlayer.setBackgroundColor(0X000);
                        Toast.makeText(MonitorActivity.this, "点击了：" + btn.getId(), Toast.LENGTH_SHORT).show();
                        new myAsynctask().execute(v.getId());
                        CurVideo=v.getId();
                    }
                }
            }
    }
    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
                // player channel 1
                MonitorActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }
    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                ;// you can add process here
            }
        };
        return oExceptionCbf;
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

                        if (!m_oPlayerSDK.play(m_iPort, sf_videoPlayer.getHolder().getSurface()))
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
    public void startPlayer(){
        new myAsynctask().execute(1);
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
    @Override
    protected void onDestroy() {
        stopPlay();
        Cleanup();
        super.onDestroy();
    }
    class myAsynctask extends AsyncTask<Integer,Void,Void> {




        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MonitorActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
            this.cancel(true);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                final RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
                int iFirstChannelNo = params[0];// get start channel no
                final NET_DVR_CLIENTINFO ClientInfo = new NET_DVR_CLIENTINFO();
                ClientInfo.lChannel = iFirstChannelNo;    // start channel no + preview channel
                ClientInfo.lLinkMode = (1 << 31);            // bit 31 -- 0,main stream;1,sub stream
                // bit 0~30 -- link type,0-TCP;1-UDP;2-multicast;3-RTP
                ClientInfo.sMultiCastIP = null;

                // net sdk start preview
                m_iPlayID = m_oHCNetSDK.NET_DVR_RealPlay_V30(m_iLogID, ClientInfo, fRealDataCallBack, true);
                if (m_iPlayID < 0) {
                    Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + m_oHCNetSDK.NET_DVR_GetLastError());
                }
            } catch (Exception err) {
            }
            return null;
        }


    }

}
