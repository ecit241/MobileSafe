package com.android.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mobilesafe.db.dao.NumberAddressQueryUtils;

public class NumberAdressQueryActivity extends Activity {
	
	private EditText et_phone;
	private TextView tv_result;
	
	// 震动服务
	private Vibrator vibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_adress_query);
		et_phone = (EditText)findViewById(R.id.et_phone);
		tv_result = (TextView)findViewById(R.id.tv_result);
		vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
		et_phone.addTextChangedListener(new TextWatcher() {
			
			/**
			 * 当文本发生变化的时候回调
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s!= null&&s.length()>=3){
					//查询数据库，并且显示结果
					String address = NumberAddressQueryUtils.queryNumber(s.toString());
					tv_result.setText(address);
				}
				
			}
			
			/**
			 * 当文本发生变化之前回调
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			/**
			 * 当文本发生变化之后回调
			 */
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/**
	 * 号码归属地查询
	 * @param view
	 */
	public void numberAddressQuery(View view){
		String phone = et_phone.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
			Toast.makeText(this, "请输入号码", Toast.LENGTH_SHORT).show();
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_phone.startAnimation(shake);
			
			// 当电话号码为空的时候,就震动
			vibrator.vibrate(2000);
			return ;
		}
		// 去数据库查询号码归属地
		// 1.网络查询; 2.本地数据库
		
		String address = NumberAddressQueryUtils.queryNumber(phone);
			
		tv_result.setText(address);
	}
}
