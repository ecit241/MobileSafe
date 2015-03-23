package com.android.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Setup1Activity extends BaseSetupActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	
	}
	
	/**
	 * 下一步点击事件
	 * @param view
	 */
	public void next(View view){
		showNext();
	}

	protected void showNext() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		// 要求在finish()或者satartActivity(intent)后面执行
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	protected void showPre() {
		
	}

}
