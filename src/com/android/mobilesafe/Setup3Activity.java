package com.android.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity{
	
	private EditText et_phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_phone = (EditText)findViewById(R.id.et_phone);
		
		et_phone.setText(sp.getString("safenumber", ""));
	}
	
	/**
	 * 下一步点击事件
	 * @param view
	 */
	public void next(View view){
		showNext();
	}

	protected void showNext() {
		String phone = et_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "安全号码尚未设置", Toast.LENGTH_SHORT).show();
			return ;
		}
		// 保存安全号码
		Editor editor = sp.edit();
		editor.putString("safenumber", phone);
		editor.commit();
		
		Intent intent = new Intent(this, Setup4Activity.class);
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

	protected void showPre() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	
	/**
	 * 选择联系人
	 * @param view
	 */
	public void selectContact(View view){
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}
	
	/**
	 * 取得选择联系人返回结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(data == null){
			return ;
		}
		String phone = data.getStringExtra("phone").replace("-", "");
		et_phone.setText(phone);
	}
}
