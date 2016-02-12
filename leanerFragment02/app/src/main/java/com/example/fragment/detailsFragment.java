package com.example.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.util.Shakspeare;

public class detailsFragment  extends Fragment{
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(container==null){
			return null;
		}
		ScrollView scroller=new ScrollView(getActivity());
		scroller.setBackgroundColor(0X000000);
		TextView	tv=new TextView(getActivity());


		int padding = (int) TypedValue.applyDimension(   
                TypedValue.COMPLEX_UNIT_DIP, 4, getActivity().getResources()   
                        .getDisplayMetrics());   
        tv.setPadding(padding, padding, padding, padding); 

		int position=getArguments().getInt("position",0);
        tv.setText(Shakspeare.TitleList[position]);
		scroller.addView(tv);
		
		
		return scroller;
	}


	//负责实例化本类的方法-（方便传递参数）
	public static detailsFragment newInstance(int position) {
		detailsFragment df=new detailsFragment();
		Bundle args=new Bundle();
		args.putInt("position", position);
		df.setArguments(args);
		return df;
	}
	
	
	
}
