package com.yuanhai.test.Info_Fragment;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.component.view.XListView;
import com.component.view.XListView.IXListViewListener;
import com.net.tsz.afinal.FinalBitmap;
import com.yhzl.utility.IGlobalSetting;
import com.yuanhai.appShows.MainApplication;
import com.yuanhai.test.R;
import com.yuanhai.test.FlashActivity;
import com.yuanhai.test.UserInfo;
import com.yuanhai.util.Tools;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import java.util.Collections;
import java.util.Comparator;

public class NoticeActivity extends Activity implements OnClickListener,
OnItemClickListener,IXListViewListener{
	private XListView noticList;
	private ArrayList<NoticeBean> noticeBeans;
	private String phonehtmlpath;
	private ImageView topImageView;
	private FinalBitmap finalbitmap;
	private NoticeAdapter noticeAdapter;
	private int currentFreshPage = 1;
	private final int ITEM_COUNT_PRE_PAGE = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.notic_fragment);
		finalbitmap = FinalBitmap.create(this);
		noticeBeans = new ArrayList<NoticeBean>();
		initView();
		
		String noticeJsonString = UserInfo.getNoticeCache(this);
		if(noticeJsonString.length() != 0)
			RefreshAllNoticeView(0, noticeJsonString);
		
		Tools.addLoading(this);
		NoticDataDealService noticeservice = new NoticDataDealService ();
		noticeservice.freshData(notichandler, this, 1);
		FlashActivity.setNoticeActivity(this);
	}
	
	private void initView() {
		findViewById(R.id.notic_top_back).setOnClickListener(this);
		findViewById(R.id.notic_top_refalsh).setOnClickListener(this);
		LayoutInflater inflater = getLayoutInflater();
		View topImageLayout = inflater.inflate(R.layout.notice_top_image, null);//这张如片也是在handler里面设置下载的
		topImageView = (ImageView) topImageLayout.findViewById(R.id.notice_top_image);
		topImageView.setOnClickListener(this);
		noticList = (XListView) findViewById(R.id.notice_list);
		noticList.setOnItemClickListener(this);
		noticList.addHeaderView(topImageLayout);
		
		//初始化列表下拉刷新上拉更多相关
		noticList.setDividerHeight(0);
		noticList.setCacheColorHint(0);
		noticList.setIXListViewListener(NoticeActivity.this);
		noticList.setPullRefreshEnable(true);
		noticList.setPullLoadEnable(true);
		
	}
	
	private void RefreshAllNoticeView(int result, String webresult)
	{
		if (result == 0) {
			try {
				JSONObject jsonstr = new JSONObject(webresult);
				//获取前缀名
				IGlobalSetting setting = ((MainApplication)getApplication()).getGlobalSetting();
				String url = setting.getNoticePrefixUrl();
				JSONArray topimgArr = jsonstr.getJSONArray("tophead");
				JSONObject topimg = (JSONObject) topimgArr.get(0);
				phonehtmlpath = url+topimg.getString("imagespath");//得到的数据传到你要现实的界面
				finalbitmap.display(topImageView, phonehtmlpath);
				
				JSONArray arr = jsonstr.getJSONArray("data");
				for (int j = 0; j < arr.length(); j++) {
					JSONObject temp = (JSONObject) arr.get(j);
					String noticeid = temp.getString("noticeid");
					boolean find = false;
					NoticeBean noticeBean = null;
					for(int m=0; m<noticeBeans.size(); ++m)
					{
						if(noticeid.equals(noticeBeans.get(m).getT()))
						{
							noticeBean = noticeBeans.get(m);
							find = true;
							break;
						}
					}
					if(find == false)
						noticeBean = new NoticeBean();
					noticeBean.setBigtitle(temp.getString("headline"));
					noticeBean.setSmarlltitle(temp.getString("subtitle"));
					noticeBean.setImageads(url + temp.getString("imagespath"));// 图片地址
					noticeBean.setHtmlads(url+ temp.getString("url"));// HTML页面地址
					noticeBean.setMite(temp.getString("createtime"));
					noticeBean.setT(noticeid);
					if(find == false)
						noticeBeans.add(noticeBean);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(NoticeActivity.this, getString(R.string.notice_failed), Toast.LENGTH_SHORT).show();
				Tools.removeLoading(NoticeActivity.this);
				noticList.stopRefresh();
				return;
			}
		}
		else{
			Toast.makeText(NoticeActivity.this, getString(R.string.notice_failed), Toast.LENGTH_SHORT).show();
			Tools.removeLoading(NoticeActivity.this);
			noticList.stopRefresh();
			return;
		}
		//sort by time
		Comparator<NoticeBean> comparator = new Comparator<NoticeBean>(){
			   public int compare(NoticeBean n1, NoticeBean n2) {
				   return n2.getMite().compareTo(n1.getMite());
			   }
		};
		Collections.sort(noticeBeans, comparator);
		
		//refresh notice list
		if(noticeAdapter == null){
			noticeAdapter = new NoticeAdapter(NoticeActivity.this, noticeBeans);
			 noticList.setAdapter(noticeAdapter);
		}else{
			noticeAdapter.notifyDataSetChanged();
		}
	}

	private Handler notichandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle b = msg.getData();   
			int result = b.getInt("connect_result");
			String webresult = b.getString("connect_webresult");
			RefreshAllNoticeView(result, webresult);
			Tools.removeLoading(NoticeActivity.this);
			noticList.stopRefresh();
			if(result == 0 && currentFreshPage == 1)
				UserInfo.saveNoticeCache(NoticeActivity.this, webresult);
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.notice_top_image:
			if(phonehtmlpath != null && phonehtmlpath.length() != 0)
			{
				Intent intent = new Intent(NoticeActivity.this, NoticeIteamActivity.class);
				intent.putExtra("html_url", phonehtmlpath);
				NoticeActivity.this.startActivity(intent);
			}
			break;
		case R.id.notic_top_back: 
			FlashActivity.setNoticeActivity(null);
			finish();
			break;
		case R.id.notic_top_refalsh:
			Tools.addLoading(this);
			NoticDataDealService noticeservice = new NoticDataDealService ();
			noticeservice.freshData(notichandler, this,1);
			currentFreshPage = 1;
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Intent intent = new Intent(NoticeActivity.this, NoticeIteamActivity.class);
		intent.putExtra("html_url",noticeBeans.get(position-2).getHtmlads());
		NoticeActivity.this.startActivity(intent);
	}

	@Override
	public void onRefresh() {//下拉刷新
		Tools.addLoading(this);
		NoticDataDealService noticeservice = new NoticDataDealService ();
		noticeservice.freshData(notichandler, this,1);
		currentFreshPage = 1;
	}

	@Override
	public void onLoadMore() {//上拉显示更多
		Tools.addLoading(this);
		NoticDataDealService noticeservice = new NoticDataDealService ();
		currentFreshPage = (noticeBeans.size()/ITEM_COUNT_PRE_PAGE)+1;
		noticeservice.freshData(notichandler, this , currentFreshPage);
		noticList.stopLoadMore();
	}
	
	public void onNotificationClicked()
	{
		MainApplication app = (MainApplication)getApplicationContext();
		if(app.getNoticeIteamActivity() != null)
		{
			app.getNoticeIteamActivity().finish();
		}
		onRefresh();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode==KeyEvent.KEYCODE_BACK){
			 FlashActivity.setNoticeActivity(null);
			 finish();
			 return true;
		 }
		 return super.onKeyDown(keyCode, event);
	}
}
