package com.example.kakeibo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class CalendarView extends SimpleAlertDialog {
	public static boolean THIS_WEEK = true;
	public static boolean OTHER     = false;
	
	private static int year  = KakeiboDataFormatter.TODAY;
	private static int month = KakeiboDataFormatter.TODAY;
	private static boolean state = THIS_WEEK; 
	private Spinner calendar_year;
	private Spinner calendar_month;
	
	protected CalendarView(DialogHolderActivity context) {
		super(context);
		setLayout(R.layout.calendar_window);
		setTitle("ÉJÉåÉìÉ_Å[");
		
		KakeiboDBHelper helper = new KakeiboDBHelper(context);
		SQLiteDatabase db = helper.getReadableDatabase();
		int nm[] = KakeiboDBHelper.getMinMaxYear(db);
		System.out.println(nm[0] + "   " + nm[1]);
		ArrayList<String> yearList = new ArrayList<String>();
		for(Integer i=nm[0]; i<=nm[1];i++){
			yearList.add(i.toString());
			System.out.println(yearList.get(i-nm[0]));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item,yearList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		calendar_year = (Spinner)getLayout().findViewById(R.id.calendar_year);
		calendar_year.setAdapter(adapter);
		calendar_month = (Spinner)getLayout().findViewById(R.id.calendar_month);
	}
	
	public CalendarView setOnClickListener(DialogInterface.OnClickListener listener){
		mBuilder.setPositiveButton("OK", listener);
		return this;
	}
	
	public void create(){
		mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				RadioGroup radio = (RadioGroup)getLayout().findViewById(R.id.radio_group);
				switch(radio.getCheckedRadioButtonId()){
				case R.id.this_week:
					state = THIS_WEEK;
					break;
				case R.id.this_month:
					state = OTHER;
					year = KakeiboDataFormatter.TODAY;
					month = KakeiboDataFormatter.TODAY;
					break;
				case R.id.this_year:
					state = OTHER;
					year = KakeiboDataFormatter.TODAY;
					month = KakeiboDataFormatter.ANY;
					break;
				case R.id.user_set:
					state = OTHER;
					year = new Integer(calendar_year.getSelectedItem().toString());
					if(calendar_month.getSelectedItemId() == 0)
						month = KakeiboDataFormatter.ANY;
					else
						month = new Integer(calendar_month.getSelectedItem().toString());					
					break;
				}
				mContext.DialogOK();
			}
		})
		.show();
	}
	
	public static boolean getState(){
		return state;
	}
	public static int getYear(){
		return year;
	}
	
	public static int getMonth(){
		return month;
	}
	
}
