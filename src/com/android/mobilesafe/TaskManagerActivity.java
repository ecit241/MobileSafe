package com.android.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mobilesafe.domain.TaskInfo;
import com.android.mobilesafe.engine.TaskInfoProvider;
import com.android.utils.SystemInfoUtils;

public class TaskManagerActivity extends Activity {
	
	private TextView tv_process_count;
	private TextView tv_mem_info;
	private LinearLayout ll_loading;
	private TextView tv_status;
	private ListView lv_task_manager;
	private TaskManagerAdapter adapter;
	
	private List<TaskInfo> taskInfos;
	
	private List<TaskInfo> userTaskInfos;
	
	private List<TaskInfo> systemTaskInfos;
	
	private int processCount;
	private long availMem;
	private long totalMem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		ll_loading = (LinearLayout)findViewById(R.id.ll_loading);
		tv_status = (TextView)findViewById(R.id.tv_status);
		tv_process_count = (TextView)findViewById(R.id.tv_process_count);
		tv_mem_info = (TextView)findViewById(R.id.tv_mem_info);
		lv_task_manager = (ListView)findViewById(R.id.lv_task_manager);
		setTitle();
		
		
		fillData();
		
		lv_task_manager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(userTaskInfos == null || systemTaskInfos == null) return ;
				if(firstVisibleItem > userTaskInfos.size()){
					tv_status.setText("系统进程:(" + systemTaskInfos.size() + ")");
				}else{
					tv_status.setText("用户进程:(" + userTaskInfos.size() + ")"	);
				}
				
			}
		});
		
		lv_task_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0 || position == userTaskInfos.size()+1)return ;
				
				TaskInfo taskInfo;
				if(position <= userTaskInfos.size()){
					int newposition = position - 1;
					taskInfo = userTaskInfos.get(newposition);
				}else{
					int newposition = position - 1 - userTaskInfos.size() - 1;
					taskInfo = systemTaskInfos.get(newposition);
				}
				
				if(getPackageCodePath().equals(taskInfo.getPackname()))return;
				
				ViewHolder holder = (ViewHolder) view.getTag();
				if(taskInfo.isChecked()){
					taskInfo.setChecked(false);
					holder.cb_status.setChecked(false);
				}else{
					taskInfo.setChecked(true);
					holder.cb_status.setChecked(true);
				}
			}
		});
	}

	private void setTitle() {
		processCount = SystemInfoUtils.getRunningProcessCount(this);
		tv_process_count.setText("运行中的进程:" + processCount);
		availMem = SystemInfoUtils.getAvailMem(this);
		totalMem = SystemInfoUtils.getTotalMem(this);
		tv_mem_info.setText("剩余/总内存："
				+ Formatter.formatFileSize(this, availMem) + "/"
				+ Formatter.formatFileSize(this, totalMem));
	}
	
	/**
	 * 填充数据
	 */
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(){
			public void run() {
				taskInfos = TaskInfoProvider.getTaskInfos(TaskManagerActivity.this);
				System.out.println("taskinfos:" + taskInfos.size());
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo taskInfo : taskInfos) {
					if(taskInfo.isUserTask()){
						userTaskInfos.add(taskInfo);
					}else{
						systemTaskInfos.add(taskInfo);
					}
				}
				
				// 更新界面
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if(adapter == null){
							adapter = new TaskManagerAdapter();
							lv_task_manager.setAdapter(adapter);
						}else{
							adapter.notifyDataSetChanged();
						}
						setTitle();
					}
				});
			};
		}.start();
	}
	
	private class TaskManagerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			if(sp.getBoolean("showsystem", false)){
				return taskInfos.size() + 2;
			}else{
				return userTaskInfos.size() + 1;
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			TaskInfo taskInfo;
			ViewHolder holder;
			if(position == 0){
				TextView tv = new TextView(TaskManagerActivity.this);
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("用户进程:(" + userTaskInfos.size() + ")");
				return tv;
			}else if(position == (userTaskInfos.size() + 1)){
				TextView tv = new TextView(TaskManagerActivity.this);
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("系统进程:(" + systemTaskInfos.size() + ")");
				return tv;
			}else if(position <= userTaskInfos.size()){
				taskInfo = userTaskInfos.get(position - 1);
			}else{
				taskInfo = systemTaskInfos.get(position - 1- userTaskInfos.size() - 1);
			}
			
			if(convertView!=null && convertView instanceof RelativeLayout){
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}else{
				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(), R.layout.list_item_taskinfo, null);
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_task_name);
				holder.tv_memsize = (TextView) view.findViewById(R.id.tv_task_size);
				holder.cb_status = (CheckBox)view.findViewById(R.id.cb_status);
				view.setTag(holder);
			}
			
			holder.iv_icon.setImageDrawable(taskInfo.getIcon());
			holder.tv_name.setText(taskInfo.getName());
			holder.tv_memsize.setText("内存占用:" + Formatter.formatFileSize(getApplicationContext(), taskInfo.getMemsize()));
			holder.cb_status.setChecked(taskInfo.isChecked());
			if(getPackageName().equals(taskInfo.getPackname())){
				holder.cb_status.setVisibility(View.INVISIBLE);
			}else{
				holder.cb_status.setVisibility(View.VISIBLE);
			}
			
			return view;
		}
		
	}
	
	static class ViewHolder{
		TextView tv_name;
		TextView tv_memsize;
		ImageView iv_icon;
		CheckBox cb_status;
	}
	
	/**
	 * 选中全部
	 * @param view
	 */
	public void selectAll(View view){
		for (TaskInfo info : taskInfos) {
			if(getPackageName().equals(info.getPackname()))
				continue;
			info.setChecked(true);
		}
		
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 选中相反的
	 * @param view
	 */
	public void selectOppositve(View view){
		for (TaskInfo info : taskInfos) {
			if(getPackageName().equals(info.getPackname()))
				continue;
			info.setChecked(!info.isChecked());
		}
		
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 清理
	 * @param view
	 */
	public void killSelect(View view){
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int count = 0;
		long saveMem = 0;
		List<TaskInfo> killTaskInfos = new ArrayList<TaskInfo>();
		for (TaskInfo info : taskInfos) {
			if(getPackageName().equals(info.getPackname()))
				continue;
			if(info.isChecked()){  // 被勾选的进程,将被杀死
				am.killBackgroundProcesses(info.getPackname());
				if(info.isUserTask()){
					userTaskInfos.remove(info);
				}else{
					systemTaskInfos.remove(info);
				}
				count ++;
				saveMem += info.getMemsize();
				killTaskInfos.add(info);
			}
			
		}
		// 刷新界面
		taskInfos.removeAll(killTaskInfos);
		adapter.notifyDataSetChanged();
		Toast.makeText(this, "杀死了" + count + "进程,释放了" + Formatter.formatFileSize(this, saveMem), Toast.LENGTH_LONG).show();
		processCount -= count;
		availMem += saveMem;
		tv_process_count.setText("运行中的进程:" + processCount);
		tv_mem_info.setText("剩余/总内存："
				+ Formatter.formatFileSize(this, availMem) + "/"
				+ Formatter.formatFileSize(this, totalMem));
	}
	
	/**
	 * 进入设置
	 * @param view
	 */
	public void enterSetting(View view){
		Intent intent = new Intent(this, TaskSettingActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		adapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
