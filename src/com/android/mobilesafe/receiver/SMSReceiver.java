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
		
		// 接收短信
		Object[] objs = (Object[])intent.getExtras().get("pdus");
		
		for (Object b : objs) {
			// 具体的某一条短信
			SmsMessage sms = SmsMessage.createFromPdu((byte[])b);
			// 发送人
			String sender = sms.getOriginatingAddress();
			String safenumber = sp.getString("safenumber", "");
		//	if(sender.contains(safenumber)){
				// 内容
				String body = sms.getMessageBody();
				if("#*location*#".equals(body)){
					// 得到手机GPS
					System.out.println("得到手机GPS");
					// 启动服务
					Intent i = new Intent(context, GPSService.class);
					context.startService(i);
					SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
					String lastlocation = sp.getString("lastlocation", null);
					if(TextUtils.isEmpty(lastlocation)){
						SmsManager.getDefault().sendTextMessage(sender, null, "getting location ....", null, null);
					}else{
						SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
					}
					
					// 屏闭短信
					abortBroadcast();
				}else if("#*alarm*#".equals(body)){
					// 播放报警音乐
					System.out.println("播放报警音乐");
					MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
					player.setLooping(false);
					player.setVolume(1.0f, 1.0f);
					player.start();
					
					abortBroadcast();
				}else if("#*wipedata*#".equals(body)){
					// 远程清除数据
					System.out.println("远程清除数据");
					// 屏闭短信
					abortBroadcast();
				}else if("#*lockscreen*#".equals(body)){
					// 远程锁屏
					System.out.println("远程锁屏");
					// 屏闭短信
					abortBroadcast();
				}
			//}
		}
	}

}
