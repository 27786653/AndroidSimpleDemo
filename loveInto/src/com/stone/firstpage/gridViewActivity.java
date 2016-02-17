package com.stone.firstpage;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.Toast;

public class gridViewActivity extends Activity {

private 	List<imageInfo> clist=null;
	private GridView gv1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gridview);
		gv1=(GridView) findViewById(R.id.gridView1);
new myasyncTask().execute(httpconnection.nowpages+"");	
	}
	public void changeImage(View v){
		Toast.makeText(getApplicationContext(), "稍等哦，老婆大人~", 0).show();
		httpconnection.nowpages++;
		if(httpconnection.nowpages>httpconnection.totalpages)httpconnection.nowpages=1;
		new myasyncTask().execute(httpconnection.nowpages+"");	
	}
	public void closeactivity(View v){
		this.finish();
	}
	class myasyncTask extends AsyncTask<String,Void, List<imageInfo>>{

		@Override
		protected List<imageInfo> doInBackground(String... arg0) {
			clist=new ArrayList<imageInfo>();
			try {
				String str = new httpconnection().getjson(Integer.parseInt(arg0[0]));
				JSONObject json=new JSONObject(str);
				httpconnection.totalpages=json.getInt("pages");
				JSONArray o = (JSONArray) json.get("pic");
				for (int i = 0; i < o.length(); i++) {
					JSONObject jso = o.getJSONObject(i);
					imageInfo c=new imageInfo();
					c.id= jso.getInt("id");
					c.name= jso.optString("name");
					c.linkurl= jso.optString("linkurl");
					clist.add(c);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return clist;
		}
		
		@Override
		protected void onPostExecute(List<imageInfo> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			gv1.setAdapter(new myadpter(getApplicationContext(), clist));
		}
		
	}		
}
	
