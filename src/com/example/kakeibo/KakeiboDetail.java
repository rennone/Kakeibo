package com.example.kakeibo;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Touch;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_kakeibo_detail)
public class KakeiboDetail extends DialogHolderActivity implements BackupTask.CompletionListener{
	private final int WrapContent = TableLayout.LayoutParams.WRAP_CONTENT;
	private final int MatchParent = TableLayout.LayoutParams.MATCH_PARENT;
	
	private Calendar calendar;
	private SQLiteDatabase db;
	private KakeiboDBHelper helper;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	private GestureDetector mGestureDetector;
	
	private TableRow getTableRow(int width, int height, float weight, int color) {
		TableRow row = new TableRow(this);
		TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(width, height);
		rowParams.weight = 1.0f;
		row.setLayoutParams(rowParams);
		row.setBackgroundColor(color);
		return row;
	}
	
	@ViewById
	TextView detailMonthText;
	
	@ViewById
	TableLayout kakeiboList;
	
	@AfterViews
	void initialize(){
		mGestureDetector = new GestureDetector(this, mOnGestureListener); 
		
		calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		detailMonthText.setText((calendar.get(Calendar.MONTH)+1) + "月");
		helper = new KakeiboDBHelper(this);
		
		//DBの初期化
		db = helper.getReadableDatabase();
		
		display();
	}
	
	void display()
	{	
		kakeiboList.removeAllViews();
		
		TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams();
		tableParams.setMargins(20, 20, 20, 20);
		kakeiboList.setLayoutParams(tableParams);
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		
		try {
			Time time = new Time("Asia/Tokyo");
			time.setToNow();
			String suffix[] = {"","","円"};
			
			//今月のデータを取ってくる
			for(Integer day = 31; day > 0; day--){
				String columns[] = {KakeiboDBHelper.KAKEIBO_TABLE_NAME, KakeiboDBHelper.KAKEIBO_TABLE_CATEGORY, KakeiboDBHelper.KAKEIBO_TABLE_PRICE};
				String selection = "date = ? AND price >= 0";
				String selectionArgs[] = {KakeiboDataFormatter.dateFormat(year, month, day)}; 
				String order = KakeiboDBHelper.KAKEIBO_TABLE_CATEGORY;
				Cursor cursor = db.query(KakeiboDBHelper.KAKEIBO_TABLE, columns, selection, selectionArgs, null, null, order, null);
				//System.out.println(selectionArgs[0] + "  " + cursor.getCount());
				//データベースが空なら次の日へ
				boolean isEof = cursor.moveToFirst();
				if(!isEof) continue;
				
				TableRow date = getTableRow(MatchParent, WrapContent, 1.0f, android.graphics.Color.WHITE);
				TextView dateText = createTextView(year + "年" + month + "月" + day + "日");
							
				date.addView(dateText);
				kakeiboList.addView(date);
				
				int total = 0;
				
				while(isEof){
					TableRow row = getTableRow(MatchParent, WrapContent, 1.0f, android.graphics.Color.BLACK);
					
					for(int j=0; j < cursor.getColumnCount(); j++){	//getColumnCount()で列の数を取ってこれる.
						String str;
						
						//価格の時、合計に追加する

						if(cursor.getColumnName(j).equals(KakeiboDBHelper.KAKEIBO_TABLE_PRICE)) {
							str = KakeiboDataFormatter.priceFormat(cursor.getInt(j));
							total += cursor.getInt(j);
						}
						else{
							str = cursor.getString(j) + suffix[j];
						}
						
						TableRow.LayoutParams lParams;
						if(cursor.getColumnName(j).equals(KakeiboDBHelper.KAKEIBO_TABLE_NAME)) {
							lParams = new TableRow.LayoutParams(WrapContent, WrapContent);
						}
						else {
							//高さを名前欄に合わせる
							lParams = new TableRow.LayoutParams(WrapContent, MatchParent);
						}
						
						lParams.setMargins(0, 0, 0, 1);
						
						lParams.weight = (j == 0 ? 1:0);
						
						
						TextView text = createTextView(str);
						text.setBackgroundColor(android.graphics.Color.WHITE);
						text.setClickable(true);
						text.setOnLongClickListener(new View.OnLongClickListener() {
							
							@Override
							public boolean onLongClick(View v) {
								// TODO Auto-generated method stub
								final String str = ((TextView)v).getText().toString();
								System.out.println(str);
								return false;
							}
						});
						
						row.addView(text, lParams);
					}
					
					kakeiboList.addView(row);
					isEof = cursor.moveToNext();
					
				}
				
				//日ごとの合計金額を追加
				TableRow row = getTableRow(MatchParent, WrapContent, 1.0f, android.graphics.Color.BLACK);
				String[] totalStrings = {"合計", KakeiboDataFormatter.priceFormat(total)};
				
				for(int j = 0; j < totalStrings.length; j++) {
					TextView text = createTextView(totalStrings[j]);
					text.setBackgroundColor(android.graphics.Color.WHITE);
					TableRow.LayoutParams lParams = new TableRow.LayoutParams(0, WrapContent);
					lParams.setMargins(0, 0, 0, 1);
					lParams.weight = 0.5f;
				
					row.addView(text, lParams);	
				}
				
				kakeiboList.addView(row);
				//空行挿入
				TableRow empRow = getTableRow(MatchParent, WrapContent, 2.0f, android.graphics.Color.WHITE);
				empRow.addView(createTextView(" "), new TableRow.LayoutParams(0, WrapContent));
				kakeiboList.addView(empRow);
				cursor.close();
			}
		} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "error occurred", Toast.LENGTH_SHORT).show();
		}
	}

	//Backup Method
	@Override
	public void onResume(){
		super.onResume();
		display();
		/*
		 //バックアップをリストア
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			BackupTask task = new BackupTask(this);
			task.setCompletionListener(this);
			task.execute(BackupTask.COMMAND_RESTORE);
		}
		*/
	}
	
	@Override
	public void onPause(){
		super.onPause();
		/*
		 //バックアップをとる
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			BackupTask task = new BackupTask(this);
			task.execute(BackupTask.COMMAND_BACKUP);
		}
		*/
	}
	
	@Override
	public void onBackupComplete() {
		Toast.makeText(this, "Backup Successful", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRestoreComplete() {
		Toast.makeText(this, "Restore Successful", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onError(int errorCode) {
		if(errorCode == BackupTask.RESTORE_NOFILEERROR){
			Toast.makeText(this, "No Backup Found to Restore", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Error During Operation: " +errorCode, Toast.LENGTH_SHORT).show();
		}	
	}
	
	/*
	@Click({R.id.top_button})
	void top(){	
		finish();
	}
	*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail_menu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Touch({R.id.kakeiboList})
	void touched(MotionEvent event) {
		 mGestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		System.out.println("onOptionItemSelected");
		switch(item.getItemId()){
		case R.id.action_recording:
			Intent intent = new Intent(this, KakeiboHandle_.class);
			startActivity(intent);
			break;
		case R.id.action_change_calendar:
			createDatePickerDialog();
			return true;
		case R.id.action_settings:
			new Setting(this).create();
			return true;
		case R.id.action_search:
			onSearchRequested();
		case R.id.action_back:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	
	 private final SimpleOnGestureListener mOnGestureListener = new SimpleOnGestureListener() {
		 @Override
		 public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
			 try {
				 System.out.println("タッチ");
				 if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH) {
					 return false; // 縦の移動距離が大きすぎる場合は無視
				 }
				 if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					 // 開始位置から終了位置の移動距離が指定値より大きい
					 // X軸の移動速度が指定値より大きい
					 System.out.println("右から左");
					 calendar.add(Calendar.MONTH, -1);
					 display();
					 kakeiboList.invalidate();
					 
				 } else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					 // 終了位置から開始位置の移動距離が指定値より大きい
					 // X軸の移動速度が指定値より大きい
					 System.out.println("左から右");
					 calendar.add(Calendar.MONTH, 1);
					 display();
					 kakeiboList.invalidate();
				 }
			 } catch (Exception e) {
				 // nothing
				 System.out.println("エラー");
			 }
			 return false;
		 }
	 };	
	 
	 @Override
	 public void DialogOK(){
		 display();
		 kakeiboList.invalidate();
	 }

	@Override
	public void DialogCancel() {
		// TODO Auto-generated method stub
		
	}
	
	//日付選択dialogの作成
	public void createDatePickerDialog() {
		int mYear = calendar.get(Calendar.YEAR);
		int mMonth = calendar.get(Calendar.MONTH);
		int mDay = calendar.get(Calendar.DAY_OF_MONTH);
		
		//日付選択メニューの作成
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		DatePicker datePicker = new DatePicker(this);
		builder.setView(datePicker);
		builder.setTitle("日付選択");
		builder.setNegativeButton("Cancel", null);
		
		builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				display();
				kakeiboList.invalidate();
			}
		});
		
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
		
		datePicker.init(mYear, mMonth, mDay, new DatePicker.OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				calendar.set(year, monthOfYear, dayOfMonth);
				detailMonthText.setText((1+calendar.get(Calendar.MONTH)) + "月");
			}
		});
	}
	
	//検索用dialogの作成
	public void createSearchDialog() {
		System.out.println("search");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText editText = new EditText(this);
		
		builder.setIcon(android.R.drawable.ic_search_category_default);
		builder.setTitle("search");
		builder.setView(editText);
		builder.setPositiveButton("search", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//とりあえずデバッグ出力する
				System.out.println(editText.getText().toString());
				searchQuery(editText.getText().toString());
			}
		});
		
		AlertDialog alertDialog = builder.create();
		alertDialog.show();			
	}
	
	//検索
	public void searchQuery(final String query) {
		kakeiboList.removeAllViews();
		
		TableRow date = getTableRow(MatchParent, WrapContent, 1.0f, android.graphics.Color.WHITE);
		TextView dateText = createTextView("検索文字列: " + query);
		date.addView(dateText);
		
		kakeiboList.addView(date);
		
		//以下に検索結果を表示するコード
		String suffix[] = {"","","円"};
		
		String columns[] = {KakeiboDBHelper.KAKEIBO_TABLE_NAME, KakeiboDBHelper.KAKEIBO_TABLE_CATEGORY, KakeiboDBHelper.KAKEIBO_TABLE_PRICE, KakeiboDBHelper.KAKEIBO_TABLE_DATE};
		String selection = "ItemName like ?";
		String selectionArgs[] = {"%" + query + "%"}; 
		String order = KakeiboDBHelper.KAKEIBO_TABLE_DATE + " desc";
		Cursor cursor = db.query(KakeiboDBHelper.KAKEIBO_TABLE, columns, selection, selectionArgs, null, null, order, null);
	
		String dateStr = "0000-00-00";
		boolean isEof = cursor.moveToFirst();
		
		while(isEof){
			for(int j = 0; j < cursor.getColumnCount(); j++) {
				if(cursor.getColumnName(j).equals(KakeiboDBHelper.KAKEIBO_TABLE_DATE)) {
					if(dateStr.equals(cursor.getString(j)) == false) {
						dateStr = cursor.getString(j);
						
						TableRow d = getTableRow(MatchParent, WrapContent, 1.0f, android.graphics.Color.WHITE);
						TextView dt = createTextView(KakeiboDataFormatter.dateFormat(dateStr));
						d.addView(dt);
						kakeiboList.addView(d);		
					}
				}
			}
			
			TableRow row = getTableRow(MatchParent, WrapContent, 1.0f, android.graphics.Color.BLACK);
			
			for(int j=0; j < cursor.getColumnCount(); j++){	//getColumnCount()で列の数を取ってこれる.
				String str;
				
				if(cursor.getColumnName(j).equals(KakeiboDBHelper.KAKEIBO_TABLE_DATE)) {
					continue;
				}

				if(cursor.getColumnName(j).equals(KakeiboDBHelper.KAKEIBO_TABLE_PRICE)) {
					str = KakeiboDataFormatter.priceFormat(cursor.getInt(j));
				}
				else{
					str = cursor.getString(j) + suffix[j];
				}
				
				TableRow.LayoutParams lParams;
				if(cursor.getColumnName(j).equals(KakeiboDBHelper.KAKEIBO_TABLE_NAME)) {
					lParams = new TableRow.LayoutParams(WrapContent, WrapContent);
				}
				else {
					//高さを名前欄に合わせる
					lParams = new TableRow.LayoutParams(WrapContent, MatchParent);
				}
				
				lParams.setMargins(0, 0, 0, 1);
				
				lParams.weight = (j == 0 ? 1:0);
				
				TextView text = createTextView(str);
				text.setBackgroundColor(android.graphics.Color.WHITE);
				
				row.addView(text, lParams);
			}
			
			kakeiboList.addView(row);
			isEof = cursor.moveToNext();
			
		}
		
		kakeiboList.addView(getTableRow(MatchParent, WrapContent, 2.0f, android.graphics.Color.WHITE));
		cursor.close();
	}
	
	//TextViewの生成　デフォルト値として黒色
	public TextView createTextView(final String text) {
		TextView textView = new TextView(this);
		
		textView.setText(text);
		textView.setTextColor(android.graphics.Color.BLACK);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Setting.getTextSize());
		return textView;
	}
	
	
}


