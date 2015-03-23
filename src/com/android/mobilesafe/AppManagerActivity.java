package com.android.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mobilesafe.db.dao.ApplockDao;
import com.android.mobilesafe.domain.AppInfo;
import com.android.mobilesafe.engine.AppInfoProvider;
import com.android.utils.DensityUtil;

public class AppManagerActivity extends Activity implements OnClickListener{

	private TextView tv_avail_rom;
	private TextView tv_avail_sd;
	private LinearLayout ll_loading;
	private ListView lv_app_manager;
	
	/**
	 * ����Ӧ�ó������Ϣ
	 */
	private List<AppInfo> appInfos;
	
	private List<AppInfo> userAppInfos;
	
	private List<AppInfo> systemAppInfos;
	
	// ��ǰ����״̬
	private TextView tv_status;
	
	/**
	 * ������������
	 */
	private PopupWindow popupWindow;
	
	private AppManagerAdapter adapter;
	
	private LinearLayout ll_start; //����
	private LinearLayout ll_share; //����
	private LinearLayout ll_uninstall; // ж��
	
	/**
	 * ��Ҫ��click �¼��ṩ����
	 */
	private AppInfo appInfo;
	
	
	private ApplockDao dao;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		dao = new ApplockDao(this);
		tv_avail_rom = (TextView)findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView)findViewById(R.id.tv_avail_sd);
		lv_app_manager = (ListView)findViewById(R.id.lv_app_manager);
		ll_loading = (LinearLayout)findViewById(R.id.ll_loading);
		tv_status = (TextView)findViewById(R.id.tv_status);
		
		
		long sdsize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
		long romsize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());
		
		tv_avail_sd.setText("SD�����ÿռ�: " + Formatter.formatFileSize(this, sdsize));
		tv_avail_rom.setText("�ֻ����ÿռ�: " + Formatter.formatFileSize(this, romsize));
	
		
		ll_loading.setVisibility(View.VISIBLE);
		fillData();
		
		// listview��������
		lv_app_manager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			// ��һ���ɼ���Ŀ��listview�е�λ��
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dimssPopupWindow();
				if(userAppInfos == null || systemAppInfos == null) return ;
				if(firstVisibleItem > userAppInfos.size()){
					tv_status.setText("ϵͳ����:(" + systemAppInfos.size() + ")");
				}else{
					tv_status.setText("�û�����:(" + userAppInfos.size() + ")"	);
				}
			}
		});
		
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0 || position == userAppInfos.size()+1)return ;
				
				if(position <= userAppInfos.size()){
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				}else{
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
				}
				//System.out.println(appInfo.getPackname());
				// �Ѿɵĵ������ڹرյ�
				dimssPopupWindow();
				
				View contentView = View.inflate(AppManagerActivity.this, R.layout.popup_app_item, null);
				ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
				ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
				
				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				
				
				popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				// ����Ч���Ĳ��ű���Ҫ�����б�����ɫ
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				
				int[] location = new int[2];
				view.getLocationInWindow(location);
				// �ڴ����������õĿ���ֵ��������
				int dip = 60;
				int px = DensityUtil.dip2px(AppManagerActivity.this, dip);
				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, px, location[1]);
			
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(500);
				
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(500);
				
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(aa);
				set.addAnimation(sa);
				contentView.startAnimation(set);
			}
		});
		
		lv_app_manager.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0 || position == userAppInfos.size()+1)
					return true;
				
				if(position <= userAppInfos.size()){
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				}else{
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				// �ж���Ŀ�Ƿ���ڳ��������ݿ�����
				if(dao.find(appInfo.getPackname())){
					// �������
					dao.delete(appInfo.getPackname());
					holder.iv_status.setImageResource(R.drawable.unlock);
				}else{
					dao.add(appInfo.getPackname());
					holder.iv_status.setImageResource(R.drawable.lock);
				}
				return true;
			}
		});
	}


	private void fillData() {
		new Thread(){
			public void run() {
				appInfos = AppInfoProvider.getAppInfo(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo info : appInfos) {
					if(info.isUserApp()){
						userAppInfos.add(info);
					}else{
						systemAppInfos.add(info);
					}
				}
				
				// ����listview����������
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if(adapter == null){
							adapter = new AppManagerAdapter();
							lv_app_manager.setAdapter(adapter);
						}else{
							adapter.notifyDataSetChanged();
						}
					}
				});
			};
		}.start();
	}
	
	
	private class AppManagerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			//return appInfos.size();
			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			AppInfo appInfo;
			if (position == 0) {// ��ʾ�����ó����ж��ٸ���С��ǩ
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("�û�����(" + userAppInfos.size() + ")");
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ����(" + systemAppInfos.size() + ")");
				return tv;
			} else if (position <= userAppInfos.size()) {// �û�����
				int newposition = position - 1;// ��Ϊ����һ��textview���ı�ռ����λ��
				appInfo = userAppInfos.get(newposition);
			} else {// ϵͳ����
				int newposition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newposition);
			}
			View view;
			ViewHolder holder;
			
//			if(position < userAppInfos.size()){
//				appInfo = userAppInfos.get(position);
//			}else{// ��Щλ��������ϵͳ�����
//				int newposition = position - userAppInfos.size();
//				appInfo = systemAppInfos.get(newposition);
//			}
			
			if (convertView != null && convertView instanceof RelativeLayout) {
				// ������Ҫ����Ƿ�Ϊ�գ���Ҫ�ж��Ƿ��Ǻ��ʵ�����ȥ����
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_appinfo, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				holder.tv_location = (TextView) view.findViewById(R.id.tv_app_location);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.iv_status = (ImageView)view.findViewById(R.id.iv_status);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				holder.tv_location.setText("�ֻ��ڴ�   uid:" + appInfo.getUid());
			} else {
				holder.tv_location.setText("�ⲿ�洢   uid:" + appInfo.getUid());
			}
			
			if(dao.find(appInfo.getPackname())){
				holder.iv_status.setImageResource(R.drawable.lock);
			}else{
				holder.iv_status.setImageResource(R.drawable.unlock);
			}

			return view;
		}
		
	}
	
	static class ViewHolder{
		TextView tv_name;
		TextView tv_location;
		ImageView iv_icon;
		ImageView iv_status;
	}
	
	
	/**
	 * ��ȡĳ��Ŀ¼�Ŀ��ÿռ�
	 * @param path
	 * @return
	 */
	private long getAvailSpace(String path){
		StatFs statFs = new StatFs(path);
		//long blockCount = statFs.getBlockCount();   // ��ȡ��������
		long blockSize = statFs.getBlockSize();     // ��ȡ������С
		
		long availBlocks = statFs.getAvailableBlocks();   // ���÷�������
		long availSpace = blockSize *  availBlocks;
		
		return availSpace;
	}


	private void dimssPopupWindow() {
		if(popupWindow != null && popupWindow.isShowing()){
			popupWindow.dismiss();
			popupWindow = null;
		}
	}
	
	@Override
	protected void onDestroy() {
		dimssPopupWindow();
		super.onDestroy();
	}


	/**
	 * ���������Ӧ����¼�
	 */
	@Override
	public void onClick(View v) {
		dimssPopupWindow();
		switch (v.getId()) {
		case R.id.ll_start:
			startApplication();
			break;

		case R.id.ll_uninstall:
			if(!appInfo.isUserApp()){
				Toast.makeText(AppManagerActivity.this, "����rootȨ�޲���ж��ϵͳӦ��", Toast.LENGTH_LONG).show();
				return ;
			}
			uninstallApplication();
			//Runtime.getRuntime().exec("");  //rootȨ��
			break;
			
		case R.id.ll_share:
			shareApplication();
			break;
		}
	}


	private void uninstallApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appInfo.getPackname()));
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ˢ�½���
		fillData();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ����Ӧ�ó���
	 */
	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "�Ƽ���ʹ��һ������,���ֽ�:" + appInfo.getName());
		startActivity(intent);
	}


	/**
	 * ����Ӧ�ó���
	 */
	private void startApplication() {
		// ���������������
		PackageManager pm = getPackageManager();
		// Intent intent = new Intent();
		// intent.setAction("android.intent.action.MAIN");
		// intent.addCategory("android.intent.category.LAUNCHER");
		// // ��ѯ�ֻ������п���������activity
		// List<ResolveInfo> infos = pm.queryIntentActivities(intent,
		// PackageManager.GET_INTENT_FILTERS);
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if(intent != null){
			startActivity(intent);
		}else{
			Toast.makeText(this, "��ǰӦ�ò��ܱ�����", Toast.LENGTH_SHORT).show();
		}
	}
}