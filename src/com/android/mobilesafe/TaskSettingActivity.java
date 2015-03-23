package com.android.mobilesafe;

import com.android.mobilesafe.service.AutoCleanService;
import com.android.utils.ServiceUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskSettingActivity extends Activity {

	private CheckBox cb_show_system;
	private CheckBox cb_auto_clean;
	private SharedPreferences sp;
	
	private Intent autoCleanService;
	private boolean autoClean;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		cb_show_system = (CheckBox)findViewById(R.id.cb_show_system);
		cb_auto_clean = (CheckBox)findViewById(R.id.cb_auto_clean);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		cb_show_system.setChecked(sp.getBoolean("showsystem", false));
		cb_auto_clean.setChecked(sp.getBoolean("autoclean", false));
		
		autoCleanService = new Intent(this, AutoCleanService.class);
		autoClean = ServiceUtils.isServiceRunning(this, getPackageName()+".service.AutoCleanService");
		if(sp.getBoolean("autoclean", false)){
			if(!autoClean)startService(autoCleanService);
		}else{
			if(autoClean)stopService(autoCleanService);
		}
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				editor.putBoolean("showsystem", isChecked);
				editor.commit();
			}
		});
		
		
		cb_auto_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// �����Ĺ㲥�¼���һ������Ĺ㲥�¼�,���嵥ע���ǲ�����Ч��
				// ֻ���ڴ�������ע��Ż���Ч
				
				Editor editor = sp.edit();
				editor.putBoolean("autoclean", isChecked);
				editor.commit();
				if (isChecked) {
					if(!autoClean)startService(autoCleanService);
				}else{
					if(autoClean)stopService(autoCleanService);
				}

			}
		});
	}
	
	/**
	 * �����Ƿ���ʾϵͳӦ��
	 * @param view
	 */
	public void setShowSystem(View view){
		if(cb_show_system.isChecked()){
			cb_show_system.setChecked(false);
		}else{
			cb_show_system.setChecked(true);
		}
	}
	
	/**
	 * �����Ƿ�������
	 * @param view
	 */
	public void setAutoClean(View view){
		if(cb_auto_clean.isChecked()){
			cb_auto_clean.setChecked(false);
		}else{
			cb_auto_clean.setChecked(true);
		}
	}
}
