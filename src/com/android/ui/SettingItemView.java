package com.android.ui;

import com.android.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * �Զ�����Ͽؼ�
 * @author Administrator
 *
 */
public class SettingItemView extends RelativeLayout {
	
	private CheckBox cb_status;
	private TextView tv_desc;
	private TextView tv_title;
	
	private static final String namespace = "http://schemas.android.com/apk/res/com.android.mobilesafe";
	
	private String desc_on;
	private String desc_off;
	/**
	 * ��ʼ�������ļ�
	 * @param context
	 */
	private void iniView(Context context) {
		View.inflate(context, R.layout.setting_item_view, this);
		cb_status = (CheckBox)findViewById(R.id.cb_status);
		tv_desc = (TextView)findViewById(R.id.tv_desc);
		tv_title = (TextView)findViewById(R.id.tv_title);
	}
	
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}

	/**
	 * ���������������췽��,�����ļ�ʱ�����
	 * @param context
	 * @param attrs
	 */
	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
		String title = attrs.getAttributeValue(namespace, "title");
		desc_on = attrs.getAttributeValue(namespace, "desc_on");
		desc_off = attrs.getAttributeValue(namespace, "desc_off");
		
		tv_title.setText(title);
		setChecked(isChecked());
	}

	public SettingItemView(Context context) {
		super(context);
		iniView(context);
	}
	
	/**
	 * У����Ͽؼ��Ƿ�ѡ��
	 */
	public boolean isChecked(){
		return cb_status.isChecked();
	}
	
	/**
	 * ������Ͽؼ�״̬
	 */
	public void setChecked(boolean checked){
		if(checked){
			tv_desc.setText(desc_on);
		}else{
			tv_desc.setText(desc_off);
		}
		cb_status.setChecked(checked);
	}
	
	/**
	 * ������Ͽؼ���������Ϣ
	 */
	public void setDesc(String desc){
		tv_desc.setText(desc);
	}
}