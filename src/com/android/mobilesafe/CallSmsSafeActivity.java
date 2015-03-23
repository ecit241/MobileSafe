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
		
		// listviewע��һ�������¼�������
		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			
			// ������״̬�����仯
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: // ����״̬
					// �жϵ�ǰListivew��λ��
					// ��ȡ���һ���ɼ���Ŀ�ڼ��ϵ�λ��
					int lastPostion = lv_callsms_safe.getLastVisiblePosition();
					// ����������10��item λ�ô�0��ʼ�����һ����Ŀ��λ����9
					if(lastPostion == (infos.size()-1)){
						offset += maxnumber;
						fillData();
					}
					break;

				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: // ��ָ��������
					break;
					
				case OnScrollListener.SCROLL_STATE_FLING:  // ���Ի���״̬
					
					break;
				}
			}
			
			// ������ʱ�����
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
							Toast.makeText(getApplicationContext(), "��ѡ������ģʽ", Toast.LENGTH_SHORT).show();
							return ;
						}
						if(cb_phone.isChecked() && cb_sms.isChecked() || cb_all.isChecked()){
							// ȫ������
							mode = "3";
						}else if(cb_phone.isChecked()){
							// �绰����
							mode = "1";
						}else if(cb_sms.isChecked()){
							// ��������
							mode = "2";
						}
						// ��������
						dao.update(infos.get(position).get("num"), mode);
						infos.get(position).put("mode", mode);
						// ֪ͨlistView������������������
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

		// �ж��ٸ���Ŀ����ʾ,�͵��� �ú������ٴ�
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final View view;
			ViewHolder holder = null;
			// 1.����������view���󴴽��ĸ���
			if(convertView == null){
				holder = new ViewHolder();
				System.out.println("�����µ�view����");
				view= View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
				holder.tv_number = (TextView) view.findViewById(R.id.tv_black_number);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_block_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				
				// ����ڼ��±�
				view.setTag(holder);
			}else{
				System.out.println("��ʷ����view����");
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			

			holder.tv_number.setText(infos.get(position).get("num"));
			String mode = infos.get(position).get("mode");
			if("1".equals(mode)){
				holder.tv_mode.setText("�绰����");
			}else if("2".equals(mode)){
				holder.tv_mode.setText("��������");
			}else if("3".equals(mode)){
				holder.tv_mode.setText("ȫ������");
			}
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
					builder.setTitle("��ʾ");
					builder.setMessage("ȷ��Ҫɾ��������¼ô?");
					builder.setCancelable(false);
					builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dao.delete(infos.get(position).get("num"));
							
							// ���½���
							infos.remove(position);
							// ֪ͨlistView������������������
							adapter.notifyDataSetChanged();
							dialog.dismiss();
						}
					});
					builder.setNegativeButton("ȡ��", null);
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
	 * ���Ӻ�������¼
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
					Toast.makeText(getApplicationContext(), "���������벻��Ϊ��", Toast.LENGTH_SHORT).show();
					return ;
				}
				if(!cb_phone.isChecked() && !cb_sms.isChecked() && !cb_all.isChecked()){
					Toast.makeText(getApplicationContext(), "��ѡ������ģʽ", Toast.LENGTH_SHORT).show();
					return ;
				}
				if(cb_phone.isChecked() && cb_sms.isChecked() || cb_all.isChecked()){
					// ȫ������
					mode = "3";
				}else if(cb_phone.isChecked()){
					// �绰����
					mode = "1";
				}else if(cb_sms.isChecked()){
					// ��������
					mode = "2";
				}
				// �������ӵ����ݿ�
				dao.add(blacknumber, mode);
				// ����listView����
				Map<String, String> map = new HashMap<String, String>();
				map.put("num", blacknumber);
				map.put("mode", mode);
				infos.add(0, map);
				// ֪ͨlistView������������������
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
	}
	
	/**
	 * view���������
	 * ��¼�ؼ������ڵ�ַ
	 * �൱��һ�����±�
	 * @author Administrator
	 *
	 */
	class ViewHolder{
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}
}