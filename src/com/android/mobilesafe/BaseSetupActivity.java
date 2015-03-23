package com.android.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

public abstract class BaseSetupActivity extends Activity {

	// ��������ʶ����
	private GestureDetector detector;
	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		// ʵ��������ʶ����
		detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//������x��������������
				if(Math.abs(velocityX) < 200){
					return true;
				}
				
				//����б�����
				if(Math.abs(e1.getRawY() - e1.getRawY()) > 100){
					return true;
				}
				
				if((e2.getRawX() - e1.getRawX()) > 200){
					showPre();
					return true;
				}
				if((e1.getRawX() - e2.getRawX()) > 200){
					showNext();
					return true;
				}
				
				return super.onFling(e1, e2, velocityX, velocityY);
			}
			
			
		});
	}
	
	protected abstract void showPre();

	protected abstract void showNext();

		// ʹ������ʶ����
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// ����Ļ�����¼���������ʶ����
			detector.onTouchEvent(event);
			return super.onTouchEvent(event);
		}
}
