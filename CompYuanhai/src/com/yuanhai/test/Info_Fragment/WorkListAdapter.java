package com.yuanhai.test.Info_Fragment;

import com.net.tsz.afinal.FinalBitmap;
import com.yuanhai.test.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WorkListAdapter extends BaseAdapter{
	private WorkListActivity workListActivity;
	private FinalBitmap finalbitmap;
	
	public WorkListAdapter(WorkListActivity activity){
		workListActivity = activity;
		finalbitmap = FinalBitmap.create(activity);
	}
	
	@Override
	public int getCount() {
		return workListActivity.getWorkReportArray().size()+1;
	}

	@Override
	public Object getItem(int position) {
		return workListActivity.getWorkReportArray().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View coverView, ViewGroup viewGroup) {
		if(position == workListActivity.getWorkReportArray().size() )
		{
			View  currentView = LayoutInflater.from(workListActivity).inflate(R.layout.work_list_add_item, null);
			return currentView;
		}
		else
		{
			WorkReport report = workListActivity.getWorkReportArray().get(position);
			
			View  currentView = LayoutInflater.from(workListActivity).inflate(R.layout.work_list_item, null);
			
			ImageView imageView = (ImageView) currentView.findViewById(R.id.item_image);
			finalbitmap.display(imageView, report.filepath);
			
			TextView titleTextView = (TextView) currentView.findViewById(R.id.item_title);
			titleTextView.setText(report.customername);
			
			TextView timeTextView = (TextView) currentView.findViewById(R.id.item_time);
			timeTextView.setText(report.time);
			return currentView;
		}
	}

}
