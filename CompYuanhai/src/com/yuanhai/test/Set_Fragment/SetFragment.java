package com.yuanhai.test.Set_Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.yuanhai.test.R;
/**
 * ViewPage的第五页
 */
public class SetFragment extends Fragment implements OnClickListener{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.set_fragment, container,false);
		rootView.findViewById(R.id.exit).setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id)
		{
		case R.id.exit:
			android.os.Process.killProcess(android.os.Process.myPid());

			break;
		}
	}
}
