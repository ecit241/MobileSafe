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
	
	// ���������
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
	// �ڷ����ﶨ��һ���㲥������,����ȥ��
	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// �õ�Ҫ���ȥ�ĵ绰����
			String phone = getResultData();
			String address = NumberAddressQueryUtils.queryNumber(phone);
			MyToast(address);
		}

	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// ʵ��������
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		// ע��һ���㲥������,�ô��뷽ʽ
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
			// ��һ��������״̬,�ڶ��������ǵ绰����
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:  // ���������ʱ��
				// ���ݵõ��ĵ绰����,��ѯ���Ĺ�����
				String address = NumberAddressQueryUtils.queryNumber(incomingNumber);
				
				// �Զ���Toast
				//Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
				MyToast(address);
				
				break;

			case TelephonyManager.CALL_STATE_IDLE:   // ����״̬
				// ���Զ���Toast�Ƴ�
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
		// ȡ��ע��һ���㲥������
		unregisterReceiver(receiver);
		receiver = null;
		
		// ȡ����������
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}

	private WindowManager.LayoutParams params;
	long[] mHits = new long[2];
	/**
	 * �Զ���Toast
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
		
		// ��view��������һ�������ļ�����
		view.setOnTouchListener(new OnTouchListener() {
			
			// ������ָ�ĳ�ʼλ��
			int startX;
			int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					System.out.println("��ʼλ��: " + startX + "  " + startY);
					break;

				case MotionEvent.ACTION_MOVE:
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					System.out.println("�µ�λ��: " + newX + "  " + newY);
					int dx = newX -startX;
					int dy = newY - startY;
					System.out.println("ƫ����: " + dx + "  " + dy);
					System.out.println("λ�ø���");
					
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
					// ���³�ʼ����ָ��ʼ��λ��
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
					
				case MotionEvent.ACTION_UP:
					break;
					
				}
				
				return false;  // �¼��������,��Ҫ�ø��ؼ���Ӧ�����¼�
			}
		});
		
		// "��͸��","������	","��ʿ��","������","ƻ����"
		int ids[] = {R.drawable.call_locate_white,R.drawable.call_locate_orange,
				R.drawable.call_locate_blue,R.drawable.call_locate_gray,
				R.drawable.call_locate_green};
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		int witch = sp.getInt("witch", 0);
		TextView textView = (TextView) view.findViewById(R.id.tv_address);
		textView.setText(address);
	
		view.setBackgroundResource(ids[witch]);
		// ����Ĳ���
		WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
		
		params = mParams;
		
		params.gravity = Gravity.LEFT | Gravity.TOP;
		// ָ�����봰�����5,�ϱ�20; ֻ���趨��������,�������ò�������
		params.x = 5;
		params.y = 30;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        
        //TYPE_PRIORITY_PHONE: androidϵͳ������е绰���ȼ���һ�ִ�������,�ǵ����Ȩ��
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.setTitle("Toast");
		
		
		wm.addView(view, params);
	}
}
