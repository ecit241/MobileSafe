package com.android.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactActivity extends Activity {

	private ListView list_select_contact;
	private SimpleAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		final List<Map<String, String>> list = getContactInfo();
		list_select_contact = (ListView)findViewById(R.id.list_select_contact);
		adapter = new SimpleAdapter(this, list, R.layout.contact_item_view, new String[]{"name","phone"}, new int[]{R.id.tv_name,R.id.tv_phone});
		
		list_select_contact.setAdapter(adapter);
		
		list_select_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String phone = list.get(position).get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);
				// 当前页面关闭
				finish();
			}
		
		});
	}

	
	private List<Map<String, String>> getContactInfo() {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		
		// 得到一个内容解析器
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		
		Cursor cursor= resolver.query(uri, new String[]{"contact_id"}, null, null, null);
		while(cursor.moveToNext()){
			String contact_id = cursor.getString(0);
			
			if(contact_id == null)continue;
			Cursor dataCursor = resolver.query(dataUri, new String[]{"data1","mimetype"}, "raw_contact_id = ?", new String[]{contact_id}, null);
			Map<String, String> map = new HashMap<String, String>();
			while(dataCursor.moveToNext()){
				String mimetype = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
				String data1 = dataCursor.getString(dataCursor.getColumnIndex("data1"));
				if(mimetype.equals("vnd.android.cursor.item/phone_v2")){
					
					map.put("phone", data1);
				}else if(mimetype.equals("vnd.android.cursor.item/name")){
					
					map.put("name", data1);
				}
			}
			dataCursor.close();
			list.add(map);
		}
		
		cursor.close();
		
		return list;
	}
}
