package com.android.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.android.mobilesafe.domain.AppInfo;

/**
 * ҵ�񷽷�,�ṩ�ֻ����氲װ������Ӧ�ó������Ϣ
 * @author Administrator
 *
 */
public class AppInfoProvider {
	
	/**
	 * ��ȡ���а�װӦ�ó�����Ϣ
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppInfo(Context context){
		
		PackageManager pm = context.getPackageManager();
		// ���а�װ��ϵͳ�ϵ�Ӧ�ó������Ϣ
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : packInfos) {
			AppInfo appInfo = new AppInfo();
			String packname = packageInfo.packageName;
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			Drawable icon = applicationInfo.loadIcon(pm);
			String name = (String) applicationInfo.loadLabel(pm);
			int flags = applicationInfo.flags;  // Ӧ�ó�����Ϣ��־
			int uid = applicationInfo.uid;
			appInfo.setUid(uid);
//			File rcvfile = new File("/proc/uid_stat/"+uid+"/tcp_rcv");
//			File sndfILE = new File("/proc/uid_stat/"+uid+"/tcp_snd");			
			if((flags & ApplicationInfo.FLAG_SYSTEM) == 0){
				// �û�����
				appInfo.setUserApp(true);
			}else{
				// ϵͳ����
				appInfo.setUserApp(false);
			}
			if((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0){
				// �ֻ�����
				appInfo.setInRom(true);
			}else{
				// sd��
				appInfo.setInRom(false);
			}
			
			appInfo.setPackname(packname);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfos.add(appInfo);
		}
		
		return appInfos;
	}
}