package com.android.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class Setup4Activity extends BaseSetupActivity{
	
	private CheckBox cb_open_protect;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		cb_open_protect = (CheckBox)findViewById(R.id.cb_open_protect);
		
		cb_open_protect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					cb_open_protect.setText("你将开启防盗保护");
				}else{
					cb_open_protect.setText("你没有开启防盗保护");
				}
			}
		});
		boolean open_protect = sp.getBoolean("openProtect", false);
		cb_open_protect.setChecked(open_protect);
		if(open_protect){
			cb_open_protect.setText("你将开启防盗保护");
		}else{
			cb_open_protect.setText("你没有开启防盗保护");
		}
	}
	
	/**
	 * 下一步点击事件
	 * @param view
	 */
	public void next(View view){
		showNext();
	}

	protected void showNext() {
		if(!cb_open_protect.isChecked()){
			Toast.makeText(this, "您还没有选择开启防盗保护", Toast.LENGTH_LONG).show();
			return ;
		}
		
		System.out.println("设置完成");
		Editor editor = sp.edit();
		editor.putBoolean("openProtect", true);
		editor.putBoolean("configed", true);
		editor.commit();
		
		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}
	
	/**
	 * 上一步点击事件
	 * @param view
	 */
	public void pre(View view){
		showPre();
	}

	protected void showPre() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
}
