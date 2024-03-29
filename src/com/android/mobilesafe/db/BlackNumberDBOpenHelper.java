package com.android.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	private static String name = "blcaknumber.db";
	private static int version = 1;
	public BlackNumberDBOpenHelper(Context context) {
		super(context, name, null, version);
	}

	// 初始化数据库的表结构
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table blacknumber(id integer primary key autoincrement,num varchar(20),mode varchar(2))";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
