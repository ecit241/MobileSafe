package com.android.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {

	private SharedPreferences sp;
	private TextView tv_safe_number;
	private ImageView iv_is_locked;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// �ж�һ��,�Ƿ�����������,���û������,����ת������ҳ��ȥ����
		boolean configed = sp.getBoolean("configed", false);
		if(configed){
			// ������
			setContentView(R.layout.activity_lost_find);
			tv_safe_number = (TextView)findViewById(R.id.tv_safe_number);
			iv_is_locked = (ImageView)findViewById(R.id.iv_is_locked);
			tv_safe_number.setText(sp.getString("safenumber", ""));
			if(sp.getBoolean("openProtect", false)){
				iv_is_locked.setImageResource(R.drawable.lock);
			}
		}else{
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			// �رյ�ǰҳ��
			finish();
		}
	}
	
	/**
	 * ���½����ֻ�����������ҳ��
	 * @param view
	 */
	public void reEnterSetup(View view){
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		// �رյ�ǰҳ��
		finish();
	}
}
