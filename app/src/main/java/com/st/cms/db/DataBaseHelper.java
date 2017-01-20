package com.st.cms.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DataBaseHelper extends SQLiteOpenHelper {

	private Context context;

	/**
	 * 定义伤员表
	 * id:主键唯一标识
	 * code:编号
	 * name:名称
	 * severity_lv:受伤级别，严重程度
	 * zone_location:所在地区
	 */
//	private static String CASUALTY_MGMT_WOUNDED = "create table if not exists wounded(" +
//			"id integer primary key autoincrement," +
//			"code integer," +
//			"name text," +
//			"severity_lv integer," +
//			"zone_location text)";
	
	public DataBaseHelper(Context context){
		this(context, "poccms.db", null, 1);
	}
	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.context=context;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
//		db.execSQL(CASUALTY_MGMT_WOUNDED);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}

}
