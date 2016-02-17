package com.jpush.leanerjpush;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by 李森林 on 2016/2/17.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

    }
}
