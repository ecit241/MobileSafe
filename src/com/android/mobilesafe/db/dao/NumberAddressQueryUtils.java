package com.android.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {
	
	private static String path = "data/data/com.android.mobilesafe/files/address.db";
	/**
	 * 
	 * @param number ����
	 * @return �绰������
	 */
	public static String queryNumber(String number){
		// path ��address.db ������ݿ�copy�� /data/data/packageName/files/address.db
		
		String address = number;
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		if(number.matches("^1[34568]\\d{9}")){
			// �ֻ�����
			
			Cursor cursor = database.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)", new String[]{number.substring(0, 7)});
			while(cursor.moveToNext()){
				address = cursor.getString(0);
			}
			cursor.close();
		} else {
			// �����ĵ绰����
			switch (number.length()) {
			case 3:
				// 110
				address = "�˾�����";
				break;
			case 4:
				// 5554
				address = "ģ����";
				break;
			case 5:
				// 10086
				address = "�ͷ��绰";
				break;
			case 7:
				//
				address = "���غ���";
				break;

			case 8:
				address = "���غ���";
				break;

			default:
				// /����;�绰 10
				if (number.length() > 10 && number.startsWith("0")) {
					// 010-59790386
					Cursor cursor = database.rawQuery(
							"select location from data2 where area = ?",
							new String[] { number.substring(1, 3) });

					while (cursor.moveToNext()) {
						String location = cursor.getString(0);
						address = location.substring(0, location.length()-2);
					}
					cursor.close();

					// 0855-59790386
					cursor = database.rawQuery(
							"select location from data2 where area = ?",
							new String[] { number.substring(1, 4) });
					while (cursor.moveToNext()) {
						String location = cursor.getString(0);
						address = location.substring(0, location.length()-2);

					}
				}

				break;
			}

		}
		return address;
	}
}
