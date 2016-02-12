package com.example.usingfragmentdemo;

import com.example.fragment.ListFragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;


/**
 * 在主视图是继承activity的时候，元素指定的frament必须是android.app.fragment下的（换包或者让主视图继承FragmentActivity）
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		Fragment frag =  getFragmentManager().findFragmentById(R.id.fragement_List);
//		if(frag==null){
//			frag=new ListFragment();
//			getFragmentManager()
//							.beginTransaction().add(R.id.fragement_List, frag)
//							.commit();
//		}
	}


}
