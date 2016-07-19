package org.kbieron.iomerge.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.androidannotations.annotations.EBean;

import java.util.Arrays;
import java.util.List;


@EBean(scope = EBean.Scope.Singleton)
public class MySQLiteServerOpenHelper extends SQLiteOpenHelper {

	static final String TABLE = "servers";
	static final String ID_COL = "_id";
	static final String ADDRESS_COL = "address";
	static final String PORT_COL = "port";
	static final List<String> COLUMNS = Arrays.asList(ID_COL, ADDRESS_COL, PORT_COL);

	private static final String DATABASE_NAME = "servers.db";

	private static final int DATABASE_VERSION = 2;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE + "(" //
												  + ID_COL + " integer primary key autoincrement, " //
												  + ADDRESS_COL + " text not null, " //
												  + PORT_COL + " text not null, " //
												  + "UNIQUE (" + ADDRESS_COL + "," + PORT_COL + ")"
												  + "ON CONFLICT REPLACE);";

	MySQLiteServerOpenHelper(Context context) {
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
