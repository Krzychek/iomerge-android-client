package org.kbieron.iomerge.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.pawegio.kandroid.e
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


	private fun <T> doWithDb(writable: Boolean = false, callback: SQLiteDatabase.() -> T): T {
		val result = if (writable) dbHelper.writableDatabase.run(callback) else dbHelper.readableDatabase.run(callback)
		dbHelper.close()
		return result
	}


	fun createServer(address: String, port: Int): ServerBean? {

		val insertId = doWithDb(writable = true) {
			insert(TABLE, null, ContentValues(2).apply {
				put(ADDRESS_COL, address)
				put(PORT_COL, port)
			})
		}

		if (insertId == -1L) {
			e("Problem while creating new server")
			return null

		} else {
			return ServerBean(insertId, address, port).apply {
				onServerAddedListeners.forEach { it.onServerAdded(this) }
			}
		}
	}

	fun deleteServer(server: ServerBean) {
		doWithDb(writable = true) {
			delete(TABLE, "$ID_COL = ${server.id}", null)
		}
	}

	val allServers: MutableList<ServerBean>
		get() = doWithDb {
			query(TABLE, COLUMNS).let {
				it.moveToFirst()

				val servers = ArrayList<ServerBean>(it.count)
				while (!it.isAfterLast) {
					servers.add(it.getServer())
					it.moveToNext()
				}
				it.close()
				servers
			}
		}


	private fun Cursor.getServer(): ServerBean {
		val id = getLong(getColumnIndex(ID_COL))
		val address = getString(getColumnIndex(ADDRESS_COL))
		val port = getInt(getColumnIndex(PORT_COL))

		return ServerBean(id, address, port)
	}

	fun addOnServerAddedListener(onServerAddedListener: OnServerAddedListener) {
		this.onServerAddedListeners.add(onServerAddedListener)
	}

	interface OnServerAddedListener {

		fun onServerAdded(server: ServerBean)
	}
}

