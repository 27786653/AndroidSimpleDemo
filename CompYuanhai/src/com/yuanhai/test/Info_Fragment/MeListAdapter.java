package com.yuanhai.test.Info_Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yuanhai.test.R;


/**
 * ViewPage数据适配器
 */
public class MeListAdapter extends BaseAdapter{
	private MeFragment meFragmet;
	
	public MeListAdapter(MeFragment fragment){
		meFragmet = fragment;
	}
	
	@Override
	public int getCount() {
		return meFragmet.getOrderArray().size();
	}

	@Override
	public Object getItem(int position) {
		return meFragmet.getOrderArray().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View coverView, ViewGroup viewGroup) {
		MyOrder order = meFragmet.getOrderArray().get(position);
			
		View  currentView = LayoutInflater.from(meFragmet.getActivity()).inflate(R.layout.me_list_item, null);
		
		TextView titleTextView = (TextView) currentView.findViewById(R.id.item_title);
		titleTextView.setText(order.serviceType);
		
		TextView timeTextView = (TextView) currentView.findViewById(R.id.item_time);
		timeTextView.setText(order.createTime);
		return currentView;
}
}
