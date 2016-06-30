package org.kbieron.iomerge.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.androidannotations.annotations.EBean;


@EBean
public class MySQLiteServerOpenHelper extends SQLiteOpenHelper {

	public static final String TABLE = "servers";

	public static final String ID_COL = "_id";

	public static final String ADDRESS_COL = "address";

	public static final String PORT_COL = "port";

	private static final String DATABASE_NAME = "servers.db";

	private static final int DATABASE_VERSION = 2;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE + "(" //
												  + ID_COL + " integer primary key autoincrement, " //
												  + ADDRESS_COL + " text not null, " //
												  + PORT_COL + " text not null, " //
												  + "UNIQUE (" + ADDRESS_COL + "," + PORT_COL + ")"
												  + "ON CONFLICT REPLACE);";

	public MySQLiteServerOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(db);
	}


}
