package com.yuanhai.test.Info_Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.component.view.XListView;
import com.component.view.XListView.IXListViewListener;
import com.yhzl.utility.IOrgWebService;
import com.yhzl.utility.UtilityFactory;
import com.yhzl.utility.WebServiceStringResult;
import com.yuanhai.test.R;
import com.yuanhai.test.UserInfo;
import com.yuanhai.util.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * ViewPage的第四页
 */
public class MeFragment extends Fragment implements OnClickListener,OnItemClickListener,IXListViewListener{
	private View rootView;
	private int currentFreshPage = 1;
	private final int ITEM_COUNT_PRE_PAGE = 10;
	private XListView meListView;
	private MeListAdapter meListAdapter;
	private MyHandler wsResultHandler;
	private WebServiceStringResult webServiceStringResult = new WebServiceStringResult(0, "");
	private FragmentActivity activity;
	
	ArrayList<MyOrder> getOrderArray(){
		return myOrderArray;
	}
	
	//all order array
	private ArrayList<MyOrder> myOrderArray = new ArrayList<MyOrder>();
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_refresh:
			onRefresh();
			break;

		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView= inflater.inflate(R.layout.me_fragment, container,false);
		
		rootView.findViewById(R.id.top_refresh).setOnClickListener(this);


		activity = getActivity();
		
		wsResultHandler = new MyHandler(this);
		
		meListAdapter = null;
		meListView = (XListView) rootView.findViewById(R.id.me_list);
		meListView.setOnItemClickListener(this);
		meListView.setDividerHeight(0);
		meListView.setCacheColorHint(0);
		meListView.setIXListViewListener(this);
		meListView.setPullRefreshEnable(true);
		meListView.setPullLoadEnable(true);
		
		//use local cache xml string to create work list view 
		webServiceStringResult.webServiceResult = UserInfo.getMeListCache(activity);
		createMeListView();
		
		Tools.addLoading(activity);
		refreshMeList();
		return rootView;
	}
	
	void refreshMeList(){
		new Thread(){
			@Override
			public void run() {
				super.run();
				IOrgWebService ws = UtilityFactory.CreateOrgWebService();
				webServiceStringResult = ws.refreshOrderList(UserInfo.getUserId(activity), UserInfo.getOrgId(activity), currentFreshPage);		
				Message msg = new Message();
				wsResultHandler.sendMessage(msg);
			}
		}.start();
	}
	public void createMeListView(){
		if(webServiceStringResult.webServiceResult.length() != 0){
			try {
				JSONObject jsonstr = new JSONObject(webServiceStringResult.webServiceResult);
				int stateRes = jsonstr.optInt("state");
				if(stateRes != 1)
				{
					Toast.makeText(activity, getString(R.string.me_list_failed), Toast.LENGTH_SHORT).show();
					return;
				}
				JSONArray arr = (JSONArray) jsonstr.get("data");
				for (int j = 0; j < arr.length(); j++) {
					JSONObject temp = (JSONObject) arr.get(j);
					
					String orderId = temp.getString("orderid");
					boolean find = false;
					MyOrder order = null;
					for(int m=0; m<myOrderArray.size(); ++m)
					{
						if(orderId.equals(myOrderArray.get(m).id)){
							order = myOrderArray.get(m);
							find = true;
							break;
						}
					}
					if(find == false){
						order = new MyOrder();
						myOrderArray.add(order);
					}
					order.id = orderId;
					order.linker = temp.getString("linker");
					order.linkType = temp.getString("linktype");
					order.serviceDate = temp.getString("servicedate");
					order.serviceType = temp.getString("servicetype");
					order.serviceDes = temp.getString("servicedes");
					order.createTime = temp.getString("createtime");
					order.userId = temp.getString("userid");
					order.proCode = temp.getString("procode");
					order.proName = temp.getString("proname");
					order.proModel = temp.getString("promodel");
					order.qty = Integer.parseInt(temp.getString("qty"));
					order.price = Double.parseDouble(temp.getString("price"));
					order.amount = Double.parseDouble(temp.getString("amount"));

				}
			}
			catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(activity, getString(R.string.me_list_failed), Toast.LENGTH_SHORT).show();
				return;
			}
		}
		
		//sort by time
		Comparator<MyOrder> comparator = new Comparator<MyOrder>(){
			   public int compare(MyOrder n1, MyOrder n2) {
				   return n2.createTime.compareTo(n1.createTime);
			   }
		};
		Collections.sort(myOrderArray, comparator);
		
		//refresh order list
		if(meListAdapter == null){
			meListAdapter = new MeListAdapter(this);
			meListView.setAdapter(meListAdapter);
		}else{
			meListAdapter.notifyDataSetChanged();
		}
		if(myOrderArray.size() == 0)
			rootView.findViewById(R.id.no_order).setVisibility(View.VISIBLE);
		else
			rootView.findViewById(R.id.no_order).setVisibility(View.GONE);
	}
	
	private void onMeListRefreshResult(){
		Tools.removeLoading(activity);
		meListView.stopRefresh();
		if(webServiceStringResult.result != 0){
			Toast.makeText(activity, getString(R.string.me_list_failed), Toast.LENGTH_SHORT).show();
			return;
		}
		if(currentFreshPage == 1)
			UserInfo.saveMeListCache(activity, webServiceStringResult.webServiceResult);
		createMeListView();
	}
	
	//main thread message handler
	static class MyHandler extends Handler {
		private MeFragment meFragment;
		MyHandler(MeFragment fragment){
			meFragment = fragment;
		}
		@Override
		public void handleMessage(Message msg) {
			meFragment.onMeListRefreshResult();
		}
	}

	@Override
	public void onRefresh() {
		currentFreshPage = 1;
		Tools.addLoading(activity);
		refreshMeList();
	}

	@Override
	public void onLoadMore() {
		Tools.addLoading(activity);
		currentFreshPage = (myOrderArray.size()/ITEM_COUNT_PRE_PAGE)+1;
		refreshMeList();
		meListView.stopLoadMore();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

}
