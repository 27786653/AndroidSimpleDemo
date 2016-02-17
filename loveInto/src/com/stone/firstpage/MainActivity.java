package com.stone.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

	}
	

	
	public void clickTask(View v){
		v.setVisibility(View.INVISIBLE);
		getSupportFragmentManager().beginTransaction()
		.add(R.id.container, new Template1Fragment()).commit();
	}
	public void testtasktextview(View v){
		Toast.makeText(getApplicationContext(), "还以为永远都不会有人点我尼~", 0).show();
		Intent mIntent=new Intent(getApplicationContext(),gridViewActivity.class);
		startActivity(mIntent);
	}
}
