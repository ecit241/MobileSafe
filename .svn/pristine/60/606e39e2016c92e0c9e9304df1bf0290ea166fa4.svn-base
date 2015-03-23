package com.android.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	public static String md5Password(String password){
		
		// 得到一个信息摘要器
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			
			byte [] bytes =  digest.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			for(byte b: bytes){
				int number = b & 0xff;//加盐
				String hex = Integer.toHexString(number);
				if(hex.length()==1){
					buffer.append("0");
				}
				buffer.append(hex);
			}
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}
}
