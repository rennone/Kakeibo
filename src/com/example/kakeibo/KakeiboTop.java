package com.example.kakeibo;

import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.afree.chart.AFreeChart;
import org.afree.chart.ChartFactory;
import org.afree.data.general.DefaultPieDataset;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_kakeibo_top)
public class KakeiboTop extends DialogHolderActivity {
	private static final int REGION_WEEK = 0;
	private static final String[] REGION_NAME = {"今週の家計簿", "今月の家計簿", "今年の家計簿"};
	private int now_region = REGION_WEEK;
	private Calendar calendar;
	private KakeiboDBHelper helper;
	
	@ViewById
	TextView SumPrice;
	
	@ViewById(R.id.chart_view)
	ChartView chartView;

	@AfterViews
	void Initialize(){
		calendar = Calendar.getInstance();
		helper = new KakeiboDBHelper(this);
		draw();
	}

	private String condition(){
		String condition = "";
		calendar.setTime(new Date());
		if(CalendarView.getState() == CalendarView.THIS_WEEK){
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			condition = "date(Date) >= date(" + "\'" + KakeiboDataFormatter.dateFormat(calendar.getTime()) + "\'" + ") ";
			calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			condition +="AND date(Date) <= date(" + "\'" + KakeiboDataFormatter.dateFormat(calendar.getTime()) + "\'" + ") ";
		}
		else{
			condition = " Date like " + "\'" + KakeiboDataFormatter.dateFormat_YM(CalendarView.getYear(), CalendarView.getMonth())+ "\'"; 
		}
		
		return condition;
	}
	
	private Integer getTotalCost(){
		try{
			SQLiteDatabase db = helper.getReadableDatabase();
			System.out.println(condition());
			Cursor cursor = db.rawQuery(
				"select sum(" + KakeiboDBHelper.KAKEIBO_TABLE_PRICE + ")" +
				" from " + KakeiboDBHelper.KAKEIBO_TABLE +
				" where " + condition() + ";"
				,null);
			boolean isEof = cursor.moveToFirst();
			Integer total = -1;
			if(isEof){
				total = cursor.getString(0) == null ? 0 : cursor.getInt(0);
			}
			cursor.close();
			db.close();
			return total;
		}catch(Exception e){
			return null;
		}
	}
	
	private HashMap<String, Integer> getCategoryCost(SQLiteDatabase db){
		HashMap<String, Integer> hash = new HashMap<String, Integer>();
		Cursor cursor = db.rawQuery(
				"select " + KakeiboDBHelper.KAKEIBO_TABLE_CATEGORY + ", sum( " + KakeiboDBHelper.KAKEIBO_TABLE_PRICE + ") " +
				"from Kakeibo " +
				"where " + condition() + " group by " + KakeiboDBHelper.KAKEIBO_TABLE_CATEGORY + ";", null);
		Boolean isEof = cursor.moveToFirst();
		while(isEof){
			hash.put(cursor.getString(0), cursor.getInt(1));
			isEof = cursor.moveToNext();
		}
		return hash;
	}

	private void setGraphData(){
		try {
			SQLiteDatabase db = helper.getReadableDatabase();
			DefaultPieDataset dataset = new DefaultPieDataset();
			Map<String, Integer> map = getCategoryCost(db);
			for (Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator(); it.hasNext();) {
			    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)it.next();
			    dataset.setValue((Comparable<String>)entry.getKey(), (Number)entry.getValue());
			}
		     //AFreeChartの作成
	        AFreeChart chart = ChartFactory.createPieChart("",dataset,false,false,false);
	        chartView.setChart(chart);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	@Click({R.id.detail_button})
	void detail(){
		Intent intent = new Intent(this, KakeiboDetail_.class);
		startActivity(intent);
	}
	*/
	//タイトルバーの表示
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	private boolean draw(){
		setTitle(REGION_NAME[now_region]);
		SumPrice.setText("合計 " + KakeiboDataFormatter.priceFormat(getTotalCost()) );
		setGraphData();
		chartView.invalidate();
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		System.out.println("onOptionItemSelected");
		switch(item.getItemId()){
		case R.id.action_change_calendar:
			new CalendarView(this).create();
			break;
		case R.id.action_recording:
			startActivity(new Intent(this, KakeiboHandle_.class));
			break;
		case R.id.action_settings:
			new Setting(this).create();
			break;
		case R.id.action_search:
			startActivity(new Intent(this, KakeiboDetail_.class));
			break;
		case R.id.action_backup:
			backup();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	void backup(){
		SQLiteDatabase db = helper.getReadableDatabase();
		try{
			//拡張子は.hbf と定義
			String file_name = KakeiboDataFormatter.dateFormat(new Date()); //今日の日付をファイル名にする
			@SuppressWarnings("deprecation")
			FileOutputStream mOutput = openFileOutput(file_name + ".hbf", MODE_WORLD_READABLE);
			
			Cursor cursor = db.rawQuery(" select * from " + KakeiboDBHelper.KAKEIBO_TABLE + 
										" where date(Date) >= date('2013-04-01');", null);
			boolean isEof = cursor.moveToFirst();
			while(isEof){
				String data = "";
				for(int i=0; i < cursor.getColumnCount(); i++){
					data += "{" + cursor.getString(i) + "}";
				}
				data += "\n";
				mOutput.write(data.getBytes());
				isEof = cursor.moveToNext();
			}
			
			cursor.close();
			mOutput.close();
			Toast.makeText(this, "back up success",Toast.LENGTH_SHORT);
		}catch(Exception  e){
			e.printStackTrace();
			Toast.makeText(this, "back up failed", Toast.LENGTH_SHORT);
		}
	}
	@Override
	public void onResume(){
		super.onResume();
		draw();
	}
	
	@Override
	public void DialogOK(){
		draw();
	}

	@Override
	public void DialogCancel() {

	}

	
}
