package com.yuanhai.test.Info_Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.net.tsz.afinal.FinalBitmap;
import com.yhzl.utility.IOrgWebService;
import com.yhzl.utility.UtilityFactory;
import com.yhzl.utility.WebServiceStringResult;
import com.yuanhai.test.R;
import com.yuanhai.test.UserInfo;
import com.yuanhai.util.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WorkingActivity extends Activity implements OnClickListener{
	private EditText Customername, Pubdate, Seetype, Talkcontetn, Customerisr,
			Marketisr;
	private String name, time, visit, talkcontent, costomerisr, marketisr;
	private ImageView nameImage;
	private final Calendar cd = Calendar.getInstance();
	private String imgPath;
	private Bitmap bitmapAttachment;
	private Dialog getPicDialog;
	private Toast toast;
	private WebServiceStringResult webServiceStringResult;
	private FinalBitmap finalbitmap;
	//baidu lbs client
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	private String locationAddr = "";
	private Uri uriPhoto;
	
	private void showToast(CharSequence message) {
		if (null == toast) {
			toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		} else {
			toast.setText(message);
		}

		toast.show();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.working_activity);

		findViewById(R.id.working_top_back).setOnClickListener(this);
		findViewById(R.id.working_image_select).setOnClickListener(this);
		findViewById(R.id.working_button).setOnClickListener(this);

		initView();
		
		processIntent();
		
		if(getIntent().getBooleanExtra("IsAddWorkReport", true))
			requestLocation();
	}

	/**
	 * 提交定位信息（百度）
	 */
	private void requestLocation()
	{
		//create baidu lbs client and register listener
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener( myListener );
			    
		LocationClientOption option = new LocationClientOption();
	    option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
	    option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
	    option.setIsNeedAddress(true);//返回的定位结果包含地址信息
	    mLocationClient.setLocOption(option);
	    
	    mLocationClient.start();
	}



	public class MyLocationListener implements BDLocationListener {
	    @Override
	   public void onReceiveLocation(BDLocation location) {
	    	TextView tv = (TextView) findViewById(R.id.working_location_text);
			if (location == null) {
				tv.setText(getString(R.string.my_location_failed));
				return;
			}
			locationAddr = location.getAddrStr();
			if(locationAddr == null || locationAddr.length() == 0)
				tv.setText(getString(R.string.my_location_failed));
			else
				tv.setText(getString(R.string.my_location) + locationAddr);
		}

		@Override
		public void onReceivePoi(BDLocation location) {
						
		}
	}
	private void processIntent()
	{
		Intent intent = getIntent();
		if(!intent.getBooleanExtra("IsAddWorkReport", true)){
			finalbitmap = FinalBitmap.create(this);
			
			findViewById(R.id.working_button).setVisibility(View.GONE);
			findViewById(R.id.working_image_select).setVisibility(View.GONE);
			
			Customername.setText(intent.getStringExtra("customername"));
			Customername.setEnabled(false);
			Customername.setFocusable(false);
			
			Pubdate.setText(intent.getStringExtra("pubdate"));
			Pubdate.setEnabled(false);
			Pubdate.setFocusable(false);
			
			Seetype.setText(intent.getStringExtra("seetype"));
			Seetype.setEnabled(false);
			Seetype.setFocusable(false);
			
			Talkcontetn.setText(intent.getStringExtra("talkcontent"));
			Talkcontetn.setEnabled(false);
			Talkcontetn.setFocusable(false);
			
			Customerisr.setText(intent.getStringExtra("customerisr"));
			Customerisr.setEnabled(false);
			Customerisr.setFocusable(false);
			
			Marketisr.setText(intent.getStringExtra("marketisr"));
			Marketisr.setEnabled(false);
			Marketisr.setFocusable(false);
			
			ImageView imageView = (ImageView) findViewById(R.id.imageshow);
			finalbitmap.display(imageView, intent.getStringExtra("filepath"));
			
			locationAddr = intent.getStringExtra("workPos");
			TextView tv = (TextView) findViewById(R.id.working_location_text);
			tv.setText(getString(R.string.my_location) + locationAddr);
		}
	}

	private boolean WebServiceStringResultIsOk(WebServiceStringResult result)
	{
		if (result.result != 0) 
			return false;
		
		try {
			JSONObject jsonObj = new JSONObject(result.webServiceResult);
			if(jsonObj.optInt("state") != 1)
				return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	private Handler viewHandler  = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Tools.removeLoading(WorkingActivity.this);
			findViewById(R.id.working_button).setEnabled(true);
			findViewById(R.id.working_top_back).setEnabled(true);
			findViewById(R.id.working_image_select).setEnabled(true);
			if(WebServiceStringResultIsOk(webServiceStringResult))
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(WorkingActivity.this); 
				builder.setMessage(getString(R.string.commit_success)) ;
				builder.setPositiveButton(getString(R.string.ok),  
		                new DialogInterface.OnClickListener() {  
		                    public void onClick(DialogInterface dialog, int whichButton) {  
		                    	dialog.dismiss();
		                    	finish();
		                    }  
		                });
				AlertDialog alert = builder.create();
				alert.show();
			}
			else
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(WorkingActivity.this); 
				builder.setMessage(getString(R.string.commit_failed)) ;
				builder.setPositiveButton(getString(R.string.ok),  
		                new DialogInterface.OnClickListener() {  
		                    public void onClick(DialogInterface dialog, int whichButton) {  
		                    	dialog.dismiss();
		                    }  
		                });
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
	};
	
	private class HttpThread extends Thread{

		@Override
		public void run() {
			super.run();
			Message msg = new Message();
			try {
				ByteArrayOutputStream o = new ByteArrayOutputStream();
				bitmapAttachment.compress(Bitmap.CompressFormat.PNG, 100, o);

				String userID = UserInfo.getUserId(WorkingActivity.this);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("userid", userID);
				jsonObject.put("title", "工作汇报");
				jsonObject.put("customername", name);
				jsonObject.put("content", "content");
				jsonObject.put("seetype", visit);
				jsonObject.put("talkcontent", talkcontent);
				jsonObject.put("customerisr", costomerisr);
				jsonObject.put("marketisr", marketisr);
				jsonObject.put("pubdate", time);
				jsonObject.put("workpos", locationAddr);
				jsonObject.put("orgid", UserInfo.getOrgId(WorkingActivity.this));
				
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String timeString = sdf.format(c.getTime());
				jsonObject.put("filename", userID + timeString + ".png");
				
				JSONObject jsonRootObject = new JSONObject();
				jsonRootObject.put("object", jsonObject);
				IOrgWebService ws = UtilityFactory.CreateOrgWebService();
				webServiceStringResult = ws.addWrokReport(jsonRootObject.toString(), o.toByteArray());
			} catch (JSONException e) {
				webServiceStringResult.result = 1;
				e.printStackTrace();
			}
			viewHandler.sendMessage(msg);
		}
	}

	private void addWorkReport() {
		// 获取输入的东西
		name = Customername.getText().toString();
		if(name.length() == 0)
		{
			showToast(getString(R.string.name_is_null));
			return;
		}
		// 时间
		
		time = Pubdate.getHint().toString();
		// 拜访方式
		visit = Seetype.getText().toString();
		if(visit.length() == 0)
		{
			showToast(getString(R.string.Visit_is_null));
			return;
		}
		
		// 洽谈内容
		talkcontent = Talkcontetn.getText().toString();
		if(talkcontent.length() == 0)
		{
			showToast(getString(R.string.talk_is_null));
			return;
		}
		
		// 客户情报
		costomerisr = Customerisr.getText().toString();
		if(costomerisr.length() == 0)
		{
			showToast(getString(R.string.costomerisr_is_null));
			return;
		}
		
		// 市場情报
		
		marketisr = Marketisr.getText().toString();
		if(marketisr.length() == 0)
		{
			showToast(getString(R.string.mark_is_null));
			return;
		}
		
		if(bitmapAttachment == null)
		{
			showToast(getString(R.string.image_is_null));
			return;
		}
		Tools.addLoading(this);
		findViewById(R.id.working_button).setEnabled(false);
		findViewById(R.id.working_top_back).setEnabled(false);
		findViewById(R.id.working_image_select).setEnabled(false);
		
		new HttpThread().start();
	}

	private void initView() {

		Customername = (EditText) findViewById(R.id.working_name_edt);
		Customername.addTextChangedListener(mTextWatcher);
		nameImage = (ImageView) findViewById(R.id.name_image);
		Pubdate = (EditText) findViewById(R.id.working_time_edt);
		Seetype = (EditText) findViewById(R.id.working_visit_edt);
		Talkcontetn = (EditText) findViewById(R.id.working_talk_edt);
		Customerisr = (EditText) findViewById(R.id.working_com_edt);
		Marketisr = (EditText) findViewById(R.id.working_mark_edt);

		Pubdate.setHint(cd.get(cd.YEAR) + "-" + (cd.get(cd.MONTH)+1) + "-"
				+ cd.get(cd.DAY_OF_MONTH));

		Pubdate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new DatePickerDialog(WorkingActivity.this,
						new OnDateSetListener() {
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								monthOfYear++;
								Pubdate.setText(year + "-" + (monthOfYear) + "-"
										+ dayOfMonth);
							}
						}, cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), cd
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		Pubdate.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
					return;
				new DatePickerDialog(WorkingActivity.this,
						new OnDateSetListener() {
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								Pubdate.setText(year + "-" + (monthOfYear+1) + "-"
										+ dayOfMonth);
							}
						}, cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), cd
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

	}

	TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			nameImage.setVisibility(View.GONE);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			if (arg0.length() == 0) {
				nameImage.setVisibility(View.VISIBLE);
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.working_top_back:
			finish();
			break;
		case R.id.working_image_select:
			TelephoneDielog();
			break;
		case R.id.working_button:// 保存
			addWorkReport();
			break;
		default:
			break;
		}
	}

	private void TelephoneDielog() {
		final String[] items = new String[] { "拍照", "从相册中选择" };
		getPicDialog = new AlertDialog.Builder(WorkingActivity.this)
				.setTitle("请选择获取方式:")
				.setSingleChoiceItems(items, 0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									//Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
									//startActivityForResult(intent, 1);
									Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
									File appDir = new File(Environment.getExternalStorageDirectory() + "/yusheng");
									if (!appDir.exists()) 
										appDir.mkdir();

									uriPhoto = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
									                + "/yusheng/", "yushengPic"
									                + String.valueOf(System.currentTimeMillis()) + ".jpg"));
									cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uriPhoto);
									try {
										cameraIntent.putExtra("return-data", true);
										startActivityForResult(cameraIntent, 1);
									} catch (Exception e) {
										e.printStackTrace();
									}
									break;
								case 1:
									Intent i = new Intent(
											Intent.ACTION_PICK,
											android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);// 调用android的图库
									startActivityForResult(i, 2);
									break;
								default:
									break;
								}
							}
						}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(getPicDialog.isShowing()){
			getPicDialog.dismiss();
		}
		switch (requestCode) {
		case 1:
			switch (resultCode) {
			case Activity.RESULT_OK:// 照相完成点击确定
				/*String sdStatus = Environment.getExternalStorageState(); // 对sd进行读写
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
					return;
				}
				Bundle bundle = data.getExtras();
				Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
				FileOutputStream b = null;
				String path = Environment.getExternalStorageDirectory().getPath() + "/pk4fun/";
				File file = new File(path);
				file.mkdirs();// 可读可写的权利
				String str = null;
				Date date = null;
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");// 获取当前时间，进一步转化为字符串
				date = new Date(resultCode);
				str = format.format(date);
				imgPath = path + str + ".jpg";
				ImageView imageView = (ImageView) findViewById(R.id.imageshow);
				
				try {
					b = new FileOutputStream(imgPath);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
					bitmapAttachment = BitmapFactory.decodeFile(imgPath);
					imageView.setImageBitmap(bitmapAttachment);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						if(b != null){
							b.flush(); // 清空缓冲区数据
							b.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}*/
				
				if (bitmapAttachment != null) //如果不释放的话，不断取图片，将会内存不够
					bitmapAttachment.recycle();
				ContentResolver cr = this.getContentResolver();
				try {
					bitmapAttachment = BitmapFactory.decodeStream(cr.openInputStream(uriPhoto));
					ImageView imageView = (ImageView) findViewById(R.id.imageshow);
					imageView.setImageBitmap(bitmapAttachment);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}				
				break;
			case Activity.RESULT_CANCELED:// 取消
				break;
			}
			break;
		case 2:
			switch (resultCode) {
			case Activity.RESULT_OK: {
				Uri uri = data.getData();
				Cursor cursor = WorkingActivity.this.getContentResolver()
						.query(uri, null, null, null, null);
				cursor.moveToFirst(); // 移动到第一条记录
				imgPath = cursor.getString(1); // 图片文件路径
				cursor.close();
				ImageView imageView = (ImageView) findViewById(R.id.imageshow);
				bitmapAttachment = BitmapFactory.decodeFile(imgPath);
				imageView.setImageBitmap(bitmapAttachment);
			}
				break;
			case Activity.RESULT_CANCELED:// 取消
				break;
			}
			break;

		}

	}
}
