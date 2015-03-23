package com.android.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.mobilesafe.db.BlackNumberDBOpenHelper;

/**
 * 黑名单数据库的增删改查业务类
 * @author Administrator
 *
 */
public class BlackNumberDao {

	private BlackNumberDBOpenHelper helper;
	public BlackNumberDao(Context context){
		helper = new BlackNumberDBOpenHelper(context);
	}
	
	/**
	 * 查询黑名单号码是否存在
	 * @param number
	 * @return
	 */
	public boolean find(String number){
		boolean result = false;
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select * from blacknumber where num = ?", new String[]{number});
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		database.close();
		return result;
	}
	
	
	/**
	 * 查询黑名单号码总条数
	 * @param number
	 * @return
	 */
	public int getCount(){
		int result = 0;
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select count(id) from blacknumber", null);
		if(cursor.moveToNext()){
			result = cursor.getInt(0);
		}
		cursor.close();
		database.close();
		return result;
	}
	
	/**
	 * 查询黑名单号码拦截模式
	 * @param number
	 * @return
	 */
	public String findMode(String number){
		String result = null;
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select mode from blacknumber where num = ?", new String[]{number});
		if(cursor.moveToNext()){
			result = cursor.getString(cursor.getColumnIndex("mode"));
		}
		cursor.close();
		database.close();
		return result;
	}
	
	
	
	/**
	 * 添加黑名单号码
	 * @param number
	 * @param mode
	 */
	public long add(String number,String mode){
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("num", number);
		values.put("mode", mode);
		long id = database.insert("blacknumber", null, values);
		database.close();
		return id;
	}
	
	/**
	 * 修改黑名单拦截模式
	 * @param number
	 * @param mode
	 */
	public void update(String number,String mode){
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		database.update("blacknumber", values, "num = ?", new String[]{number});
		database.close();
	}
	
	/**
	 * 删除一条黑名单数据
	 * @param number
	 */
	public int delete(String number){
		SQLiteDatabase database = helper.getWritableDatabase();
		int erow= database.delete("blacknumber", "num = ?", new String[]{number});
		database.close();
		return erow;
	}
	
	/**
	 * 查找全部黑名单号码
	 * @return
	 */
	public List<Map<String, String>> findAll(){
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query(true, "blacknumber", null, null, null, null, null, "id desc", null);
		Map<String, String> map;
		while(cursor.moveToNext()){
			map = new HashMap<String, String>();
			String number = cursor.getString(cursor.getColumnIndex("num"));
			String mode = cursor.getString(cursor.getColumnIndex("mode"));
			map.put("num", number);
			map.put("mode", mode);
			list.add(map);
		}
		cursor.close();
		database.close();
		return list;
	}
	
	/**
	 * 查找部分黑名单号码
	 * @return
	 * @offset 从哪个位置开始查询
	 * @maxnumber 一次最多查找多少条数据
	 */
	public List<Map<String, String>> findPart(int offset, int maxnumber){
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query(true, "blacknumber", null, null, null, null, null, "id desc",offset + "," + maxnumber);
		Map<String, String> map;
		while(cursor.moveToNext()){
			map = new HashMap<String, String>();
			String number = cursor.getString(cursor.getColumnIndex("num"));
			String mode = cursor.getString(cursor.getColumnIndex("mode"));
			map.put("num", number);
			map.put("mode", mode);
			list.add(map);
		}
		cursor.close();
		database.close();
		return list;
	}
}
