package com.example.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.usingfragmentdemo.R;
import com.example.util.Shakspeare;

public class ListFragment extends android.app.ListFragment //列表fragment
{
	int mCurCheckPosition = 0;//当前选择
	int mShowCheckPosition = -1; //显示选择的

	// 在activity创建的时候调用（绑定的参数）
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, Shakspeare.TitleList));
		if (savedInstanceState != null) {
			mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
			mShowCheckPosition = savedInstanceState.getInt("showChoice", -1);
		}
		// 改变listview的选中显示模式
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE); 
		showDetails(mCurCheckPosition);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		showDetails(position);
	}
	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("curChoice", mCurCheckPosition);   
        outState.putInt("showChoice", mShowCheckPosition); 
	}
	
	
//显示详情
	private void showDetails(int position) {
		mCurCheckPosition=position;
		getListView().setItemChecked(position, true);
		
		if(mShowCheckPosition!=mCurCheckPosition){
			detailsFragment df=detailsFragment.newInstance(position);
			android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.fragment_layout_deilte, df);
		//	ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
			mShowCheckPosition=position;
		}
		
		
	}
	
	
	
	
	
}
