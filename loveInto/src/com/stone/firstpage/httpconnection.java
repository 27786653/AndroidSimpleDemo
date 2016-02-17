package com.stone.firstpage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;

import android.os.AsyncTask;

public class httpconnection {

	public static int nowpages=1;
	public static int totalpages=0;
	//http://api.tietuku.com/v2/api/getpiclist?key=npiXm5thlZiWm8aSmWaYmmGYnmNomshobZiXaphjmWuZZcudkmSXmcObmGSUaJY=&aid=1196700&p=1
	//1196700
	public String getjson(int pages) throws Exception{
		String Url="http://api.tietuku.com/v2/api/getpiclist"
				+ "?key=npiXm5thlZiWm8aSmWaYmmGYnmNomshobZiXaphjmWuZZcudkmSXmcObmGSUaJY="
				+ "&aid=1196584&p="+pages;
		URL u=new URL(Url);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		BufferedReader br=new  BufferedReader(new InputStreamReader(con.getInputStream()));
		String line=null;
		StringBuffer sb=new StringBuffer();
		while((line=br.readLine())!=null){
			sb.append(line);
		}
		br.close();
		return sb.toString();
		
	}
	
	
}
