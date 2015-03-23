package com.android.mobilesafe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mobilesafe.db.dao.BlackNumberDao;

public class CallSmsSafeActivity extends Activity {

	private ListView lv_callsms_safe;
	private List<Map<String, String>> infos;
	private BlackNumberDao dao;
	private CallSmsSafeAdapter adapter;
	private LinearLayout ll_loading;
	private int offset = 0;
	private int maxnumber = 10;
	private int count = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_callsms_safe = (ListView)findViewById(R.id.lv_call_sms_safe);
		dao = new BlackNumberDao(this);
		count = dao.getCount();
		System.out.println("count = "+count);
		fillData();
		
		// listview注册一个滚动事件监听器
		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			
			// 当滚动状态发生变化
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: // 空闲状态
					// 判断当前Listivew的位置
					// 获取最后一个可见条目在集合的位置
					int lastPostion = lv_callsms_safe.getLastVisiblePosition();
					// 集合里面有10个item 位置从0开始的最后一个条目的位置是9
					if(lastPostion == (infos.size()-1)){
						offset += maxnumber;
						fillData();
					}
					break;

				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: // 手指触摸滚动
					break;
					
				case OnScrollListener.SCROLL_STATE_FLING:  // 惯性滑行状态
					
					break;
				}
			}
			
			// 滚动的时候调用
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		lv_callsms_safe.setOnItemLongClickListener(new OnItemLongClickListener() {

			private CheckBox cb_phone;
			private CheckBox cb_sms;
			private CheckBox cb_all;
			private Button bt_ok;
			private Button bt_cancel;
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
				final AlertDialog dialog = builder.create();
				View contentView = View.inflate(getApplicationContext(), R.layout.dialog_chang_blockmode, null);
				
				dialog.setView(contentView, 0, 0, 0, 0);
				dialog.setCancelable(false);
				dialog.show();
				
				cb_phone = (CheckBox)contentView.findViewById(R.id.cb_phone);
				cb_sms = (CheckBox)contentView.findViewById(R.id.cb_sms);
				cb_all = (CheckBox)contentView.findViewById(R.id.cb_all);
				
				bt_cancel = (Button)contentView.findViewById(R.id.cancel);
				bt_ok = (Button)contentView.findViewById(R.id.ok);
				
				cb_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked){
							cb_phone.setChecked(true);
							cb_sms.setChecked(true);
						}else{
							cb_phone.setChecked(false);
							cb_sms.setChecked(false);
						}
					}
				});
				
				bt_cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
				bt_ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String mode = null;
						if(!cb_phone.isChecked() && !cb_sms.isChecked() && !cb_all.isChecked()){
							Toast.makeText(getApplicationContext(), "请选择拦截模式", Toast.LENGTH_SHORT).show();
							return ;
						}
						if(cb_phone.isChecked() && cb_sms.isChecked() || cb_all.isChecked()){
							// 全部拦截
							mode = "3";
						}else if(cb_phone.isChecked()){
							// 电话拦截
							mode = "1";
						}else if(cb_sms.isChecked()){
							// 短信拦截
							mode = "2";
						}
						// 更新数据
						dao.update(infos.get(position).get("num"), mode);
						infos.get(position).put("mode", mode);
						// 通知listView数据适配器更新数据
						adapter.notifyDataSetChanged();
						dialog.dismiss();
					}
				});
				return true;
			}
		});
	}

	private void fillData() {
		if(infos == null || infos.size() != count){
			ll_loading.setVisibility(View.VISIBLE);
		}
		new Thread(){
			public void run() {
				if(infos == null){
					infos = dao.findPart(offset, maxnumber);
				}else{
					if(infos.size() != count){
						infos.addAll(dao.findPart(offset, maxnumber));
					}
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(infos.size() <= count){
							ll_loading.setVisibility(View.INVISIBLE);
						
							if(adapter == null){
								adapter = new CallSmsSafeAdapter();
								lv_callsms_safe.setAdapter(adapter);
							}else{
								adapter.notifyDataSetChanged();
							}
						}
					}
				});
			};
		}.start();
	}
	
	private class CallSmsSafeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		// 有多少个条目被显示,就调用 该函数多少次
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final View view;
			ViewHolder holder = null;
			// 1.减少内在中view对象创建的个数
			if(convertView == null){
				holder = new ViewHolder();
				System.out.println("创建新的view对象");
				view= View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
				holder.tv_number = (TextView) view.findViewById(R.id.tv_black_number);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_block_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				
				// 存放在记事本
				view.setTag(holder);
			}else{
				System.out.println("历史缓存view对象");
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			

			holder.tv_number.setText(infos.get(position).get("num"));
			String mode = infos.get(position).get("mode");
			if("1".equals(mode)){
				holder.tv_mode.setText("电话拦截");
			}else if("2".equals(mode)){
				holder.tv_mode.setText("短信拦截");
			}else if("3".equals(mode)){
				holder.tv_mode.setText("全部拦截");
			}
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("提示");
					builder.setMessage("确定要删除这条记录么?");
					builder.setCancelable(false);
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dao.delete(infos.get(position).get("num"));
							
							// 更新界面
							infos.remove(position);
							// 通知listView数据适配器更新数据
							adapter.notifyDataSetChanged();
							dialog.dismiss();
						}
					});
					builder.setNegativeButton("取消", null);
					builder.show();
					
				}
			});
			
			return view;
		}
		
	}
	
	private EditText et_blacknumber;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private CheckBox cb_all;
	private Button bt_ok;
	private Button bt_cancel;
	/**
	 * 添加黑名单记录
	 * @param view
	 */
	public void addBlackNumber(View view){
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create();
		View contentView = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		dialog.setView(contentView, 0, 0, 0, 0);
		dialog.setCancelable(false);
		dialog.show();
		
		et_blacknumber = (EditText) contentView.findViewById(R.id.et_blacknumber);
		cb_phone = (CheckBox)contentView.findViewById(R.id.cb_phone);
		cb_sms = (CheckBox)contentView.findViewById(R.id.cb_sms);
		cb_all = (CheckBox)contentView.findViewById(R.id.cb_all);
		
		bt_cancel = (Button)contentView.findViewById(R.id.cancel);
		bt_ok = (Button)contentView.findViewById(R.id.ok);
		
		cb_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					cb_phone.setChecked(true);
					cb_sms.setChecked(true);
				}else{
					cb_phone.setChecked(false);
					cb_sms.setChecked(false);
				}
			}
		});
		
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				String mode = null;
				if(TextUtils.isEmpty(blacknumber)){
					Toast.makeText(getApplicationContext(), "黑名单号码不能为空", Toast.LENGTH_SHORT).show();
					return ;
				}
				if(!cb_phone.isChecked() && !cb_sms.isChecked() && !cb_all.isChecked()){
					Toast.makeText(getApplicationContext(), "请选择拦截模式", Toast.LENGTH_SHORT).show();
					return ;
				}
				if(cb_phone.isChecked() && cb_sms.isChecked() || cb_all.isChecked()){
					// 全部拦截
					mode = "3";
				}else if(cb_phone.isChecked()){
					// 电话拦截
					mode = "1";
				}else if(cb_sms.isChecked()){
					// 短信拦截
					mode = "2";
				}
				// 数据添加到数据库
				dao.add(blacknumber, mode);
				// 更新listView内容
				Map<String, String> map = new HashMap<String, String>();
				map.put("num", blacknumber);
				map.put("mode", mode);
				infos.add(0, map);
				// 通知listView数据适配器更新数据
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
	}
	
	/**
	 * view对象的容器
	 * 记录控件的内在地址
	 * 相当于一个记事本
	 * @author Administrator
	 *
	 */
	class ViewHolder{
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}
}
