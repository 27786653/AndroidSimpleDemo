package com.accounting.myaccounting.locationCheckIn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.accounting.myaccounting.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 李森林 on 2016/4/15.
 */
public class CheckInActivity extends Fragment implements View.OnClickListener {

    private TimelineAdapter timelineAdapter;
    private ListView listview;
    private FragmentManager fm;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_checkin, null);
        fm = getChildFragmentManager();
//        fm.beginTransaction().replace(R.id.content, new ItemNewsFragment()).commit();
        initview();
        return view;
    }

    private void initview() {
        listview= (ListView) view.findViewById(R.id.checkin_list);
        listview.setDividerHeight(0);
        timelineAdapter = new TimelineAdapter(getContext(), getData());
        listview.setAdapter(timelineAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_checkin_back:
                break;
        }


    }


    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "这是第1行测试数据");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "这是第2行测试数据");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "这是第3行测试数据");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "这是第4行测试数据");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("title", "这是第4行测试数据");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("title", "这是第4行测试数据");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("title", "这是第4行测试数据");
        list.add(map);
        return list;
    }


}
