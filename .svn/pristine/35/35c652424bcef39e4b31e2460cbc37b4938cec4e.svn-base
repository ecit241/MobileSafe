package com.android.mobilesafe.test;

import com.android.mobilesafe.db.BlackNumberDBOpenHelper;
import com.android.mobilesafe.db.dao.BlackNumberDao;

import android.test.AndroidTestCase;

public class TestBlackNumberDB extends AndroidTestCase {

	public void testCreateDB() {
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(
				getContext());
		helper.getWritableDatabase();
	}

	public void testFind() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		System.out.println(dao.find("110"));
	}

	public void testAdd() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		long id = dao.add("112", "3");
		if (id != -1) {
			System.out.println("sucess");
		} else {
			System.out.println("fail");
		}
	}

	public void testDelete() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		int erow = dao.delete("110");
		if(erow != 0){
			System.out.println("É¾³ý³É¹¦");
		}
	}

	public void testUpdate() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("112", "2");
	}

	public void testQuery() {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		System.out.println(dao.findAll().toString());
	}
}
