package com.android.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.mobilesafe.R;
import com.android.mobilesafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {
	
	// 窗体管理者
	private WindowManager wm;
	
	private TelephonyManager tm;
	
	private MyPhoneStateListener listener;
	
	private OutCallReceiver receiver;
	
	private View view;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	// 在服务里定义一个广播接收者,监听去电
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 得到要打出去的电话号码
			String phone = getResultData();
			String address = NumberAddressQueryUtils.queryNumber(phone);
			MyToast(address);
		}

	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// 实例化窗体
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		// 注册一个广播接收者,用代码方式
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	class MyPhoneStateListener extends PhoneStateListener{
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// 第一个参数是状态,第二个参数是电话号码
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:  // 铃声响起的时候
				// 根据得到的电话号码,查询它的归属地
				String address = NumberAddressQueryUtils.queryNumber(incomingNumber);
				
				// 自定义Toast
				//Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
				MyToast(address);
				
				break;

			case TelephonyManager.CALL_STATE_IDLE:   // 空闲状态
				// 把自定义Toast移除
				if(view != null){
					wm.removeView(view);
				}
				break;
			default:
				break;
			}
		}
	}
	
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消注册一个广播接收者
		unregisterReceiver(receiver);
		receiver = null;
		
		// 取消监听来电
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}

	private WindowManager.LayoutParams params;
	long[] mHits = new long[2];
	/**
	 * 自定义Toast
	 * @param address
	 */
	public void MyToast(String address) {
		view = View.inflate(this, R.layout.address_show, null);
		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length-1] = SystemClock.uptimeMillis();
				if(mHits[0] > (SystemClock.uptimeMillis() - 500)){
					
					params.x = (wm.getDefaultDisplay().getWidth() - view.getWidth()) / 2;
					params.y = (wm.getDefaultDisplay().getHeight() - view.getHeight()) / 2;
					wm.updateViewLayout(view, params);
				}
			}
		});
		
		// 给view对象设置一个触摸的监听器
		view.setOnTouchListener(new OnTouchListener() {
			
			// 定义手指的初始位置
			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					System.out.println("开始位置: " + startX + "  " + startY);
					break;

				case MotionEvent.ACTION_MOVE:
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					System.out.println("新的位置: " + newX + "  " + newY);
					int dx = newX -startX;
					int dy = newY - startY;
					System.out.println("偏移量: " + dx + "  " + dy);
					System.out.println("位置更新");
					
					params.x += dx;
					params.y += dy;
					if(params.x <0){
						params.x = 0;
					}else if(params.x >(wm.getDefaultDisplay().getWidth()-view.getWidth())){
						params.x = wm.getDefaultDisplay().getWidth()-view.getWidth();
					}
					if(params.y <0){
						params.y = 0;
					}else if(params.y >(wm.getDefaultDisplay().getHeight()-view.getHeight())){
						params.y = wm.getDefaultDisplay().getHeight()-view.getHeight();
					}
					wm.updateViewLayout(view, params);
					// 重新初始化手指初始化位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
					
				case MotionEvent.ACTION_UP:
					break;
					
				}
				
				return false;  // 事件处理完毕,还要让父控件响应触摸事件
			}
		});
		
		// "半透明","活力橙	","卫士蓝","金属灰","苹果绿"
		int ids[] = {R.drawable.call_locate_white,R.drawable.call_locate_orange,
				R.drawable.call_locate_blue,R.drawable.call_locate_gray,
				R.drawable.call_locate_green};
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		int witch = sp.getInt("witch", 0);
		TextView textView = (TextView) view.findViewById(R.id.tv_address);
		textView.setText(address);
	
		view.setBackgroundResource(ids[witch]);
		// 窗体的参数
		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
		
		params = mParams;
		
		params.gravity = Gravity.LEFT | Gravity.TOP;
		// 指定距离窗体左边5,上边20; 只有设定上面属性,下面设置才起作用
		params.x = 5;
		params.y = 30;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        
        //TYPE_PRIORITY_PHONE: android系统里面具有电话优先级的一种窗体类型,记得添加权限
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.setTitle("Toast");
		
		
		wm.addView(view, params);
	}
}
