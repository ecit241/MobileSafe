package com.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

/**
 * 短信工具类
 * @author Administrator
 *
 */
public class SmsUtils {
	/**
	 * 备份短信回调接口
	 * @author Administrator
	 *
	 */
	public interface SmsCallBack{
		/**
		 * 开始备份的时候,设置进度的最大值
		 * @param max
		 */
		public void beforeOpration(int max);
		
		/**
		 * 备份过程中增加进度
		 * @param progress
		 */
		public void onSmsProgress(int progress);
	}
	
	/**
	 * 备份用户短信
	 * @param context 上下文
	 * @param pd  进度对话框
	 * @param BackUpCallBack 备份短信回调接口
	 * @throws Exception 
	 */
	public static void BackSms(Context context, SmsCallBack callBack) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(), "smsbackup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		// 把用户的短信一条一条读出来,按照一定的格式写到文件里
		XmlSerializer serializer = Xml.newSerializer(); // 获得xml文件生成器
		// 初始化生成器
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"address","type","date","body"}, null, null, null);
		// 短信总数
		int max = cursor.getCount();
		//pd.setMax(max);
		callBack.beforeOpration(max);
		serializer.attribute(null, "max", max+"");
		int progress = 0;
		while(cursor.moveToNext()){
			String body = cursor.getString(cursor.getColumnIndex("body"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String type = cursor.getString(cursor.getColumnIndex("type"));
			String date = cursor.getString(cursor.getColumnIndex("date"));
			
			Thread.sleep(500);
			
			serializer.startTag(null, "sms");
			
			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");
			
			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");
			
			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");
			
			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");
			
			serializer.endTag(null, "sms");
			progress ++;
			//pd.setProgress(progress);
			callBack.onSmsProgress(progress);
		}
		cursor.close();
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
	}
	
	/**
	 * 还原短信
	 * @param context
	 * @param flag 是否清理原来短信
	 * @throws Exception 
	 */
	public static void restoreSms(Context context,boolean flag, SmsCallBack callBack) throws Exception{
		// 1.读xml文件
		// Xml.newPullParser();
		XmlPullParser parser = Xml.newPullParser();
		File file = new File(Environment.getExternalStorageDirectory(), "smsbackup.xml");
		InputStream is = new FileInputStream(file);
		parser.setInput(is, "utf-8");
		// 获得事件类型
		int eventType = parser.getEventType();
		// 2.读取max
		
		int max;
		String body;
		String address;
		String type;
		String date;
		
		
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		ContentValues values = null;
		
		// 判断是否清理原来短信
		 if(flag){
			 resolver.delete(uri, null, null);
		 }
		
		int progress = 0;
		
		// 3.读取每一条信息
		while(eventType != XmlPullParser.END_DOCUMENT){
			
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;

			case XmlPullParser.START_TAG:
				if("smss".equals(parser.getName())){
					max = Integer.parseInt(parser.getAttributeValue(0));
					callBack.beforeOpration(max);
				}else if("sms".equals(parser.getName())){
					values = new ContentValues();
				}else if("body".equals(parser.getName())){
					body = parser.nextText();
					values.put("body", body);
				}else if("address".equals(parser.getName())){
					address = parser.nextText();
					values.put("address", address);
				}else if("type".equals(parser.getName())){
					type = parser.nextText();
					values.put("type", type);
				}else if("date".equals(parser.getName())){
					date = parser.nextText();
					values.put("date", date);
				}
				break;
			case XmlPullParser.END_TAG:
				if("sms".equals(parser.getName())){
					
					// 4.把短信插入到系统短信应用
					resolver.insert(uri, values);

					// 进度
					progress ++;
					callBack.onSmsProgress(progress);
					
					Thread.sleep(500);
					values = null;
				}
				break;
			}
			// 事件循环
			eventType = parser.next();
		}	
		is.close();
	}
}
