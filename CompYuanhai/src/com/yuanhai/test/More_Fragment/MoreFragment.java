package com.yuanhai.test.More_Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuanhai.appShows.MainApplication;
import com.yuanhai.test.R;
import com.yuanhai.test.UserInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
/**
 * ViewPage的第二页与第三页（共存？）
 */
public class MoreFragment extends Fragment implements OnClickListener{
	private Context context;
	private Map<Integer, MoreItem> moreItems = new HashMap<Integer, MoreItem>();

	private String urlPrefix;
	private LinearLayout allItems;
	private String xmlFile = new String();
	View rootView;
	
	public void setXmlFile(String file)
	{
		xmlFile = file;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		rootView = inflater.inflate(R.layout.more_fragment, container, false);
		
		MainApplication app = (MainApplication)getActivity().getApplication();
		urlPrefix = app.getGlobalSetting().getMorePrefixUrl();
		
		allItems = (LinearLayout)rootView.findViewById(R.id.more_items_Layout);
		if(xmlFile!=null){
			createViewFromXml();
		}

    	return rootView;
	}
	
	private void createViewFromXml()
	{
		int btnId = 0;
		
		InputStream stream;
		try {
			stream = context.getAssets().open(xmlFile);
			
			LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(stream);
			Element root = document.getDocumentElement();
			
			TextView btnTopText = (TextView)rootView.findViewById(R.id.more_top_text);
			btnTopText.setText(root.getAttribute("name"));
			
			NodeList items = root.getElementsByTagName("item");  
			for(int i=0;i<items.getLength();i++)
			{
				Element item = (Element)items.item(i);
				LinearLayout more_item = (LinearLayout)layoutInflater.inflate(R.layout.more_item, null);
				
				TextView name = (TextView)more_item.findViewById(R.id.more_items_text);
				name.setText(item.getAttribute("name"));
				
				NodeList lines = item.getElementsByTagName("line");
				for(int m=0;m<lines.getLength();m++)
				{
					Element line = (Element)lines.item(m);
					LinearLayout more_item_line = (LinearLayout)layoutInflater.inflate(R.layout.more_item_line, null);
					
					NodeList btns = line.getElementsByTagName("button");
					for(int n=0;n<btns.getLength();n++)
					{
						Element btn = (Element)btns.item(n);
						LinearLayout more_item_btn = (LinearLayout)layoutInflater.inflate(R.layout.more_item_btn, null);
						
						TextView btnName = (TextView)more_item_btn.findViewById(R.id.more_btn_text);
						String btnNameTxet = btn.getAttribute("name");
						btnName.setText(btnNameTxet);
						
						Button btnImage = (Button)more_item_btn.findViewById(R.id.more_btn_image);
						Resources res= getResources();
						int resID = res.getIdentifier(btn.getAttribute("image"), "drawable", context.getPackageName());
						btnImage.setBackgroundResource(resID);
						btnImage.setId(btnId);
						btnImage.setOnClickListener(this);
						
						MoreItem moreItem = new MoreItem();
						moreItem.id = btnId;
						moreItem.url = btn.getAttribute("url_id");
						moreItem.name = btnNameTxet;
					
						moreItems.put(btnId, moreItem);
						btnId++;
						
						more_item_line.addView(more_item_btn);
					}
					
					more_item.addView(more_item_line);
				}
				
				allItems.addView(more_item);
			}
					
			stream.close();
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {

		MoreItem item = moreItems.get(v.getId());
       String url_Id=item.url;
        String url="";
        		//http://pws.yuhi.com.cn/WyNotice/wyNoticeInfo?WcId=olRvosk7aQaIurhd3mmufntWUfOg
			url = urlPrefix + item.url + "&orgid=" + UserInfo.getOrgId(this.getActivity());
			Intent intent = null;
			intent = new Intent(context, MoreIteamActivity.class);
			intent.putExtra("url", url);
			intent.putExtra("name", item.name);
			context.startActivity(intent);

	}
}
