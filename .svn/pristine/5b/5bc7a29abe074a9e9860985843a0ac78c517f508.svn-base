package com.android.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ApplockDBOpenHelper extends SQLiteOpenHelper {

	private static String name = "applock.db";
	private static int version = 1;
	public ApplockDBOpenHelper(Context context) {
		super(context, name, null, version);
	}

	// 初始化数据库的表结构
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table applock(id integer primary key autoincrement,packname varchar(40))";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
