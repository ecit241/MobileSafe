package com.android.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.utils.SmsUtils;
import com.android.utils.SmsUtils.SmsCallBack;

public class AtoolsActivity extends Activity {

	private ProgressDialog pd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	
	public void numberQuery(View view){
		
		Intent intent = new Intent(this, NumberAdressQueryActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 点击事件,短信备份
	 * @param view
	 */
	public void smsBackup(View view){
		
		pd = new ProgressDialog(this);
		pd.setTitle("提示");
		pd.setMessage("正在备份短信....");
		pd.setCanceledOnTouchOutside(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(){
			public void run() {
				
				try {
					SmsUtils.BackSms(AtoolsActivity.this, new SmsCallBack() {
						
						@Override
						public void onSmsProgress(int progress) {
							pd.setProgress(progress);
						}

						@Override
						public void beforeOpration(int max) {
							pd.setMax(max);
						}
					});
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
						}
					});
					
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
						}
					});
					e.printStackTrace();
				}finally{
					pd.dismiss();
				}
			};
		}.start();
		
	}
	
	
	boolean flag;
	/**
	 * 点击事件,短信还原
	 * @param view
	 */
	public void smsRestore(View view){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提示");
		builder.setMessage("要删除已存在短信么?");
		
		pd = new ProgressDialog(AtoolsActivity.this);
		pd.setTitle("提示");
		pd.setMessage("正在还原短信....");
		pd.setCanceledOnTouchOutside(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				flag = true;
				smsRetoreProgress();
			}
		});
		
		builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				flag = false;
				smsRetoreProgress();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	private void smsRetoreProgress() {
		pd.show();
		new Thread(){
			public void run() {
				
				try {
					SmsUtils.restoreSms(AtoolsActivity.this, flag, new SmsCallBack() {
						
						@Override
						public void onSmsProgress(int progress) {
							pd.setProgress(progress);
						}

						@Override
						public void beforeOpration(int max) {
							pd.setMax(max);
						}
					});
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "还原成功", Toast.LENGTH_SHORT).show();
						}
					});
					
				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "还原失败", Toast.LENGTH_SHORT).show();
						}
					});
					e.printStackTrace();
				}finally{
					pd.dismiss();
				}
			};
		}.start();
	}

}
