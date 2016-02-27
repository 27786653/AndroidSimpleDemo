package com.yuanhai.test.Info_Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.component.view.XListView;
import com.component.view.XListView.IXListViewListener;
import com.net.tsz.afinal.FinalBitmap;
import com.yhzl.utility.IGlobalSetting;
import com.yhzl.utility.IOrgWebService;
import com.yhzl.utility.IWebService;
import com.yhzl.utility.UtilityFactory;
import com.yhzl.utility.WebServiceStringResult;
import com.yuanhai.appShows.MainApplication;
import com.yuanhai.test.UserInfo;
import com.yuanhai.util.Tools;
import com.yuanhai.test.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WorkListActivity extends Activity implements OnClickListener,
OnItemClickListener,IXListViewListener{
	private WebServiceStringResult webServiceStringResult = new WebServiceStringResult(0, "");
	
	private int currentPage = 1;
	private MyHandler wsResultHandler;
	
	private XListView workListView;
	private WorkListAdapter workListAdapter;
	
	//all work report array
	private ArrayList<WorkReport> workReportArray = new ArrayList<WorkReport>();
	
	private final int ITEM_COUNT_PRE_PAGE = 10;
	
	ArrayList<WorkReport> getWorkReportArray(){
		return workReportArray;
	}
	

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.working_top_back:
			finish();
			break;
		case R.id.add_work_button:
			Intent intent = new Intent(this, WorkingActivity.class);
			startActivity(intent);
			break;
		default:
			break;	
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.work_list_activity);
		
		wsResultHandler = new MyHandler(this);
		
		findViewById(R.id.working_top_back).setOnClickListener(this);
		findViewById(R.id.add_work_button).setOnClickListener(this);
		
		workListView = (XListView) findViewById(R.id.work_list);
		workListView.setOnItemClickListener(this);
		workListView.setDividerHeight(0);
		workListView.setCacheColorHint(0);
		workListView.setIXListViewListener(this);
		workListView.setPullRefreshEnable(true);
		workListView.setPullLoadEnable(true);
		
		Tools.addLoading(this);
		
		//use local cache xml string to create work list view 
		webServiceStringResult.webServiceResult = UserInfo.getWorkListCache(this);
		createWorkListView();
		
		refreshWorkList();
	}
	
	public void createWorkListView(){
		if(webServiceStringResult.webServiceResult.length() != 0){
			try {
				IGlobalSetting setting = ((MainApplication)getApplication()).getGlobalSetting();
				String url = setting.getNoticePrefixUrl();
				
				JSONObject jsonstr = new JSONObject(webServiceStringResult.webServiceResult);
				JSONArray arr = jsonstr.getJSONArray("data");
				for (int j = 0; j < arr.length(); j++) {
					JSONObject temp = (JSONObject) arr.get(j);
					
					String reportId = temp.getString("worktalkid");
					boolean find = false;
					WorkReport report = null;
					for(int m=0; m<workReportArray.size(); ++m)
					{
						if(reportId.equals(workReportArray.get(m).id)){
							report = workReportArray.get(m);
							find = true;
							break;
						}
					}
					if(find == false){
						report = new WorkReport();
						workReportArray.add(report);
					}
					report.id = reportId;
					report.time = temp.getString("pubdate");
					report.content = temp.getString("content");
					report.customername = temp.getString("customername");
					report.talkcontent = temp.getString("talkcontent");
					report.customerisr = temp.getString("customerisr");
					report.marketisr = temp.getString("marketisr");
					report.seetype = temp.getString("seetype");
					report.filepath = url + temp.getString("filepath");
					report.location = temp.getString("workpos");
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(this, getString(R.string.work_list_failed), Toast.LENGTH_SHORT).show();
				return;
			}
		}
		
		//sort by time
		Comparator<WorkReport> comparator = new Comparator<WorkReport>(){
			   public int compare(WorkReport n1, WorkReport n2) {
				   return n2.time.compareTo(n1.time);
			   }
		};
		Collections.sort(workReportArray, comparator);
		
		//refresh work report list
		if(workListAdapter == null){
			workListAdapter = new WorkListAdapter(this);
			workListView.setAdapter(workListAdapter);
		}else{
			workListAdapter.notifyDataSetChanged();
		}
	}
	
	private void onWorkListRefreshResult(){
		Tools.removeLoading(this);
		workListView.stopRefresh();
		if(webServiceStringResult.result != 0){
			Toast.makeText(this, getString(R.string.work_list_failed), Toast.LENGTH_SHORT).show();
			return;
		}
		if(currentPage == 1)
			UserInfo.saveWorkListCache(this, webServiceStringResult.webServiceResult);
		createWorkListView();
	}
	
	//main thread message handler
	static class MyHandler extends Handler {
		private WorkListActivity workListActivity;
		MyHandler(WorkListActivity activity){
			workListActivity = activity;
		}
		@Override
		public void handleMessage(Message msg) {
			workListActivity.onWorkListRefreshResult();
		}
	}
	
	void refreshWorkList(){
		new Thread(){
			@Override
			public void run() {
				super.run();
				IOrgWebService ws = UtilityFactory.CreateOrgWebService();
				webServiceStringResult = ws.refreshWorkReportList(UserInfo.getUserId(WorkListActivity.this), currentPage, UserInfo.getOrgId(WorkListActivity.this));
				Message msg = new Message();
				wsResultHandler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	public void onRefresh() {
		Tools.addLoading(this);
		currentPage = 1;
		refreshWorkList();
	}

	@Override
	public void onLoadMore() {
		Tools.addLoading(this);
		currentPage = (workReportArray.size()/ITEM_COUNT_PRE_PAGE)+1;
		refreshWorkList();
		workListView.stopLoadMore();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(position-1 == workReportArray.size()){
			Intent intent = new Intent(this, WorkingActivity.class);
			startActivity(intent);
		}
		else
		{
			WorkReport report = workReportArray.get(position-1);
			Intent intent = new Intent(this, WorkingActivity.class);
			intent.putExtra("IsAddWorkReport", false);
			intent.putExtra("pubdate", report.time);
			intent.putExtra("content", report.content);
			intent.putExtra("customername", report.customername);
			intent.putExtra("talkcontent", report.talkcontent);
			intent.putExtra("customerisr", report.customerisr);
			intent.putExtra("marketisr", report.marketisr);
			intent.putExtra("seetype", report.seetype);
			intent.putExtra("filepath", report.filepath);
			intent.putExtra("workPos", report.location);
			startActivity(intent);
		}
	}
}