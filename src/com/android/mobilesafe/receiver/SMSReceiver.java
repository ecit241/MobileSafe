package com.android.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.android.mobilesafe.R;
import com.android.mobilesafe.service.GPSService;

public class SMSReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		
		// ���ն���
		Object[] objs = (Object[])intent.getExtras().get("pdus");
		
		for (Object b : objs) {
			// �����ĳһ������
			SmsMessage sms = SmsMessage.createFromPdu((byte[])b);
			// ������
			String sender = sms.getOriginatingAddress();
			String safenumber = sp.getString("safenumber", "");
		//	if(sender.contains(safenumber)){
				// ����
				String body = sms.getMessageBody();
				if("#*location*#".equals(body)){
					// �õ��ֻ�GPS
					System.out.println("�õ��ֻ�GPS");
					// ��������
					Intent i = new Intent(context, GPSService.class);
					context.startService(i);
					SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
					String lastlocation = sp.getString("lastlocation", null);
					if(TextUtils.isEmpty(lastlocation)){
						SmsManager.getDefault().sendTextMessage(sender, null, "getting location ....", null, null);
					}else{
						SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
					}
					
					// ���ն���
					abortBroadcast();
				}else if("#*alarm*#".equals(body)){
					// ���ű�������
					System.out.println("���ű�������");
					MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
					player.setLooping(false);
					player.setVolume(1.0f, 1.0f);
					player.start();
					
					abortBroadcast();
				}else if("#*wipedata*#".equals(body)){
					// Զ���������
					System.out.println("Զ���������");
					// ���ն���
					abortBroadcast();
				}else if("#*lockscreen*#".equals(body)){
					// Զ������
					System.out.println("Զ������");
					// ���ն���
					abortBroadcast();
				}
			//}
		}
	}

}
