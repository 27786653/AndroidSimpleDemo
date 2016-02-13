package com.example.gsonorfastjson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.gsonorfastjson.Util.FastJsonFormat;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity {

    private  String Url="http://www.imooc.com/api/teacher?type=4&num=30";   //慕课网的课程信息api

    private Button btnSR;
    private TextView tvContent;
//    private imoocClassInfo info;    //封装json数据的Object
    private RequestQueue mQueue;    //网络任务执行队列


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueue= Volley.newRequestQueue(getApplicationContext());   //初始化队列
        init();

    }

    /**
     * 初始化控件
     * 绑定按钮点击事件
     */
    private void init() {
//        final GsonFormat gf=new GsonFormat(getApplicationContext(),mQueue);
        final FastJsonFormat gf=new FastJsonFormat(getApplicationContext(),mQueue);
        btnSR= (Button) findViewById(R.id.btn_sr);
        tvContent= (TextView) findViewById(R.id.tv_content);
        btnSR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gf.getData(tvContent,Url);

            }
        });

    }





}
