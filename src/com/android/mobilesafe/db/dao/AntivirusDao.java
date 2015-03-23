package com.android.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 病毒数据库查询业务类
 * @author Administrator
 *
 */
public class AntivirusDao {
	
	private static String path = "data/data/com.android.mobilesafe/files/antivirus.db";
	/**
	 * 查询一个md5是否在病毒数据库存在
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(String md5){
		boolean result = false;
		// 打开病毒数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select _id from datable where md5=?", new String[]{md5});
		if(cursor.moveToNext()){
			result = true;
		}
		db.close();
		return result;
	}
}
