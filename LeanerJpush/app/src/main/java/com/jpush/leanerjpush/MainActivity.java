package com.jpush.leanerjpush;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity {

    private Button btnSet=null;
    private EditText edAlies=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();


    }

    private void init() {
        btnSet= (Button) findViewById(R.id.setAlies);
        edAlies= (EditText) findViewById(R.id.EdText);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JPushInterface.setAlias(getApplicationContext(),edAlies.getText().toString(),null);
                Toast.makeText(MainActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
