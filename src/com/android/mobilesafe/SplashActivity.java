package com.android.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.android.utils.StreamTools;

public class SplashActivity extends Activity {

	protected static final int ENTER_HOME = 0;
	protected static final int SHOW_UPDATE_DIALOG = 1;
	protected static final int ERROR = 2;
	private TextView tv_splash_version;
	private String description;
	private String apkurl;
	private TextView tv_update_info;
	
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_splash_version = (TextView)findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本: " + getVersionName());
		tv_update_info = (TextView)findViewById(R.id.tv_update_info);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		boolean update = sp.getBoolean("update", false);
		
		boolean shortcut = sp.getBoolean("shortcut", false);
		if(!shortcut){
			installShortCut();
		}
		// 拷贝数据库
		copyDB("address.db");
		copyDB("antivirus.db");

		if(update){
			// 检查升级
			checkUpdate();
		}else{
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					enterHome();
				}
			}, 2000);
		}
		
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1000);
		findViewById(R.id.rl_root_splash).startAnimation(aa);
	}
	
	/**
	 * 创建快捷图标
	 */
	private void installShortCut() {
		Editor editor = sp.edit();
		// 发送广播的意图
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// 快捷方式 包含三个重要的信息: 1.名称 2.图标 3.干什么事
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机小卫士");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		// 桌面点击图标对应的意图
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClassName(getPackageName(), "com.android.mobilesafe.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		// 发出广播
		sendBroadcast(intent);
		editor.putBoolean("shortcut", true);
		Toast.makeText(SplashActivity.this, "已在桌面建立快捷图标", Toast.LENGTH_LONG	).show();
		editor.commit();
	}

	/**
	 * path 把address.db 这个数据库copy到 /data/data/packageName/files/address.db
	 */
	private void copyDB(String filename) {
		try {
			File file = new File(getFilesDir(), filename);
			if(file.exists()&&file.length()>0){
				return ;
			}
			InputStream is = getAssets().open(filename);
			
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len=is.read(buffer))!=-1){
				fos.write(buffer, 0, len);
			}
			fos.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:
				System.out.println("有新版本下载");
				showUpdateDialog();
				break;

			case ENTER_HOME:
				enterHome();
				break;
				
			case ERROR:
				Toast.makeText(getApplicationContext(), "检测更新异常", Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			}
		}
	};
	
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// 关闭当前页面
		finish();
	}
	
	/**
	 * 弹出升级对话框
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("升级提示");
		//builder.setCancelable(false);  //强制升级
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// 进入主页面
				enterHome();
				dialog.dismiss();
			}
		});
		builder.setMessage(description);
		builder.setPositiveButton("立即升级", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载apk,并且替换安装
				if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					Toast.makeText(getApplicationContext(), "未检测到sdcard", Toast.LENGTH_SHORT).show();
					enterHome();   // 进入主页面
					return ;
				}
				
				// afnal
				FinalHttp finalHttp = new FinalHttp();
				String target = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobilesafe2.0.apk";
				System.out.println(target);
				finalHttp.download(apkurl, target, new AjaxCallBack<File>() {
					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						t.printStackTrace();
						Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
						super.onFailure(t, errorNo, strMsg);
						enterHome();   // 进入主页面
					}
					
					@Override
					public void onLoading(long count, long current) {
						// TODO Auto-generated method stub
						super.onLoading(count, current);
						tv_update_info.setVisibility(View.VISIBLE);
						int progress = (int) (current * 100 / count);
						tv_update_info.setText("下载进度:" + progress + "%");
					}
					
					@Override
					public void onSuccess(File t) {
						// TODO Auto-generated method stub
						super.onSuccess(t);
						Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
						installAPK(t);
					}
					
					/**
					 * 安装apk
					 * @param t
					 */
					private void installAPK(File t) {
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						intent.addCategory("android.intent.category.DEFAULT");
						intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
					
						startActivity(intent);
					}
					
				});
				
			}
		});
		builder.setNegativeButton("下次再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();   // 进入主页面
			}
		});
		builder.create().show();
	}
	
	private void checkUpdate(){
		new Thread(){
			public void run() {
				// URL  http://10.10.1.4/mobilesafe/updateinfo.html
				Message mes = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {
					
					URL url = new URL(getString(R.string.serverurl));
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					int code = conn.getResponseCode();
					if(code != 200){
						return ;
					}
					InputStream is = conn.getInputStream();
					String result = StreamTools.readFromStream(is);
					System.out.println(result);
				
					// json解析
					JSONObject obj = new JSONObject(result);
					String version = (String)obj.get("version");
					description = (String)obj.get("description");
					apkurl = (String)obj.get("apkurl");
					
					// 校验是否有新版本
					if(getVersionName().equals(version)){
						// 版本一致,进入主页面
						mes.what = ENTER_HOME;
					}else{
						// 有新版本,弹出一个升级对话框
						mes.what = SHOW_UPDATE_DIALOG;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					mes.what = ERROR;
				}finally{
					long endTime = System.currentTimeMillis();
					// 用了多少时间
					long dTime = endTime - startTime;
					if(dTime < 2000){
						try {
							Thread.sleep(2000-dTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(mes);
				}
			
			};
		}.start();
	}
	
	/**
	 * 得到应用程序的版本名
	 */
	private String getVersionName(){
		// 用来管理手机的apk
		PackageManager pm = getPackageManager();
		// 得到指定apk的功能清单文件
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return "";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
