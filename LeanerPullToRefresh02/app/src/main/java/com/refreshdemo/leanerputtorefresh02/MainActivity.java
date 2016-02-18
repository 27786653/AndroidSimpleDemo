package com.refreshdemo.leanerputtorefresh02;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * 使用第三方下拉刷新控件（https://github.com/chrisbanes/android-pulltorefresh）
 * 支持下拉刷新和上拉刷新
 */
public class MainActivity extends AppCompatActivity {

    PullToRefreshListView mPullToRefreshListView;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);  //打开两种刷新方式
       //如果只支持下拉刷新便调用该方法OnRefreshListener
//        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
//           //下拉触发刷新事件
//           @Override
//            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//               findViewById(R.id.titletext).setVisibility(View.INVISIBLE);   //提示文字隐藏
//               new GetDataTask().execute();    //触发事件
//            }
//        });
        //如果支持上下拉刷新便调用该方法OnRefreshListener2
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                findViewById(R.id.titletext).setVisibility(View.INVISIBLE);   //提示文字隐藏
                Toast.makeText(MainActivity.this, "下拉刷新！", Toast.LENGTH_SHORT).show();
                new GetDataTask().execute();    //触发事件
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                findViewById(R.id.titletext).setVisibility(View.INVISIBLE);   //提示文字隐藏
                Toast.makeText(MainActivity.this, "上拉刷新！", Toast.LENGTH_SHORT).show();
                new GetDataTask().execute();    //触发事件
            }
        });
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                new ArrayList<String>());
        mPullToRefreshListView.setAdapter(arrayAdapter);
    }


    /**
     * 异步任务
     */
    class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mPullToRefreshListView.onRefreshComplete();  //通知ListView刷新成功

            arrayAdapter.addAll(new String[]{"测试1","测试2","测试3"});
            Toast.makeText(MainActivity.this, "刷新成功！", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
        }
    }

}
