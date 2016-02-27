package com.yuanhai.test.Info_Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.yuanhai.appShows.MainApplication;
import com.yuanhai.test.More_Fragment.MoreIteamActivity;
import com.yuanhai.test.R;
import com.yuanhai.test.UserInfo;

/**
 * ViewPage的第一页
 */
public class MainFragment extends Fragment implements OnClickListener
{
private Context activity;
private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = getActivity();
		rootView = inflater.inflate(R.layout.main_fragment, container,
				false);
		initialize(rootView);
		return rootView;
	}

	private void initialize(View rootView) {
		rootView.findViewById(R.id.notice_layout).setOnClickListener(this);
		rootView.findViewById(R.id.image_notic).setOnClickListener(this);
		
		rootView.findViewById(R.id.working_layout).setOnClickListener(this);
		rootView.findViewById(R.id.image_working).setOnClickListener(this);
		
		rootView.findViewById(R.id.adv_layout).setOnClickListener(this);
		rootView.findViewById(R.id.image_adv).setOnClickListener(this);
		
		rootView.findViewById(R.id.video_layout).setOnClickListener(this);
		rootView.findViewById(R.id.image_video).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.notice_layout:
			case R.id.image_notic:
				intent = new Intent(activity, NoticeActivity.class);
				break;
			case R.id.working_layout:
			case R.id.image_working:
				intent = new Intent(activity, WorkListActivity.class);
				break;
			case R.id.adv_layout:
			case R.id.image_adv:
				//intent = new Intent(activity, AdActivity.class);
				intent = new Intent(activity, MoreIteamActivity.class);
				MainApplication app = (MainApplication)getActivity().getApplication();
				String urlPrefix = app.getGlobalSetting().getMorePrefixUrl();
				intent.putExtra("url", urlPrefix + "3&orgid=" + UserInfo.getOrgId(activity));
				intent.putExtra("name", activity.getString(R.string.adv_string));
				intent.putExtra("hasOrder", "0");
				break;
			case R.id.video_layout:
			case R.id.image_video:
				intent = new Intent(activity, HKCameraChioesListActivity.class);
				break;
			default:
				break;
		}
		if(intent != null){
			activity.startActivity(intent);
		}
	}
}
