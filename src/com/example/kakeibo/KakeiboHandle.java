package com.example.kakeibo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextPaint;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_kakeibo_handle)
public class KakeiboHandle extends Activity{
	private KakeiboDBHelper helper = new KakeiboDBHelper(this);
	
	@ViewById
	Spinner categorySpinner, subCategorySpinner;
	
	@ViewById 
	Spinner recordBOP;
	
	@ViewById
	EditText itemNameText, itemPriceText;
	
	@ViewById
	TextView handleDateText;
	
	Calendar calendar = Calendar.getInstance();
	
	@AfterViews
	void Initialize(){
		calendar.setTime(new Date()); //�����̓��t�ݒ�ƃ{�^���ɕ\��
		handleDateText.setText(KakeiboDataFormatter.dateFormatYMD(calendar.getTime())); 
		
		setCategoryTextView();
		categorySpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				setSubcategoryTextView();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		//�T�u�J�e�S���[���X�g�Ɍ����Z�b�g
		setSubcategoryTextView();
		registerForContextMenu(categorySpinner);
		registerForContextMenu(subCategorySpinner);
		TextPaint textPaint = handleDateText.getPaint();
		textPaint.setColor(android.graphics.Color.BLUE);
		textPaint.setUnderlineText(true);
	}
	/**
	 * Category�̌����Z�b�g
	 */
	void setCategoryTextView(){
		//�J�e�S���[���X�g�Ɍ����Z�b�g
		SQLiteDatabase db = helper.getReadableDatabase();
		ArrayList<String> categoryList = KakeiboDBHelper.getCategoryList(db);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		categorySpinner.setAdapter(adapter);
	}
	/**
	 * SubCategory�̌����Z�b�g����.
	 */
	void setSubcategoryTextView(){
		SQLiteDatabase db = helper.getReadableDatabase();
		ArrayList<String> subCategoryList = KakeiboDBHelper.getSubCategoryList(db, categorySpinner.getSelectedItem().toString());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,subCategoryList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		subCategorySpinner.setAdapter(adapter);
	}
	
	/**
	 * ���̓��̓��t��,�ő��id������Ă���.
	 * @param DB
	 * @return
	 */
	//���̓��̓��t�ōő��id������Ă���
	int getId(SQLiteDatabase DB){
		int id = 1;
		Cursor cursor = DB.rawQuery("" +
				"select max(" + KakeiboDBHelper.KAKEIBO_TABLE_ID + ")" + 
				" from " + KakeiboDBHelper.KAKEIBO_TABLE + 
				" where date("+ KakeiboDBHelper.KAKEIBO_TABLE_DATE + ") " +
						"= date(" +  "\'" + KakeiboDataFormatter.dateFormat(calendar.getTime()) + "\'" + ");"
						, null);
		boolean isEof =  cursor.moveToFirst();
		if(isEof)
			id = cursor.getInt(0)+1;
		return id;
	}
	
	void saveTable()
	{
		if( itemNameText.getText().toString().equals("") || itemPriceText.getText().toString().equals("")){
			Toast.makeText(this, "�i���Ɖ��i�����͂���Ă��܂���", Toast.LENGTH_SHORT).show();
			return;
		}
		SQLiteDatabase dbWrite = helper.getWritableDatabase();
		Integer bop = (int)(1 - 2*recordBOP.getSelectedItemId()); //����-> -1, �x�o -> 1
		try{
			KakeiboDBHelper.insertKakeibo(dbWrite, 
					KakeiboDataFormatter.dateFormat(calendar.getTime()),
					categorySpinner.getSelectedItem().toString(),
					subCategorySpinner.getSelectedItem().toString(), 
					itemNameText.getText().toString(), 
					bop*Integer.valueOf(itemPriceText.getText().toString()));
			KakeiboDBHelper.insertCategory(dbWrite, categorySpinner.getSelectedItem().toString());
			KakeiboDBHelper.insertSubCategory(dbWrite, categorySpinner.getSelectedItem().toString(), subCategorySpinner.getSelectedItem().toString());
			itemNameText.setText("");
			itemPriceText.setText("");
			Toast.makeText(this, "�ۑ����܂���", Toast.LENGTH_SHORT).show();
		}catch(Exception e){
			e.printStackTrace();

			Toast.makeText(this, "�ۑ��Ɏ��s���܂���", Toast.LENGTH_SHORT).show();
		}
		dbWrite.close();
	}


	@Click({R.id.handleDateText})
	void changeDate(){
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				calendar.set(year, monthOfYear, dayOfMonth);
				handleDateText.setText(KakeiboDataFormatter.dateFormatYMD(calendar.getTime()));
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
	}
	/*
	@Click({R.id.addCategoryButton})
	void addCutegory(View view){
		final EditText edtText = new EditText(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("�V�����J�e�S��");
		builder.setView(edtText);
		builder.setPositiveButton("OK", 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SQLiteDatabase dbWrite = helper.getWritableDatabase();
						if(edtText.getText().toString().equals("")) return;
						KakeiboDBHelper.insertCategory(dbWrite, edtText.getText().toString());
						dbWrite.close();
						ArrayAdapter<String> adapter = (ArrayAdapter<String>)categorySpinner.getAdapter();
						adapter.add(edtText.getText().toString());
						categorySpinner.setSelection(categorySpinner.getCount(),true);
					}
				}).show();
	}
	*/
	/**
	 * �I�v�V�������j���[�̕\��
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.handle_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * �I�v�V�������j���[�̃N���b�N�C�x���g
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		System.out.println("onOptionItemSelected");
		switch(item.getItemId()){
		case R.id.action_change_calendar:
			new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					calendar.set(year, monthOfYear, dayOfMonth);
					handleDateText.setText(KakeiboDataFormatter.dateFormatYMD(calendar.getTime()));
				}
			}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
			break;
		case R.id.action_settings:
			break;
		case R.id.action_search:
			onSearchRequested();
			break;
		case R.id.action_save:
			saveTable();
			break;
		case R.id.action_back:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("�ǉ�");
		menu.add("item1");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		return true;
	}
	

}
