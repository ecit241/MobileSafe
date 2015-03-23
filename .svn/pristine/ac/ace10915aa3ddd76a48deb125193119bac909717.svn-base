package com.android.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.ui.SettingItemView;

public class Setup2Activity extends BaseSetupActivity{
	
	private SettingItemView siv_setup2_sim;
	// 读取手机sim卡信息
	private TelephonyManager tm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		siv_setup2_sim = (SettingItemView)findViewById(R.id.siv_setup2_sim);
		tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		
		if(sp.getString("sim", null) != null){
			siv_setup2_sim.setChecked(true);
		}else{
			siv_setup2_sim.setChecked(false);
		}
		
		siv_setup2_sim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(siv_setup2_sim.isChecked()){
					siv_setup2_sim.setChecked(false);
					editor.putString("sim", null);
				}else{
					siv_setup2_sim.setChecked(true);
					// 保存sim卡的序列号
					String sim = tm.getSimSerialNumber();					
					editor.putString("sim", sim);
				}
				editor.commit();
			}
		});
	}
	
	/**
	 * 下一步点击事件
	 * @param view
	 */
	public void next(View view){
		showNext();
	}

	protected void showNext() {
		// 取出是否绑定sim
		String sim = sp.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			Toast.makeText(this, "请绑定sim卡", Toast.LENGTH_LONG).show();
			return ;
		}
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		// 要求在finish()或者satartActivity(intent)后面执行
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}
	
	/**
	 * 上一步点击事件
	 * @param view
	 */
	public void pre(View view){
		showPre();
	}

	@Override
	protected void showPre() {
		
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
		// 要求在finish()或者satartActivity(intent)后面执行
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
}
