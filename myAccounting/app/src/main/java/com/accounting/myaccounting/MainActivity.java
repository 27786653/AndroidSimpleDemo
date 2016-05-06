package com.accounting.myaccounting;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.accounting.myaccounting.fragment.Rmoney;
import com.accounting.myaccounting.locationCheckIn.CheckInActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(this);
        fm = getSupportFragmentManager();
        //默认显示 资讯  标签页
        fm.beginTransaction().replace(R.id.content, new Rmoney()).commit();
    }



    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.button:
            fm.beginTransaction().replace(R.id.content, new CheckInActivity()).commit();
            break;
    }
    }
}
