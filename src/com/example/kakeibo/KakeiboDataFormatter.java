package com.example.kakeibo;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.R.integer;
import android.annotation.SuppressLint;

public class KakeiboDataFormatter {
	private static KakeiboDataFormatter dataFormatter = null;
	public static int TODAY = -1;
	public static int ANY = -2;
	
	private KakeiboDataFormatter(){
		
	}
	
	public static KakeiboDataFormatter getInstance(){
		if(dataFormatter == null){
			return dataFormatter = new KakeiboDataFormatter();
		}
		else{
			return dataFormatter;
		}
	}
	
	/**
	 * 10000 -> 10,000�~ �̂悤�Ɂ@�J���}�Ɖ~�������`�ɂ���
	 * @param price
	 * @return
	 */
	public static String priceFormat(int price){
		NumberFormat priceformat = NumberFormat.getNumberInstance();
		return priceformat.format(price) + "�~";
	}
	
	
	/**
	 * yy-MM-dd�̌`�Ƀt�H�[�}�b�g�@������TODAY���Ƃ��̕����������̔N����, ANY���Ɛ��K�\���Ŏg��__�ɂȂ�
	 * @param year, month, day
	 * @return 
	*/
	public static String dateFormat(int year, int month, int day){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		String Y = year ==ANY ? "____" : year ==TODAY ? String.format("%04d", calendar.get(Calendar.YEAR))   : String.format("%04d", year);
		String M = month==ANY ? "__"   : month==TODAY ? String.format("%02d", calendar.get(Calendar.MONTH)+1): String.format("%02d", month);
		String D = day  ==ANY ? "__"   : day  ==TODAY ? String.format("%02d", calendar.get(Calendar.DATE))   : String.format("%02d", day);
    	return Y+"-"+M+"-"+D;
    }
	
	public static String dateFormat_Y(int year){
		return dateFormat(year, KakeiboDataFormatter.ANY, KakeiboDataFormatter.ANY);
	}
	
	public static String dateFormat_YM(int year, int month){
		return dateFormat(year, month, KakeiboDataFormatter.ANY);
	}
	
	/**
	 * ������Date�^�ł� yyyy-MM-d�ϊ�
	 * @param d
	 * @return
	 */
	public static String dateFormat(Date d){
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		return sdf1.format(d.getTime());
	}
	
	/**
	 * yyyy-mm-dd -> yyyy�Nmm��dd���ɕϊ�
	 */
	public static String dateFormat(final String d) {
		String[] dateStr = d.split("-");
		String[] suffix = {"�N", "��", "��"};
		String date = new String();
		
		for(int i=0; i<dateStr.length; i++) {
			date += dateStr[i] + suffix[i];
		}
		
		return date;
	}
	
	/**
	 * date�^ -> yyyy�Nmm��dd���ϊ�
	 * @param d
	 * @return
	 */
	public static String dateFormatYMD(Date d){
		return dateFormat(dateFormat(d));
	}
	
	
	
	

}
