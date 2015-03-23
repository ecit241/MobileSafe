package com.android.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

public abstract class BaseSetupActivity extends Activity {

	// 定义手势识别器
	private GestureDetector detector;
	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		// 实例化手势识别器
		detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//屏蔽在x滑动很慢的情形
				if(Math.abs(velocityX) < 200){
					return true;
				}
				
				//屏蔽斜滑情况
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

		// 使用手势识别器
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// 将屏幕滑动事件传给手势识别器
			detector.onTouchEvent(event);
			return super.onTouchEvent(event);
		}
}
