package com.android.mobilesafe.service;

import java.io.InputStream;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service {

	// λ�÷���
	private LocationManager lm;
	private MyLocationListener listener;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		listener = new MyLocationListener();
		// ע�����λ�÷���
		// ��λ���ṩ����������
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		// ���ò���ϸ����
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);//����Ϊ��󾫶�
		// criteria.setAltitudeRequired(false);//��Ҫ�󺣰���Ϣ
		// criteria.setBearingRequired(false);//��Ҫ��λ��Ϣ
		// criteria.setCostAllowed(true);//�Ƿ�������
		// criteria.setPowerRequirement(Criteria.POWER_LOW);//�Ե�����Ҫ��

		String proveder = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(proveder, 0, 0, listener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// ȡ������λ�÷���
		lm.removeUpdates(listener);
		listener = null;
	}
	
	class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			String longitude = "j: " + location.getLongitude() + "\n";
			String latitude = "w: " + location.getLatitude() + "\n";
			String accuracy = "a: " + location.getAccuracy() + "\n";
			
			// �ѱ�׼��GPS����ת���ɻ�������
			InputStream is;
			
			try {
				is = getAssets().open("axisoffset.dat");
				ModifyOffset offset = ModifyOffset.getInstance(is);
				PointDouble point = offset.s2c(new PointDouble(location.getLongitude(), location.getLatitude()));
				longitude = "j:" + point.x + "\n";
				latitude = "w:" + point.y + "\n";
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// �����Ÿ���ȫ����
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			String value = longitude + latitude + accuracy;
			editor.putString("lastlocation", value);
			editor.commit();
		}

		/**
		 * ��״̬�����ı��ʱ��ص� ����--�ر�, �ر�--����
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * ĳһ��λ���ṩ�߿�����,�ͻص�
		 */
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * ĳһ��λ���ṩ�߲���ʹ����,�ͻص�
		 */
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	} 

}
