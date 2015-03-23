package com.android.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {
	
	/**
	 * 校验某个服务是否还在运行
	 * @param context
	 * @param serviceName
	 * @return
	 */
	public static boolean isServiceRunning(Context context,String serviceName){
		// 不光可以可以管理Activity,还可以管理Service
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(100);
		for (RunningServiceInfo info : infos) {
			// 得到正在运行的服务的名字
			String name = info.service.getClassName();
			if(serviceName.equals(name)){
				return true;
			}
		}
		
		//am.getRunningServices(maxNum)
		return false;
	}
}
