package com.android.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;
	@Override
	public void onReceive(Context context, Intent intent) {
		
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		// 读取之前保存的SIM信息
		String saveSim = sp.getString("sim", "");
		// 读取当前的SIM卡信息
		String realSim = tm.getSimSerialNumber();
		// 比较是否一样
		if(saveSim.equals(realSim)){
			// sim没有变更
			
		}else{
			System.out.println("sim 已经变更");
		}
	}

}
