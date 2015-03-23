package com.android.ui;

import com.android.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义组合控件
 * @author Administrator
 *
 */
public class SettingClickView extends RelativeLayout {
	
	private TextView tv_desc;
	private TextView tv_title;
	
	// private static final String namespace =
	// "http://schemas.android.com/apk/res/com.android.mobilesafe";
	//
	// private String desc_on;
	// private String desc_off;
	/**
	 * 初始化布局文件
	 * @param context
	 */
	private void iniView(Context context) {
		View.inflate(context, R.layout.setting_click_view, this);
		tv_desc = (TextView)findViewById(R.id.tv_desc);
		tv_title = (TextView)findViewById(R.id.tv_title);
	}
	
	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}

	/**
	 * 带有两个参数构造方法,布局文件时候调用
	 * @param context
	 * @param attrs
	 */
	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
		// String title = attrs.getAttributeValue(namespace, "title");
		// desc_on = attrs.getAttributeValue(namespace, "desc_on");
		// desc_off = attrs.getAttributeValue(namespace, "desc_off");
		//
		// tv_title.setText(title);
	}

	public SettingClickView(Context context) {
		super(context);
		iniView(context);
	}
	
	/**
	 * 设置组合控件标题
	 */
	public void setTitle(String title){
		tv_title.setText(title);
	}
	
	/**
	 * 设置组合控件的描述信息
	 */
	public void setDesc(String desc){
		tv_desc.setText(desc);
	}
}
