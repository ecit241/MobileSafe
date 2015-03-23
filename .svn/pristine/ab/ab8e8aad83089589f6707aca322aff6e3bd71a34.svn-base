package com.android.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.mobilesafe.service.AddressService;
import com.android.mobilesafe.service.CallSmsSafeService;
import com.android.mobilesafe.service.WatchDogService;
import com.android.ui.SettingClickView;
import com.android.ui.SettingItemView;
import com.android.utils.ServiceUtils;

public class SettingActivity extends Activity {

	private SettingItemView siv_update;
	private SharedPreferences sp;
	private SettingItemView siv_show_address;
	private Intent showAddressIntent;
	
	// 设置归属地显示框背景
	private SettingClickView scv_changbg;
	
	// 黑名单拦截设置
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;
	
	// 看门狗设置
	private SettingItemView siv_watchdog;
	private Intent watchDogIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		siv_update = (SettingItemView)findViewById(R.id.siv_update);
		siv_show_address = (SettingItemView)findViewById(R.id.siv_show_address);
		
		boolean update =  sp.getBoolean("update", false);
		if(update){
			// 自动升级已经开启
			siv_update.setChecked(true);
			//siv_update.setDesc("自动更新已开启");
		}else{
			siv_update.setChecked(false);
			//siv_update.setDesc("自动更新已关闭");
		}
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// 判断是否选中
				if(siv_update.isChecked()){
					// 已经打开了自动升级
					siv_update.setChecked(false);
					//siv_update.setDesc("自动更新已关闭");
					editor.putBoolean("update", false);
				}else{
					
					siv_update.setChecked(true);
					//siv_update.setDesc("自动更新已开启");
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		
		
		//设置是否开启来电归属地显示
		showAddressIntent = new Intent(this, AddressService.class);
		// 服务是否开启
		boolean isRunning = ServiceUtils.isServiceRunning(this, getPackageName()+".service.AddressService");
		
		if(sp.getBoolean("address_service_open", false)){
			siv_show_address.setChecked(true);
			if(!isRunning)startService(showAddressIntent);
		}else{
			siv_show_address.setChecked(false);
			if(isRunning)stopService(showAddressIntent);
		}
		// if(isRunning){
		// siv_show_address.setChecked(true);
		// }else{
		// siv_show_address.setChecked(false);
		// }
		siv_show_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				
				// 监听来电显示开启
				if (siv_show_address.isChecked()) {
					editor.putBoolean("address_service_open", false);
					siv_show_address.setChecked(false);
					stopService(showAddressIntent);
				}else{
					editor.putBoolean("address_service_open", true);
					siv_show_address.setChecked(true);
					startService(showAddressIntent);
				}
				editor.commit();
			}
		});
		
		// 设置号码归属地产背景
		scv_changbg = (SettingClickView)findViewById(R.id.scv_changebg);
		scv_changbg.setTitle("归属地提示风格");
		
		final String [] items = {"半透明","活力橙	","卫士蓝","金属灰","苹果绿"};
		int witch = sp.getInt("witch", 0);
		scv_changbg.setDesc(items[witch]);
		
		scv_changbg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int i = sp.getInt("witch", 0);
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示风格");
				builder.setSingleChoiceItems(items, i, new DialogInterface.OnClickListener() {
					
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 保存选择参数
						Editor editor = sp.edit();
						editor.putInt("witch", which);
						scv_changbg.setDesc(items[which]);
						editor.commit();
						// 取消对话框
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("cencel", null);
				builder.show();
			}
		});
		
		siv_callsms_safe = (SettingItemView)findViewById(R.id.siv_callsms_safe);
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
		
		boolean callSmsServiceRunning = ServiceUtils.isServiceRunning(this, getPackageName()+".service.CallSmsSafeService");
		
		if(sp.getBoolean("block", false)){
			siv_callsms_safe.setChecked(true);
			if(!callSmsServiceRunning) startService(callSmsSafeIntent);
		}else{
			siv_callsms_safe.setChecked(false);
			if(!callSmsServiceRunning) stopService(callSmsSafeIntent);
		}
		
		siv_callsms_safe.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// 判断是否选中
				if(siv_callsms_safe.isChecked()){
					siv_callsms_safe.setChecked(false);
					editor.putBoolean("block", false);
					stopService(callSmsSafeIntent);
				}else{
					
					siv_callsms_safe.setChecked(true);
					editor.putBoolean("block", true);
					startService(callSmsSafeIntent);
				}
				editor.commit();
			}
		});
		
		// 看门狗设置
		siv_watchdog = (SettingItemView)findViewById(R.id.siv_watchdog);
		watchDogIntent = new Intent(this, WatchDogService.class);
		
		boolean watchDogServiceRunning = ServiceUtils.isServiceRunning(this, getPackageName()+".service.WatchDogService");
		
		if(sp.getBoolean("watchdog", false)){
			siv_watchdog.setChecked(true);
			if(!watchDogServiceRunning) startService(watchDogIntent);
		}else{
			siv_watchdog.setChecked(false);
			if(!watchDogServiceRunning) stopService(watchDogIntent);
		}
		
		siv_watchdog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// 判断是否选中
				if(siv_watchdog.isChecked()){
					siv_watchdog.setChecked(false);
					editor.putBoolean("watchdog", false);
					stopService(watchDogIntent);
				}else{
					
					siv_watchdog.setChecked(true);
					editor.putBoolean("watchdog", true);
					startService(watchDogIntent);
				}
				editor.commit();
			}
		});
	}
}
