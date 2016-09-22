package org.kbieron.iomerge

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase


fun SQLiteDatabase.query(table: String, columns: Array<String>): Cursor
		= this.query(table, columns, null, null, null, null, null)
