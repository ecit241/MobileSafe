package com.android.mobilesafe;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CleanCasheActivity extends Activity {

	private ProgressBar pb;
	private TextView tv_scan_status;
	private PackageManager pm;
	private LinearLayout ll_container;
	private long totalcache = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		tv_scan_status = (TextView)findViewById(R.id.tv_scan_status);
		pb = (ProgressBar)findViewById(R.id.pb);
		ll_container = (LinearLayout)findViewById(R.id.ll_container);
		scanCache();
	}
	
	/**
	 * 扫描手机所有应用程序的缓存
	 */
	public void scanCache(){
		pm = getPackageManager();
		new Thread(){
			public void run() {
				Method getSizeInfoMethod = null;
				Method[] methods = PackageManager.class.getMethods();
				for (Method method : methods) {
					if("getPackageSizeInfo".equals(method.getName())){
						getSizeInfoMethod = method;
					}
				}
				List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
				pb.setMax(packageInfos.size());
				int progress = 0;
				for (PackageInfo packInfo : packageInfos) {
					try {
						getSizeInfoMethod.invoke(pm, packInfo.packageName, new MyDataObserver());
					} catch (Exception e) {
						e.printStackTrace();
					}
					progress ++;
					pb.setProgress(progress);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tv_scan_status.setText("共扫描到缓存:" + Formatter.formatFileSize(getApplicationContext(), totalcache));
					}
				});
			};
		}.start();
	}
	
	private class MyDataObserver extends IPackageStatsObserver.Stub{

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			final long cache = pStats.cacheSize;
			//long code = pStats.codeSize;
			//long data = pStats.dataSize;
			final String packname = pStats.packageName;
			final ApplicationInfo appInfo;
			try {
				appInfo = pm.getApplicationInfo(packname, 0);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tv_scan_status.setText("正在扫描:" + appInfo.loadLabel(pm) );
						if(cache > 0){
							totalcache += cache;
							View view = View.inflate(getApplicationContext(), R.layout.list_item_cacheinof, null);
							TextView tv_cache = (TextView)view.findViewById(R.id.tv_case_size);
							tv_cache.setText("缓存大小:" + Formatter.formatFileSize(getApplicationContext(), cache));
							TextView tv_name = (TextView)view.findViewById(R.id.tv_app_name);
							tv_name.setText(appInfo.loadLabel(pm));
							ImageView iv_clean = (ImageView)view.findViewById(R.id.iv_clean_cache);
							iv_clean.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									try {
										Method method = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
										method.invoke(pm, packname, new MyPackDataObserver());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
							
							ll_container.addView(view, 0);
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class MyPackDataObserver extends IPackageDataObserver.Stub{

		@Override
		public void onRemoveCompleted(String packageName, final boolean succeeded)
				throws RemoteException {
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if(succeeded){
						Toast.makeText(getApplicationContext(), "清理成功", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "清理失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
	
	/**
	 * 清理手机全部缓存
	 * @param view
	 */
	public void clearAll(View view){
		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if ("freeStorageAndNotify".equals(method)) {
				try {
					method.invoke(pm, Integer.MAX_VALUE, new MyPackDataObserver());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
