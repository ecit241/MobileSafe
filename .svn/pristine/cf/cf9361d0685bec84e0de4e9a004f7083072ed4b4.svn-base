package com.android.mobilesafe;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

import com.android.mobilesafe.db.dao.AntivirusDao;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActivity extends Activity {

	protected static final int SCANNING = 0;
	protected static final int SCANNED = 1;
	private ImageView iv_scan;
	private ProgressBar progressBar;
	private PackageManager pm;
	private LinearLayout ll_container;
	private TextView tv_scan_status;
	private Button bt_click;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANNING:
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				tv_scan_status.setText("正在扫描 " + scanInfo.name);
				TextView tv = new TextView(getApplicationContext());
				tv.setSingleLine();
				if(scanInfo.isvirus){
					tv.setTextColor(Color.RED);
					tv.setText("发现病毒:" + scanInfo.name);
				}else{
					tv.setTextColor(Color.BLACK);
					tv.setText("扫描安全:" + scanInfo.name);
				}
					
				ll_container.addView(tv,0);
				break;

			case SCANNED:
				tv_scan_status.setText("扫描完成");
				iv_scan.clearAnimation();
				bt_click.setText("完成");
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		iv_scan = (ImageView)findViewById(R.id.iv_scan);
		ll_container = (LinearLayout)findViewById(R.id.ll_container);
		bt_click = (Button)findViewById(R.id.bt_click);
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_scan.startAnimation(ra);
		
		progressBar = (ProgressBar)findViewById(R.id.progressBar1);
		
		tv_scan_status = (TextView)findViewById(R.id.tv_scan_status);
		scanVirus();
	}
	
	
	/**
	 * 扫描病毒
	 */
	public void scanVirus(){
		pm = getPackageManager();
		tv_scan_status.setText("正在初始化杀毒引擎....");
		new Thread(){
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				progressBar.setMax(infos.size());
				
				for (int i=0;i<infos.size();i++) {
					// String datadir = info.applicationInfo.dataDir;
					// apk文件的路径
					ScanInfo scanInfo = new ScanInfo();
					
					String sourcedir = infos.get(i).applicationInfo.sourceDir;
					String name = infos.get(i).applicationInfo.loadLabel(pm).toString();
					String md5 = getFileMd5(sourcedir);
					
					scanInfo.name = name;
					scanInfo.packname = sourcedir;
					
					//查询md5信息是否在病毒数据库存在
					if(AntivirusDao.isVirus(md5)){
						// 发现病毒
						scanInfo.isvirus = true;
					}else{
						scanInfo.isvirus = false;
					}
					
					Message msg = handler.obtainMessage();
					msg.obj = scanInfo;
					msg.what = SCANNING;
					handler.sendMessage(msg);
					
					progressBar.setProgress(i);
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				Message msg = handler.obtainMessage();
				msg.what = SCANNED;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	/**
	 * 扫描信息类
	 * @author Administrator
	 *
	 */
	class ScanInfo{
		String packname;
		String name;
		boolean isvirus;
	}
	
	/**
	 * 获取文件的md5值
	 * @param path 文件全路径
	 * @return
	 */
	private String getFileMd5(String path){
		// 获取一个文件的特征信息
		File file = new File("userinfo.xml");
		// md5
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			//MessageDigest digest = MessageDigest.getInstance("sha1");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len = fis.read(buffer)) != -1){
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			
			StringBuffer sb = new StringBuffer();
			for(byte b: result){
				int number = b & 0xff;//加盐
				String hex = Integer.toHexString(number);
				if(hex.length()==1){
					sb.append("0");
				}
				sb.append(hex);
			}
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	
	/**
	 * 返回主界面
	 * @param view
	 */
	public void click(View view){
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}
}
