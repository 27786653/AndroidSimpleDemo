package com.yuanhai.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yhzl.utility.IOrgWebService;
import com.yhzl.utility.UtilityFactory;
import com.yhzl.utility.WebServiceStringResult;
import com.yuanhai.util.Tools;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.security.MessageDigest;

/**
 * App登录页面（初次运行，由FlashActivity跳转至此）
 */
public class LoginActivity extends Activity implements OnClickListener{
	private EditText edt_phone,edt_mima;
	private Toast toast;
	
	private TelephonyManager phoneMgr;
	private String phone,password;
	private WebServiceStringResult result;
	private String json;
	private int stateRes;
	private int stateStr;

	private static final String AddressnameSpace = "http://tempuri.org/";//命名空间
	private static final String Addressurl = "http://pms.yuhi.com.cn:1688/WebServices/AppWebServices.asmx";
	private static final String Addressmethod = "Login";
	private static final String AddresssoapAction = "http://pms.yuhi.com.cn:1688/WebServices/AppWebServices.asmx/Login";
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F' };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		initView();
	}
	
	private void initView() {
		edt_phone = (EditText)findViewById(R.id.other_edt1);
		edt_mima = (EditText)findViewById(R.id.other_edt2);
		
		findViewById(R.id.other_btn).setOnClickListener(this);
		findViewById(R.id.find_user_id).setOnClickListener(this);
		String loginName = UserInfo.getLoginName(this);
		if(loginName.length() == 0){
			//get phone
			phoneMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
			phone=phoneMgr.getLine1Number();
			edt_phone.setText(phone);
		}
		else
		{
			edt_phone.setText(loginName);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.other_btn:
			dealBtn();

			break;
		case R.id.find_user_id://fond password
			break;
		}
		
	}
	//登录按钮触发事件
	private void dealBtn() {
		//get test's password
		phone = edt_phone.getText().toString();
		password = edt_mima.getText().toString();
		if("".equals(password))
		{
			showToast(getResources().getString(R.string.pass_no_erio));
			return;
		}
//		if(phone.length()<8||phone.length()>11)
//		{
//			showToast("账号输入格式不正确");
//			return;
//		}
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.rootLayout);
		Tools.addLoading(LoginActivity.this, layout);
		new HttpThread().start();

		//new MyThread().start();

	}


	private Handler viewHandler  = new Handler(){
		@Override

		public void handleMessage(Message msg) {

			if(result.result == 0){
				try {
					json = result.webServiceResult;
					JSONObject jsonstr = new JSONObject(json);
					stateRes = jsonstr.optInt("state");
					if(stateRes == 1){
						String userId = jsonstr.optString("userid");
						String orgId = jsonstr.optString("orgid");
						UserInfo.saveInfo(LoginActivity.this, phone, password,userId,orgId);
					}
				}
				//JSONException
				catch (Exception e) {
					//e.printStackTrace();
					e.getMessage();
				}

				if(stateRes==1){
					showToast(getResources().getString(R.string.user_password_go));
					intentToView();
				}else if(stateRes==0){
					showToast(getResources().getString(R.string.user_password_error));
				}else if(stateRes==-1){
					showToast(getResources().getString(R.string.pass_user_exception));
				}
			}else{
				showToast(getResources().getString(R.string.pass_net_exception));
			}
			RelativeLayout layout = (RelativeLayout)findViewById(R.id.rootLayout);
			Tools.removeLoading(layout);
			super.handleMessage(msg);
			System.out.println("发送信息成功");
		}
	};
	private class HttpThread extends Thread{

		@Override
		public void run() {
			super.run();
			Message msg = new Message();
			IOrgWebService ws = UtilityFactory.CreateOrgWebService();
			result = ws.login(phone, password);
			viewHandler.sendMessage(msg);
		}


	}
	//登录成功，调用此方法跳转ShouShiActivity
	private void intentToView(){
		this.finish();
		Intent intent = new Intent();
        intent.setClass(this, ShouShiActivity.class);
        startActivity(intent);
	}
	private void showToast(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		} else {
			toast.setText(message);
		}
		toast.show();
	}


	private Handler Myhandler = new Handler() {
		public void handleMessage(Message msg) {
			if(stateStr==1)
			{
				UserInfo.saveInfo(LoginActivity.this, phone, password, "", "");

				showToast(getResources().getString(R.string.user_password_go));
				//showToast("登录成功");
				//Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_LONG).show();
				intentToView();
			}
			else
			{
				//System.out.println("连接失败");
				Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
				showToast("账号或密码错误");
				//RelativeLayout layout = (RelativeLayout)findViewById(R.id.rootLayout);
				//Tools.removeLoading(layout);
				//toast.show();

			}
			//super.sendMessage(msg);
		}
	};


	private class  MyThread extends Thread
	{
		public void run() {
			Looper.prepare();
			String data = "";
			//edt_phone,edt_mima
			SoapObject soapObject = new SoapObject(AddressnameSpace, "Login");
			StringBuffer sb = new StringBuffer();
			byte[] m=new byte[]{};
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				md5.update(password.getBytes());
				m = md5.digest();//加密
				for (int i = 0; i < m.length; i++) {
					sb.append(HEX_DIGITS[(m[i] & 0xf0) >>> 4]);
					sb.append(HEX_DIGITS[m[i] & 0x0f]);
				}
			}catch (Exception e){ e.getMessage(); }
			soapObject.addProperty("LoginName", phone);
			soapObject.addProperty("PassWord", sb.toString());
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.bodyOut = soapObject;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(soapObject);
			HttpTransportSE httpTransportSE = new HttpTransportSE(Addressurl);
			System.out.println("服务设置完毕,准备开启服务");
			try {
				httpTransportSE.call("http://tempuri.org/Login", envelope);
				System.out.println("调用WebService服务成功");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("调用WebService服务失败"+e.getMessage());
			}
			//获得服务返回的数据,并且开始解析
			// SoapObject object = (SoapObject) envelope.bodyIn;
			SoapObject object = (SoapObject) envelope.bodyIn;
			System.out.println("获得服务数据");
			data = object.getProperty(0).toString();
			try {
				JSONObject jsonObject = new JSONObject(data);
				//List<JSONObject> objects = new ArrayList<>();
				//objects.add(jsonObject.getJSONObject("data"));
				stateStr=(Integer)jsonObject.get("state");
				//System.out.println(ggzt);
				//    txt_result.setText("结果显示：\n" + ggzt);
			}catch (Exception e){e.printStackTrace();};
			Message msg =new Message();
			Myhandler.handleMessage(msg);
			Looper.loop();
			//viewHandler.sendMessage(msg);
			//System.out.println(stateStr+":发送完毕,textview显示天气信息");
		}

	}



}
