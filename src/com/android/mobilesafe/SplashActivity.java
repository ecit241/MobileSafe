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
		tv_splash_version.setText("�汾: " + getVersionName());
		tv_update_info = (TextView)findViewById(R.id.tv_update_info);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		boolean update = sp.getBoolean("update", false);
		
		boolean shortcut = sp.getBoolean("shortcut", false);
		if(!shortcut){
			installShortCut();
		}
		// �������ݿ�
		copyDB("address.db");
		copyDB("antivirus.db");

		if(update){
			// �������
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
	 * �������ͼ��
	 */
	private void installShortCut() {
		Editor editor = sp.edit();
		// ���͹㲥����ͼ
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// ��ݷ�ʽ ����������Ҫ����Ϣ: 1.���� 2.ͼ�� 3.��ʲô��
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "�ֻ�С��ʿ");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		// ������ͼ���Ӧ����ͼ
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClassName(getPackageName(), "com.android.mobilesafe.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		// �����㲥
		sendBroadcast(intent);
		editor.putBoolean("shortcut", true);
		Toast.makeText(SplashActivity.this, "�������潨�����ͼ��", Toast.LENGTH_LONG	).show();
		editor.commit();
	}

	/**
	 * path ��address.db ������ݿ�copy�� /data/data/packageName/files/address.db
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
				System.out.println("���°汾����");
				showUpdateDialog();
				break;

			case ENTER_HOME:
				enterHome();
				break;
				
			case ERROR:
				Toast.makeText(getApplicationContext(), "�������쳣", Toast.LENGTH_SHORT).show();
				enterHome();
				break;
			}
		}
	};
	
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// �رյ�ǰҳ��
		finish();
	}
	
	/**
	 * ���������Ի���
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("������ʾ");
		//builder.setCancelable(false);  //ǿ������
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// ������ҳ��
				enterHome();
				dialog.dismiss();
			}
		});
		builder.setMessage(description);
		builder.setPositiveButton("��������", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ����apk,�����滻��װ
				if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					Toast.makeText(getApplicationContext(), "δ��⵽sdcard", Toast.LENGTH_SHORT).show();
					enterHome();   // ������ҳ��
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
						Toast.makeText(getApplicationContext(), "����ʧ��", Toast.LENGTH_LONG).show();
						super.onFailure(t, errorNo, strMsg);
						enterHome();   // ������ҳ��
					}
					
					@Override
					public void onLoading(long count, long current) {
						// TODO Auto-generated method stub
						super.onLoading(count, current);
						tv_update_info.setVisibility(View.VISIBLE);
						int progress = (int) (current * 100 / count);
						tv_update_info.setText("���ؽ���:" + progress + "%");
					}
					
					@Override
					public void onSuccess(File t) {
						// TODO Auto-generated method stub
						super.onSuccess(t);
						Toast.makeText(getApplicationContext(), "���سɹ�", Toast.LENGTH_SHORT).show();
						installAPK(t);
					}
					
					/**
					 * ��װapk
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
		builder.setNegativeButton("�´���˵", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				enterHome();   // ������ҳ��
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
				
					// json����
					JSONObject obj = new JSONObject(result);
					String version = (String)obj.get("version");
					description = (String)obj.get("description");
					apkurl = (String)obj.get("apkurl");
					
					// У���Ƿ����°汾
					if(getVersionName().equals(version)){
						// �汾һ��,������ҳ��
						mes.what = ENTER_HOME;
					}else{
						// ���°汾,����һ�������Ի���
						mes.what = SHOW_UPDATE_DIALOG;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					mes.what = ERROR;
				}finally{
					long endTime = System.currentTimeMillis();
					// ���˶���ʱ��
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
	 * �õ�Ӧ�ó���İ汾��
	 */
	private String getVersionName(){
		// ���������ֻ���apk
		PackageManager pm = getPackageManager();
		// �õ�ָ��apk�Ĺ����嵥�ļ�
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
