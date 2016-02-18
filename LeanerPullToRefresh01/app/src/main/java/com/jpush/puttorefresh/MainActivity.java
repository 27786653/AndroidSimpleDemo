package com.jpush.puttorefresh;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


/**
 * 使用原生系统提供的控件实现下拉刷新
 * swipeRefreshLayout
     1.设计布局
     2.该控件添加OnRefreshListen事件
     3.在onRefresh方法中实现下拉刷新时间
 */
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{


    private  SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    mTextView= (TextView) findViewById(R.id.random);
    }
    //下拉刷新调用方法
    @Override
    public void onRefresh() {
      //下拉刷新动画打开
        mSwipeRefreshLayout.setRefreshing(true);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //3秒后停止显示属性过程动画
                mSwipeRefreshLayout.setRefreshing(false);
//                产生一个（1-100）之间的随机数
                int i=(int)(Math.random()*100+1);
                mTextView.setText(i+"");
            }
        },3000);
    }
}
