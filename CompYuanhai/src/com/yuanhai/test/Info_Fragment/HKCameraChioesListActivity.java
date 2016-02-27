package com.yuanhai.test.Info_Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.component.view.XListView;
import com.yuanhai.test.MonitorActivity;
import com.yuanhai.test.R;
import com.yuanhai.util.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李森林 on 2016/2/24.
 */
public class HKCameraChioesListActivity  extends Activity implements View.OnClickListener,
        AdapterView.OnItemClickListener,XListView.IXListViewListener {

    private XListView workListView;
    private ArrayAdapter<String> workListAdapter;
    List<String> sss;


    //all work report array
    private ArrayList<WorkReport> workReportArray = new ArrayList<WorkReport>();

    private final int ITEM_COUNT_PRE_PAGE = 10;
    private Handler wsResultHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Tools.removeLoading(HKCameraChioesListActivity.this);
            workListView.stopRefresh();
            if(isFirshChioeItem){
                for (int i=0;i<10;i++){
                    workListAdapter.add("区域" + i);
                }
            }else{
                sss.add( "-地点A");
                sss.add("-地点B");
                sss.add("-地点C");
                sss.add("-地点D");
                workListAdapter.notifyDataSetChanged();
            }


        }
    };


    void refreshWorkList(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                wsResultHandler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hkvideo_listview_activity);
        findViewById(R.id.working_top_back).setOnClickListener(this);

        workListView = (XListView) findViewById(R.id.work_list);
        workListView.setOnItemClickListener(this);
        workListView.setDividerHeight(0);
        workListView.setCacheColorHint(0);
        workListView.setIXListViewListener(this);
        workListView.setPullRefreshEnable(true);
        workListView.setPullLoadEnable(true);

        Tools.addLoading(HKCameraChioesListActivity.this);


        sss = new ArrayList<String>();

        workListAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,sss);
        workListView.setAdapter(workListAdapter);
        refreshWorkList();
    }














    //////////////////////////////////时间侦听接口实现//////////////////////////////////////////////
    @Override
    public void onRefresh() {
        Tools.addLoading(this);
        refreshWorkList();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.working_top_back:
                finish();
                break;
        }
    }
Boolean isFirshChioeItem=true;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(isFirshChioeItem){
            sss.clear();
            workListAdapter.notifyDataSetChanged();
            wsResultHandler.sendMessage(new Message());
            isFirshChioeItem=false;
        }else{
            Intent i=new Intent(this,MonitorActivity.class);
            i.putExtra("bean",getDeviceBean());
            startActivity(i);
        }

    }

    //得到设备属性
    public DeviceBean getDeviceBean(){
        DeviceBean  bean= new DeviceBean();
        String ip ="61.145.102.128";
        String port = "42745";
        String userName = "admin";
        String passWord =  "adminghostghost";
        bean.setIP(ip);
        bean.setPort(port);
        bean.setUserName(userName);
        bean.setPassWord(passWord);
        bean.setChannel("1");
        return bean;
    }
}
