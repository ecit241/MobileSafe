package com.android.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.android.mobilesafe.MyWidget;
import com.android.mobilesafe.R;
import com.android.utils.SystemInfoUtils;

public class UpdateWidgetservice extends Service {

	private ScreenOffReceiver scrOffReceiver;
	private ScreenOnReceiver scrOnReceiver;
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		scrOffReceiver = new ScreenOffReceiver();
		scrOnReceiver = new ScreenOnReceiver();
		awm = AppWidgetManager.getInstance(this);
		registerReceiver(scrOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(scrOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		
		startTimer();
		
		super.onCreate();
	}

	private void startTimer() {
		if(timer==null && task==null){
			timer = new Timer();
			task = new TimerTask() {
				
				@Override
				public void run() {
					//System.out.println("update widget");
					// ���ø��µ����
					ComponentName provider = new ComponentName(UpdateWidgetservice.this, MyWidget.class);
					RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
					views.setTextViewText(R.id.process_count, "�������е�����:" + SystemInfoUtils.getRunningProcessCount(getApplicationContext()));
					views.setTextViewText(R.id.process_memory, "�����ڴ�:" + Formatter.formatFileSize(getApplicationContext(), SystemInfoUtils.getAvailMem(getApplicationContext())));
					// ����һ������,��������ɱ���һ��������ִ��
					// �Զ���һ���㲥,ɱ����̨����
					Intent intent = new Intent();
					// �˴�acition��������
					intent.setAction("com.android.mobilesafe.killall")	;
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
					
					awm.updateAppWidget(provider, views);
				}
			};
			timer.scheduleAtFixedRate(task, 0, 3000);
		}
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			stopTimer();
		}
		
	}
	
	private class ScreenOnReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			startTimer();
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		stopTimer();
		unregisterReceiver(scrOffReceiver);
		unregisterReceiver(scrOnReceiver);
		scrOffReceiver = null;
		scrOnReceiver = null;
	}

	private void stopTimer() {
		if(timer!=null && task!=null){
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}

}