package com.android.mobilesafe.service;

import java.util.List;

import com.android.mobilesafe.EnterPwdActivity;
import com.android.mobilesafe.db.dao.ApplockDao;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * 看门狗代码 监视系统程序的运行状态
 * 
 * @author Administrator
 * 
 */
public class WatchDogService extends Service {

	private ActivityManager am;
	private boolean flag;
	private ApplockDao dao;

	private InnerReceiver receiver;
	private String tempStopProtectPackname;
	
	private ScreenOffReceiver scrOffReceiver;
	
	private List<String> protectPacknames;
	private Intent intent;
	
	private DataChangeReceiver dataChangeReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("接收到临时停止的广播事件");
			tempStopProtectPackname = intent.getStringExtra("packname");
		}
		
	}
	
	private class DataChangeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			protectPacknames = dao.findAll();
		}
		
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopProtectPackname = "";
		}
		
	}

	@Override
	public void onCreate() {
		
		scrOffReceiver = new ScreenOffReceiver();
		registerReceiver(scrOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		
		receiver = new InnerReceiver();
		registerReceiver(receiver, new IntentFilter("com.android.mobilesafe.tempstop"));
		
		dataChangeReceiver = new DataChangeReceiver();
		registerReceiver(dataChangeReceiver, new IntentFilter("com.android.mobilesafe.applockchange"));
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new ApplockDao(this);
		protectPacknames = dao.findAll();
		intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
		// 这个服务是没有任务栈的,在服务开启activity,要指定这个activity运行的任务栈
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		flag = true;
		new Thread() {
			public void run() {
				while (flag) {
					// 获取程序运行任务栈,最近打开的任务在前面
					List<RunningTaskInfo> infos = am.getRunningTasks(1);
					String packname = infos.get(0).topActivity.getPackageName();
					//System.out.println("packname:"+packname);
					
					//if (dao.find(packname)) {//查询数据库太慢了，消耗资源，改成查询内存
					if(protectPacknames.contains(packname)){//查询内存效率高很多
						// 判断这个应用程序是否需要停止临时保护
						if(packname.equals(tempStopProtectPackname)){
						// 当前程序需要保护
							continue;
						}
						
						// 设置要保护程序的包名
						intent.putExtra("packname", packname);
						startActivity(intent);
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		flag = false;
		unregisterReceiver(receiver);
		receiver = null;
		
		unregisterReceiver(scrOffReceiver);
		scrOffReceiver = null;
		
		unregisterReceiver(dataChangeReceiver);
		dataChangeReceiver = null;
		super.onDestroy();
	}

}
