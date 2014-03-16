package com.example.kakeibo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

interface Kakeib{

}
public class KakeiboDBHelper extends SQLiteOpenHelper{
	private static final String DB_NAME = "Kakeibodb";
	public static final String CATEGORY_TABLE = "Categories"; 
	public static final String CATEGORY_ID = "_id";
	public static final String CATEGORY_NAME = "name";

	public static final String SUBCATEGORY_TABLE = "SubCategories";
	public static final String SUBCATEGORY_ID = "_id";
	public static final String SUBCATEGORY_NAME = "name";
	public static final String SUBCATEGORY_PARENT = "parent";

	public static final String KAKEIBO_TABLE = "Kakeibo";
	public static final String KAKEIBO_TABLE_DATE = "date";
	public static final String KAKEIBO_TABLE_ID   = "_id";
	public static final String KAKEIBO_TABLE_NAME = "itemName";
	public static final String KAKEIBO_TABLE_CATEGORY = "category";
	public static final String KAKEIBO_TABLE_SUBCATEGORY = "subCategory";
	public static final String KAKEIBO_TABLE_PRICE    = "price";

	private Context mContext;

	public KakeiboDBHelper(Context context) {
		super(context, DB_NAME, null, 1);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			System.out.println("onCreate DBHelper");
			db.execSQL("PRAGMA foreign_keys=ON;");	//�O���L�[����ON
			execSql(db, "sql/create");
			System.out.println("onCreate DBHelper End");
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {  
			execSql(db,"sql/drop");  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
		onCreate(db);  
	}

	/**
	 * �����Ɏw�肵��assets�t�H���_����sql�����s���܂��B 
	 * @param db �f�[�^�x�[�X 
	 * @param assetsDir assets�t�H���_���̃t�H���_�̃p�X 
	 * @throws IOException 
	 */  
	private void execSql(SQLiteDatabase db,String assetsDir) throws IOException {  
		AssetManager as = mContext.getResources().getAssets();      
		try {  
			String files[] = as.list(assetsDir);  
			for (int i = 0; i < files.length; i++) {      
				String str = readFile(as.open(assetsDir + "/" + files[i]));
				for (String sql: str.split("/")){ 	//������,SQL�t�@�C���𕪊����邽�߂�, .sql�̂ق��� /�@�������Ă���
					db.execSQL(sql);  
				}   
			}  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}

	/** 
	 * �t�@�C�����當�����ǂݍ��݂܂��B 
	 * @param is 
	 * @return �t�@�C���̕����� 
	 * @throws IOException 
	 */
	private String readFile(InputStream is) throws IOException{  
		BufferedReader br = null;  
		try {  
			br = new BufferedReader(new InputStreamReader(is,"SJIS"));  

			StringBuilder sb = new StringBuilder();      
			String str;        
			while((str = br.readLine()) != null){        
				sb.append(str +"\n");       
			}      
			return sb.toString();  
		} finally {  
			if (br != null) br.close();  
		}  
	}    

	/**
	 * For Debug �e�[�u���ꗗ��Ԃ�
	 * @param db
	 * @return List of table name in db 
	 */
	public static String getTableList(SQLiteDatabase db) {
		Cursor c = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' ", null);
		Log.d("��", String.valueOf(c.getCount()));
		boolean isEof = c.moveToFirst();
		String text="";
		while (isEof) {
			text += c.getString(1) + "\n";
			isEof = c.moveToNext();
		}
		c.close();
		return text;	
	}

	/**
	 * For Debug     �f�[�^�x�[�X�̒��g��l����Ă���(�f�o�b�O�p)
	 * @param db 
	 * @param limit ����Ă��錏��
	 * @return
	 */
	public static String getAllTableColum(SQLiteDatabase db, String limit){
		String COLUMS[] = {"*"};
		Cursor c = db.query(KAKEIBO_TABLE, COLUMS, null, null, null, null, null, limit);
		boolean isEof = c.moveToFirst();
		String text="";
		while (isEof) {
			text += c.getString(0) + "  " + c.getString(2) + "\n";
			isEof = c.moveToNext();
		}
		c.close();
		System.out.println("�f�[�^�x�[�X \n" + text);
		return text;
	}

	/**
	 * �o�^����Ă���f�[�^�̍ŏ��N�ƍő�N���擾
	 * @param db
	 * @return [min, max]
	 */
	public static int[] getMinMaxYear(SQLiteDatabase db){
		int nm[] = new int[2];
		try{
			Cursor cursor = db.rawQuery(
					"select Min(date) " +
							"from " + KAKEIBO_TABLE + ";", null);
			if(cursor.moveToFirst()){
				nm[0] = Integer.valueOf(cursor.getString(0).substring(0, 4)).intValue();
				System.out.println(nm[0]);
			}
			cursor.close();
			cursor = db.rawQuery(
					"select Max(date) " +
							"from " + KAKEIBO_TABLE + ";", null);
			if(cursor.moveToFirst()){
				nm[1] = Integer.valueOf(cursor.getString(0).substring(0, 4)).intValue(); 
				System.out.println(nm[1]);
			}
			cursor.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		return nm;
	}

	/**
	 * �J�e�S���[�e�[�u���̖��O��S�Ď擾
	 * @param dbRead
	 * @return �J�e�S���[�e�[�u���̖��O��ArrayList<String>��
	 */
	public static ArrayList<String> getCategoryList(SQLiteDatabase dbRead){
		ArrayList<String> categoryList = new ArrayList<String>();
		try {
			Cursor cursor = dbRead.rawQuery(
					"select Name " +
							"from "+ CATEGORY_TABLE + ";", null);
			boolean isEof = cursor.moveToFirst();
			while(isEof){
				categoryList.add(cursor.getString(0));
				isEof = cursor.moveToNext();
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		dbRead.close();
		return categoryList;
	}

	/**
	 * �T�u�J�e�S���[�e�[�u������,�e��parent�ł��閼�O���擾
	 * @param dbRead
	 * @return ArrayList<String>��
	 */
	public static ArrayList<String> getSubCategoryList(SQLiteDatabase dbRead, String parent){
		ArrayList<String> subCategoryList = new ArrayList<String>();
		try {
			Cursor cursor = dbRead.rawQuery(
					"select Name " +
							" from "+ SUBCATEGORY_TABLE +
							" where " + SUBCATEGORY_PARENT  + " = \'" + parent+ "\' ;"
							, null);
			boolean isEof = cursor.moveToFirst();
			while(isEof){
				subCategoryList.add(cursor.getString(0));
				isEof = cursor.moveToNext();
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		dbRead.close();
		return subCategoryList;
	}

	/**
	 * �J�e�S���[�e�[�u���ɐV�������R�[�h��ǉ�
	 * @param dbWrite
	 * @param categoryName
	 */
	public static void insertCategory(SQLiteDatabase dbWrite, String name)
			throws SQLException{
		if(name.equals("")) return;
		try {
			String sql = "INSERT OR IGNORE INTO " +
					CATEGORY_TABLE + " (" + CATEGORY_NAME + ")"+
					"VALUES ('" + name+"');";
			dbWrite.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �J�e�S���[���X�g����name�̃��R�[�h���폜
	 * @param dbWrite
	 * @param name
	 */
	public static void deleteCategory(SQLiteDatabase dbRead, String name){
		String where  =CATEGORY_NAME + " = '" + name +"'"; 
		try {
			dbRead.delete(CATEGORY_TABLE, where, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dbRead.close();
	}

	/**
	 * �T�u�J�e�S���[��name, prent�̃��R�[�h��ǉ�
	 * @param dbWrite
	 * @param name
	 * @param parent
	 * @exception SQLExeption
	 */
	public static void insertSubCategory(SQLiteDatabase dbWrite, String name, String parent)
			throws SQLException{
		if(name.equals("") || parent.equals("")) return;
		try{
			String sql = "insert into " + SUBCATEGORY_TABLE + "(" +SUBCATEGORY_NAME + ", " + SUBCATEGORY_PARENT + ")"+
					select(name, parent) +
					" where not exist ( select *"+
					" from "  + SUBCATEGORY_TABLE + 
					" where " + equal(SUBCATEGORY_NAME,name ) +
					" AND "+equal(SUBCATEGORY_PARENT, parent)+");"; 
			dbWrite.execSQL(sql);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * �T�u�J�e�S���[���烌�R�[�h���폜
	 * @param dbRead
	 * @param name
	 * @param parent
	 * @exception SQLException
	 */
	public static void deleteSubCategory(SQLiteDatabase dbRead, String name, String parent)
			throws SQLException{
		String where = SUBCATEGORY_NAME + " = '" + name + "' AND " + SUBCATEGORY_PARENT + " = '" + parent + "'" ;
		try {
			dbRead.delete(SUBCATEGORY_TABLE, where, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dbRead.close();
	}

	/**
	 * Kakeibo�e�[�u���Ƀ��R�[�h�ǉ�
	 * @param dbWrite
	 * @param date
	 * @param category
	 * @param subCategory
	 * @param name
	 * @param price
	 * @throws SQLException
	 */
	public static void insertKakeibo(SQLiteDatabase dbWrite, 
			String date,
			String category,
			String subCategory,
			String name,
			Integer price) throws SQLException{
		try{
			ContentValues ctval = new ContentValues();
			ctval.put(KAKEIBO_TABLE_DATE, date);
			ctval.put(KakeiboDBHelper.KAKEIBO_TABLE_NAME, name);
			ctval.put(KakeiboDBHelper.KAKEIBO_TABLE_CATEGORY, category);
			ctval.put(KakeiboDBHelper.KAKEIBO_TABLE_SUBCATEGORY, subCategory);
			ctval.put(KakeiboDBHelper.KAKEIBO_TABLE_PRICE, price);
			dbWrite.insert(KakeiboDBHelper.KAKEIBO_TABLE, null, ctval);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static Cursor getKakeiboRecord(SQLiteDatabase dbRead, String date){
		String columns[] = {KAKEIBO_TABLE_NAME, KAKEIBO_TABLE_CATEGORY, KAKEIBO_TABLE_PRICE};
		String selection = "Date = ?";
		String selectionArgs[] = {date}; 
		String order = KAKEIBO_TABLE_CATEGORY;
		Cursor cursor = dbRead.query(KakeiboDBHelper.KAKEIBO_TABLE, columns, selection, selectionArgs, null, null, order, null);
		return cursor;
	}
	/**
	 * sqlite�ł̃e�L�X�g�̔�r���s����������Ԃ�.
	 * @param colum
	 * @param value
	 * @return colum = 'value' �̕������Ԃ�
	 */
	public static String equal(String colum, String value){
		return colum+"='"+value+"'";
	}

	public static String equal(String colum, Integer value){
		return colum + "=" + value;
	}

	public static String select(String... colums){
		String str = "SELECT " + "'"+colums[0]+"'";
		for(int i=1; i<colums.length;i++){
			str += "," + "'"+colums[i]+"'";
		}
		return str + " ";
	}
}
