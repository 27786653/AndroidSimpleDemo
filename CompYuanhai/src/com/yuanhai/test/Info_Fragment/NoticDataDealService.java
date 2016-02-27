package com.yuanhai.test.Info_Fragment;

import java.util.List;

import com.yhzl.utility.IOrgWebService;
import com.yhzl.utility.IWebService;
import com.yhzl.utility.UtilityFactory;
import com.yhzl.utility.WebServiceStringResult;
import com.yuanhai.test.UserInfo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class NoticDataDealService {
	private List<NoticeBean> noticeBeans;
	private int pages;
	private Handler hand;
	private Context context;
	public void freshData(Handler handler,Context c,int page){
		hand = handler;
		pages = page;
		context = c;
	new Thread(){
		@Override
		public void run() {
			super.run();
			IOrgWebService ws = UtilityFactory.CreateOrgWebService();
			WebServiceStringResult result = ws.refreshNotice(UserInfo.getUserId(context), UserInfo.getOrgId(context), pages);
			Message message= new Message();
			Bundle b = new Bundle();
			b.putInt("connect_result", result.result);
			b.putString("connect_webresult", result.webServiceResult);
			message.setData(b);  
			hand.sendMessage(message);

		}
		
	}.start();
	}
	
	public List<NoticeBean> getData(){
		return noticeBeans;
	}
}
