package com.example.gsonorfastjson.Util;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gsonorfastjson.entity.imoocClassInfo;

/**
 * 通过fast-Json解析Json数据
 * 用法和Gson相似
 *         1.定义实体类
 *         2.网络获取json数据的字符串
 *         3.通过json.parseObject转换为实例类对象
 */
public class FastJsonFormat {

    private final Context context;
    private final RequestQueue mQueue;

    public FastJsonFormat(Context context, RequestQueue mQueue){
    this.context=context;
        this.mQueue=mQueue;

    }


    private imoocClassInfo info;    //封装json数据的Object


    /**
     * 访问网络得到json数据
     */
    public  void getData(final TextView tvContent, String Url) {
        StringRequest sr=new StringRequest(Request.Method.GET,Url ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                                Toast.makeText(MainActivity.this, "链接成功，回调是：" + s, Toast.LENGTH_SHORT).show();
                        parseJson(s);
                        tvContent.setText(info.getMsg());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("IntentError", "onErrorResponse: 网络访问错误" );
            }

        });
        mQueue.add(sr);
    }
    /**
     * 解析json成对象
     */
    private  void parseJson(String result){
        info= JSON.parseObject(result,imoocClassInfo.class);
        Log.d("imooc", "parseJson: " + info);
    }


}
