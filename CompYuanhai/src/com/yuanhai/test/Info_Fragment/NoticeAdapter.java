package com.yuanhai.test.Info_Fragment;

import java.util.List;

import com.net.tsz.afinal.FinalBitmap;
import com.yuanhai.test.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NoticeAdapter extends BaseAdapter{
	private List<NoticeBean> noticeList;
	private LayoutInflater inflater;
	private Context context;
	private FinalBitmap finalbitmap;
	public NoticeAdapter(Context c, List<NoticeBean> noticeBeans){
		context = c;
		inflater = LayoutInflater.from(c);
		noticeList = noticeBeans;
		finalbitmap = FinalBitmap.create(context);
//		notifyDataSetChanged();
	}
	//此时返回为一
	@Override
	public int getCount() {
		return noticeList.size();
	}

	@Override
	public Object getItem(int position) {
		return noticeList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int posion, View coverView, ViewGroup arg2) {
		ViewHolder holder;
		View  currentView = inflater.inflate(R.layout.notice_list_item, null);
//		if(coverView == null){
			holder = new ViewHolder();
//			currentView = inflater.inflate(R.layout.notice_list_item, null); 
			holder.titleImage = (ImageView) currentView.findViewById(R.id.notice_iteam_image);
			holder.bigTitleView = (TextView) currentView.findViewById(R.id.notice_iteam_bigtitle);
			holder.smarllTitle = (TextView) currentView.findViewById(R.id.notice_iteam_smarlltitle);
			holder.timeText = (TextView) currentView.findViewById(R.id.notice_iteam_time);
//			currentView.setTag(holder);
//		}else{
//			holder = (ViewHolder) coverView.getTag();
//		}
			
		holder.bigTitleView.setText( noticeList.get(posion).getBigtitle());
		holder.smarllTitle.setText( noticeList.get(posion).getSmarlltitle());
		holder.timeText.setText( noticeList.get(posion).getMite());
		finalbitmap.display(holder.titleImage, noticeList.get(posion).getImageads());
		return currentView;
	}
	
	private class ViewHolder{
		ImageView titleImage;
		TextView bigTitleView , smarllTitle,timeText;
	}
	
}
