package com.jiandan.leanernfcdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jiandan.leanernfcdemo.entity.TagInfo;
import com.jiandan.leanernfcdemo.mesDao.NfcTagDao;

import java.util.List;

/**
 * Created by 李森林 on 2016/2/29.
 */
public class DbInfoActivity extends Activity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbinfo);
        listView= (ListView) findViewById(R.id.listView);
        NfcTagDao nfcTagDao=new NfcTagDao(getApplicationContext());


        String username = getIntent().getStringExtra("username");
        List<TagInfo> tagInfos = nfcTagDao.findAll(username);
        listView.setAdapter(new ArrayAdapter<TagInfo>(this,android.R.layout.simple_list_item_1,tagInfos));
    }





}
