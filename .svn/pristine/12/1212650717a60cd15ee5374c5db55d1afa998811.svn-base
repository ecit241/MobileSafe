package com.android.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.android.mobilesafe.db.dao.BlackNumberDao;

public class CallSmsSafeService extends Service {

	private InnerSmsReceiver receiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;
	private MyPhoneStateListener listener;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		dao = new BlackNumberDao(getApplicationContext());
		receiver = new InnerSmsReceiver();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
		
		IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(1000);   // 设置优先级最高
		registerReceiver(receiver, filter);
	}
	
	class MyPhoneStateListener extends PhoneStateListener{
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// 第一个参数是状态,第二个参数是电话号码
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:  // 铃声响起的时候
				String result = dao.findMode(incomingNumber);
				if("1".equals(result) || "3".equals(result)){
					System.out.println("挂断电话");
					endCall();     // 另外一个进程进行的远程服务,方法调用后,呼叫记录可能还没有生成
					// 删除呼叫记录
					// 联系人应用程序
					//deleteCallLog(incomingNumber);
					// 观察呼叫记录数据内容的变化
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true, new CallLogObserver(incomingNumber,new Handler()));
				}
				break;

			case TelephonyManager.CALL_STATE_IDLE:   // 空闲状态

				break;
			default:
				break;
			}
		}
	}
	
	private class CallLogObserver extends ContentObserver{

		private String incomingNumber;
		public CallLogObserver(String incomingNumber,Handler handler) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}
		
		@Override
		public void onChange(boolean selfChange) {
			System.out.println("数据库的内容变化了,产生了呼叫记录");
			// 取消注册
			getContentResolver().unregisterContentObserver(this);
			// 删除呼叫记录
			deleteCallLog(incomingNumber);
			super.onChange(selfChange);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}
	
	/**
	 * 利用内容提供者删除呼叫记录
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		Uri url = Uri.parse("content://call_log/calls");
		
		resolver.delete(url, "number = ?", new String[]{incomingNumber});
	}

	public void endCall() {
		
		//IBinder binder = ServiceManager.getService(TELEPHONY_SERVICE);
		try {
			// 加载ServiceManager的字节码
			Class<?> clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			// 得到 getService方法
			Method method = clazz.getDeclaredMethod("getService", String.class);
			// 调用ServiceManager.getService方法
			IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(ibinder).endCall();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("短信到来了");
			// 检查发件人是否是黑名单号码
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				SmsMessage smSmessage = SmsMessage.createFromPdu((byte[]) obj);
				//  得到发件人
				String sender = smSmessage.getOriginatingAddress();
				String result = dao.findMode(sender);
				if("2".equals(result) || "3".equals(result)){
					System.out.println("拦截短信");
					abortBroadcast();
				}
			}
		}
		
	}

}
