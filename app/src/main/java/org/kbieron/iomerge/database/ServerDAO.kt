package org.kbieron.iomerge.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.kbieron.iomerge.database.MySQLiteServerOpenHelper.Companion.ADDRESS_COL
import org.kbieron.iomerge.database.MySQLiteServerOpenHelper.Companion.COLUMNS
import org.kbieron.iomerge.database.MySQLiteServerOpenHelper.Companion.ID_COL
import org.kbieron.iomerge.database.MySQLiteServerOpenHelper.Companion.PORT_COL
import org.kbieron.iomerge.database.MySQLiteServerOpenHelper.Companion.TABLE
import org.kbieron.iomerge.query
import java.util.*


open class ServerDAO(context: Context) {

	internal var dbHelper = MySQLiteServerOpenHelper(context)

	private val onServerAddedListeners = Collections.newSetFromMap(WeakHashMap<OnServerAddedListener, Boolean>())


	private fun <T> doWithDb(callback: SQLiteDatabase.() -> T): T {
		val result = dbHelper.writableDatabase.run(callback)
		dbHelper.close()
		return result
	}


	fun createServer(address: String, port: Int): ServerBean? {

		val insertId = doWithDb {
			insert(TABLE, null, ContentValues().apply {
				put(ADDRESS_COL, address)
				put(PORT_COL, port)
			})
		}

		return if (insertId == -1L) null
		else ServerBean(insertId, address, port).apply {
			onServerAddedListeners.forEach { it.onServerAdded(this) }
		}
	}

	fun deleteServer(server: ServerBean) {
		doWithDb { delete(TABLE, "$ID_COL = ${server.id}", null) }
	}

	val allServers: MutableList<ServerBean>
		get() {
			val servers = ArrayList<ServerBean>()
			doWithDb {
				this.query(TABLE, COLUMNS.toTypedArray()).apply {
					moveToFirst()

					while (!isAfterLast) {
						servers.add(this.getServer())
						moveToNext()
					}
				}.close()

			}
			return servers
		}

	private fun Cursor.getServer(): ServerBean {
		val id = getLong(COLUMNS.indexOf(ID_COL))
		val address = getString(COLUMNS.indexOf(ADDRESS_COL))
		val port = getInt(COLUMNS.indexOf(PORT_COL))

		return ServerBean(id, address, port)
	}

	fun addOnServerAddedListener(onServerAddedListener: OnServerAddedListener) {
		this.onServerAddedListeners.add(onServerAddedListener)
	}

	interface OnServerAddedListener {

		fun onServerAdded(server: ServerBean)
	}
}

