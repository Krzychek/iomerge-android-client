package org.kbieron.iomerge.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*


class MySQLiteServerOpenHelper internal constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

	override fun onCreate(database: SQLiteDatabase) {
		database.execSQL(DATABASE_CREATE)
	}

	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE)
		onCreate(db)
	}

	companion object {

		internal val TABLE = "servers"
		internal val ID_COL = "_id"
		internal val ADDRESS_COL = "address"
		internal val PORT_COL = "port"
		internal val COLUMNS = Arrays.asList(ID_COL, ADDRESS_COL, PORT_COL)

		private val DATABASE_NAME = "servers.db"

		private val DATABASE_VERSION = 2

		// Database creation sql statement
		private val DATABASE_CREATE = """CREATE TABLE $TABLE(
														$ID_COL integer primary key autoincrement,
														$ADDRESS_COL text not null,
														$PORT_COL text not null,
														UNIQUE ($ADDRESS_COL,$PORT_COL) ON CONFLICT REPLACE
													);"""
	}

}
