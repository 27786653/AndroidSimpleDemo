package com.yuanhai.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhzl.utility.IGlobalSetting;
import com.yuanhai.adapter.InfoFragmentAdapter;
import com.yuanhai.appShows.MainApplication;
import com.yuanhai.test.Info_Fragment.MainFragment;
import com.yuanhai.test.Info_Fragment.MeFragment;
import com.yuanhai.test.More_Fragment.MoreFragment;
import com.yuanhai.test.Set_Fragment.SetFragment;

import java.util.ArrayList;

/**
 * 主界面（）
 */
public class MainActivity extends FragmentActivity implements OnClickListener{
    private static final String TAG = "MainActivity";
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ImageView ivBottomLine;

    private int currIndex = 0;   //当前选中页面
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;
    private int position_two;
    private int position_three;
    private int position_four;
    private ImageView infoImage , knownImage , settingImage , moreImage, meImage;
    private TextView infoText , knownText , settingText , moreText, meText;
    
    MainFragment mainFragment = new MainFragment();
    MoreFragment moreFragment2 = new MoreFragment();
	SetFragment setFragment = new SetFragment();
	MoreFragment moreFragment = new MoreFragment();
	MeFragment meFragment = new MeFragment();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_page);
        InitWidth();  //初始化宽度（得到屏幕尺寸进行分配）
        InitTextView(); //初始化文本域
        InitViewPager(); //初始化viewPage
        FlashActivity.onShowInfoActivity(this);
        
        IGlobalSetting setting = ((MainApplication)getApplication()).getGlobalSetting();
		setting.setFirstRunFlag(0);
		
		UserInfo.setFirstRun(this, false);
    }
    //初始化Tab的每一个TextView
    private void InitTextView() {
    	//导航菜单初始化
        findViewById(R.id.tv_tab_main).setOnClickListener(this);
        findViewById(R.id.tv_tab_work).setOnClickListener(this);
        findViewById(R.id.tv_tab_life).setOnClickListener(this);
        findViewById(R.id.tv_tab_me).setOnClickListener(this);
        findViewById(R.id.tv_tab_more).setOnClickListener(this);
        
        infoImage = (ImageView) findViewById(R.id.img_info);
        knownImage = (ImageView) findViewById(R.id.img_known);
        moreImage = (ImageView) findViewById(R.id.img_more);
        settingImage = (ImageView) findViewById(R.id.img_setting);
        meImage = (ImageView) findViewById(R.id.img_me);
        
        infoText = (TextView) findViewById(R.id.text_info);
        knownText = (TextView) findViewById(R.id.text_known);
        settingText = (TextView) findViewById(R.id.text_setting);
        moreText = (TextView) findViewById(R.id.text_more);
        meText = (TextView) findViewById(R.id.text_me);
    }
    //初始化ViewPage
    private void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager);
        fragmentsList = new ArrayList<Fragment>();
        moreFragment2.setXmlFile("more2.xml");
        moreFragment.setXmlFile("more.xml");
        
        fragmentsList.add(mainFragment);
		fragmentsList.add(moreFragment2);
		fragmentsList.add(moreFragment);
		fragmentsList.add(meFragment);
		fragmentsList.add(setFragment);
				
		mPager.setAdapter(new InfoFragmentAdapter(getSupportFragmentManager(),
				fragmentsList));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		infoImage.setBackgroundResource(R.drawable.img_info);
        infoText .setTextColor(getResources().getColor(R.color.info_bottom_color));
	}
//初始化宽度
    private void InitWidth() {
        ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);
        bottomLineWidth = ivBottomLine.getLayoutParams().width;
        Log.d(TAG, "cursor imageview width=" + bottomLineWidth);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (int) ((screenW / 5.0 - bottomLineWidth) / 2);
        Log.i("MainActivity", "offset=" + offset);

        position_one = (int) (screenW / 5.0);
        position_two = position_one * 2;
        position_three = position_one * 3;
        position_four  = position_one * 4;
    }

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    };

    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
            case 0:
                if (currIndex == 1) {//从第二页到第一页
                    animation = new TranslateAnimation(position_one, 0, 0, 0);
                    knownImage.setBackgroundResource(R.drawable.info_icon_known);
                    knownText .setTextColor(getResources().getColor(R.color.white));
                } else if (currIndex == 2) {
                	animation = new TranslateAnimation(position_two, 0, 0, 0);
                    moreImage.setBackgroundResource(R.drawable.info_icon_more);
                    moreText .setTextColor(getResources().getColor(R.color.white));
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(position_three, 0, 0, 0);
                	meText .setTextColor(getResources().getColor(R.color.white));
                	meImage.setBackgroundResource(R.drawable.use);
                }
                else if (currIndex == 4) {
                    animation = new TranslateAnimation(position_four, 0, 0, 0);
                	settingText .setTextColor(getResources().getColor(R.color.white));
                	settingImage.setBackgroundResource(R.drawable.info_icon_set);
                }
                infoImage.setBackgroundResource(R.drawable.img_info);
                infoText .setTextColor(getResources().getColor(R.color.info_bottom_color));
                break;
            case 1:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(0, position_one, 0, 0);
                    infoImage.setBackgroundResource(R.drawable.info_icon_selected);
                    infoText .setTextColor(getResources().getColor(R.color.white));
                } else if (currIndex == 2) {
                	animation = new TranslateAnimation(position_two, position_one, 0, 0);
                    moreImage.setBackgroundResource(R.drawable.info_icon_more);
                    moreText .setTextColor(getResources().getColor(R.color.white));
                }else if (currIndex == 3) {
                	animation = new TranslateAnimation(position_three, position_one, 0, 0);
                 	meText .setTextColor(getResources().getColor(R.color.white));
                 	meImage.setBackgroundResource(R.drawable.use);
                } 
                else if (currIndex == 4) {
                	animation = new TranslateAnimation(position_four, position_one, 0, 0);
                 	settingText .setTextColor(getResources().getColor(R.color.white));
                 	settingImage.setBackgroundResource(R.drawable.info_icon_set);
                }
                knownImage.setBackgroundResource(R.drawable.img_known);
                knownText.setTextColor(getResources().getColor(R.color.info_bottom_color));
                break;
            case 2:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(0, position_two, 0, 0);
                    infoText .setTextColor(getResources().getColor(R.color.white));
                    infoImage.setBackgroundResource(R.drawable.info_icon_selected);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(position_one, position_two, 0, 0);
                    knownText .setTextColor(getResources().getColor(R.color.white));
                    knownImage.setBackgroundResource(R.drawable.info_icon_known);
                } else if (currIndex == 3) {
                	animation = new TranslateAnimation(position_three, position_two, 0, 0);
                 	meText .setTextColor(getResources().getColor(R.color.white));
                 	meImage.setBackgroundResource(R.drawable.use);
                }
                else if (currIndex == 4) {
                	animation = new TranslateAnimation(position_four, position_two, 0, 0);
                 	settingText .setTextColor(getResources().getColor(R.color.white));
                 	settingImage.setBackgroundResource(R.drawable.info_icon_set);
                }
                moreImage.setBackgroundResource(R.drawable.img_more);
                moreText .setTextColor(getResources().getColor(R.color.info_bottom_color));
                break;
            case 3:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(0, position_three, 0, 0);
                    infoText .setTextColor(getResources().getColor(R.color.white));
                    infoImage.setBackgroundResource(R.drawable.info_icon_selected);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(position_one, position_three, 0, 0);
                    knownText .setTextColor(getResources().getColor(R.color.white));
                    knownImage.setBackgroundResource(R.drawable.info_icon_known);
                } else if (currIndex == 2) {
                	animation = new TranslateAnimation(position_two, position_three, 0, 0);
                    moreImage.setBackgroundResource(R.drawable.info_icon_more);
                    moreText .setTextColor(getResources().getColor(R.color.white));
                } else if (currIndex == 4) {
                	animation = new TranslateAnimation(position_four, position_three, 0, 0);
                 	settingText .setTextColor(getResources().getColor(R.color.white));
                 	settingImage.setBackgroundResource(R.drawable.info_icon_set);
                }
                meImage.setBackgroundResource(R.drawable.use0);
                meText .setTextColor(getResources().getColor(R.color.info_bottom_color));
                break;
            case 4:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(0, position_four, 0, 0);
                    infoText .setTextColor(getResources().getColor(R.color.white));
                    infoImage.setBackgroundResource(R.drawable.info_icon_selected);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(position_one, position_four, 0, 0);
                    knownText .setTextColor(getResources().getColor(R.color.white));
                    knownImage.setBackgroundResource(R.drawable.info_icon_known);
                } else if (currIndex == 2) {
                	animation = new TranslateAnimation(position_two, position_four, 0, 0);
                    moreImage.setBackgroundResource(R.drawable.info_icon_more);
                    moreText .setTextColor(getResources().getColor(R.color.white));
                }else if (currIndex == 3) {
                	animation = new TranslateAnimation(position_three, position_four, 0, 0);
                    meImage.setBackgroundResource(R.drawable.use);
                    meText .setTextColor(getResources().getColor(R.color.white));
                }
                
                settingImage.setBackgroundResource(R.drawable.img_setting);
                settingText .setTextColor(getResources().getColor(R.color.info_bottom_color));
                break;
            }
            currIndex = arg0;
            animation.setFillAfter(true);   //结尾动画
            animation.setDuration(300);
            ivBottomLine.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

	@Override
	public void onClick(View v) {
		int currentIndex = 0;
		switch (v.getId()) {
		case R.id.tv_tab_main:
			currentIndex = 0;
			break;
		case R.id.tv_tab_work:
			currentIndex = 1;
			break;
		case R.id.tv_tab_life:
			currentIndex = 2;
			break;
		case R.id.tv_tab_me:
			currentIndex = 3;
			break;
		case R.id.tv_tab_more:
			currentIndex = 4;
			break;
		default:
			break;
		}
		mPager.setCurrentItem(currentIndex);
	}
}