package com.mh.example.polygon;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import com.mh.widget.HexagonView;


/**
 * @author Mhui55555
 * 演示自定义六边形点击事件监听
 */
public class MainActivity extends Activity implements HexagonView.OnHexagonViewClickListener{
    private final String TAG="MainActivity";
    HexagonView hw1,hw2,hw3,hw4,hw5,hw6,hw7; //六边形控件实例

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        init();
    }


    //---------------------------------------------------------

    /**
     * 事件监听
     */
    public void onClick(View view){
        Log.d(TAG,"onClick()");
        switch (view.getId()){
            case R.id.center:
                Toast.makeText(this,"解锁被点击",Toast.LENGTH_SHORT).show();
                break;
            case R.id.hexagon_hello:
                Toast.makeText(this,"物业投诉",Toast.LENGTH_SHORT).show();
                break;
            case R.id.hexagon_car:
                Toast.makeText(this,"成员信息",Toast.LENGTH_SHORT).show();
                break;
            case R.id.three:
                Toast.makeText(this,"物业缴费",Toast.LENGTH_SHORT).show();
                break;
            case R.id.four:
                Toast.makeText(this,"通知公告",Toast.LENGTH_SHORT).show();
                break;
            case R.id.five:
                Toast.makeText(this,"物业保修",Toast.LENGTH_SHORT).show();
                break;
            case R.id.six:
                Toast.makeText(this,"物业点赞",Toast.LENGTH_SHORT).show();
                break;
        }
    }


    //---------------------------------------------------------

    /**
     * init
     */
    private void init(){
        Log.d(TAG,"init()");
        hw1=(HexagonView)this.findViewById(R.id.center);
        hw2=(HexagonView)this.findViewById(R.id.hexagon_hello);
        hw3=(HexagonView)this.findViewById(R.id.hexagon_car);
        hw4=(HexagonView)this.findViewById(R.id.three);
        hw5=(HexagonView)this.findViewById(R.id.four);
        hw6=(HexagonView)this.findViewById(R.id.five);
        hw7=(HexagonView)this.findViewById(R.id.six);



        hw1.setOnHexagonClickListener(this);
        hw2.setOnHexagonClickListener(this);
        hw3.setOnHexagonClickListener(this);
        hw4.setOnHexagonClickListener(this);
        hw5.setOnHexagonClickListener(this);
        hw6.setOnHexagonClickListener(this);
        hw7.setOnHexagonClickListener(this);

    }
}
