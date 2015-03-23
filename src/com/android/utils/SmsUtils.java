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
 * ���Ź�����
 * @author Administrator
 *
 */
public class SmsUtils {
	/**
	 * ���ݶ��Żص��ӿ�
	 * @author Administrator
	 *
	 */
	public interface SmsCallBack{
		/**
		 * ��ʼ���ݵ�ʱ��,���ý��ȵ����ֵ
		 * @param max
		 */
		public void beforeOpration(int max);
		
		/**
		 * ���ݹ��������ӽ���
		 * @param progress
		 */
		public void onSmsProgress(int progress);
	}
	
	/**
	 * �����û�����
	 * @param context ������
	 * @param pd  ���ȶԻ���
	 * @param BackUpCallBack ���ݶ��Żص��ӿ�
	 * @throws Exception 
	 */
	public static void BackSms(Context context, SmsCallBack callBack) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(), "smsbackup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		// ���û��Ķ���һ��һ��������,����һ���ĸ�ʽд���ļ���
		XmlSerializer serializer = Xml.newSerializer(); // ���xml�ļ�������
		// ��ʼ��������
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"address","type","date","body"}, null, null, null);
		// ��������
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
	 * ��ԭ����
	 * @param context
	 * @param flag �Ƿ�����ԭ������
	 * @throws Exception 
	 */
	public static void restoreSms(Context context,boolean flag, SmsCallBack callBack) throws Exception{
		// 1.��xml�ļ�
		// Xml.newPullParser();
		XmlPullParser parser = Xml.newPullParser();
		File file = new File(Environment.getExternalStorageDirectory(), "smsbackup.xml");
		InputStream is = new FileInputStream(file);
		parser.setInput(is, "utf-8");
		// ����¼�����
		int eventType = parser.getEventType();
		// 2.��ȡmax
		
		int max;
		String body;
		String address;
		String type;
		String date;
		
		
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		ContentValues values = null;
		
		// �ж��Ƿ�����ԭ������
		 if(flag){
			 resolver.delete(uri, null, null);
		 }
		
		int progress = 0;
		
		// 3.��ȡÿһ����Ϣ
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
					
					// 4.�Ѷ��Ų��뵽ϵͳ����Ӧ��
					resolver.insert(uri, values);

					// ����
					progress ++;
					callBack.onSmsProgress(progress);
					
					Thread.sleep(500);
					values = null;
				}
				break;
			}
			// �¼�ѭ��
			eventType = parser.next();
		}	
		is.close();
	}
}
