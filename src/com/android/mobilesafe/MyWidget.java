package com.android.mobilesafe;

import com.android.mobilesafe.service.UpdateWidgetservice;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class MyWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, UpdateWidgetservice.class);
		context.startService(i);
		System.out.println("onReceive");
		super.onReceive(context, i);
	}
	
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		System.out.println("onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	
	@Override
	public void onEnabled(Context context) {
		System.out.println("onEnabled");
		Intent intent = new Intent(context, UpdateWidgetservice.class);
		context.startService(intent);
		super.onEnabled(context);
	}
	
	
	
	@Override
	public void onDisabled(Context context) {
		System.out.println("onDisabled");
		Intent intent = new Intent(context, UpdateWidgetservice.class);
		context.stopService(intent);
		super.onDisabled(context);
	}
}
