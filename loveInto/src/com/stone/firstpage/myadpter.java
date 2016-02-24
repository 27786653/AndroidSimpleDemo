package com.stone.firstpage;

import java.math.BigDecimal;
import java.util.List;

import com.loopj.android.image.SmartImageView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class myadpter extends BaseAdapter {

	
	private List<imageInfo> clist;
	private LayoutInflater infaInflater;
	private Context mContext;
	
	public myadpter(Context context, List<imageInfo> cclist) {
		infaInflater=LayoutInflater.from(context);
		clist=cclist;
		this.mContext=context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return clist.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return clist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	private SmartImageView tv;
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		tv=new SmartImageView(mContext);
		tv.setImageUrl(clist.get(arg0).linkurl);
		tv.setLayoutParams(new GridView.LayoutParams(250, 350));  
	tv.setPadding(8,8,8,8);  
		return tv;
	}

}
