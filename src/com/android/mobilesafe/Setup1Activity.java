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
	 * ��һ������¼�
	 * @param view
	 */
	public void next(View view){
		showNext();
	}

	protected void showNext() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		// Ҫ����finish()����satartActivity(intent)����ִ��
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	protected void showPre() {
		
	}

}
