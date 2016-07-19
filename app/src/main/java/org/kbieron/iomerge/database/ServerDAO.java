package org.kbieron.iomerge.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import static org.kbieron.iomerge.database.MySQLiteServerOpenHelper.ADDRESS_COL;
import static org.kbieron.iomerge.database.MySQLiteServerOpenHelper.COLUMNS;
import static org.kbieron.iomerge.database.MySQLiteServerOpenHelper.ID_COL;
import static org.kbieron.iomerge.database.MySQLiteServerOpenHelper.PORT_COL;
import static org.kbieron.iomerge.database.MySQLiteServerOpenHelper.TABLE;


@EBean(scope = EBean.Scope.Singleton)
public class ServerDAO {

	@Bean
	MySQLiteServerOpenHelper dbHelper;

	private SQLiteDatabase database;
	private Set<OnServerAddedListener> onServerAddedListeners = Collections.newSetFromMap(new WeakHashMap<OnServerAddedListener, Boolean>());


	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		dbHelper.close();
	}

	public ServerBean createServer(String address, int port) {

		ContentValues values = new ContentValues();
		values.put(ADDRESS_COL, address);
		values.put(PORT_COL, port);

		open();
		long insertId = database.insert(TABLE, null, values);
		close();

		if (insertId != -1) {
			ServerBean server = new ServerBean(insertId, address, port);

			for (OnServerAddedListener onServerAddedListener : onServerAddedListeners) {
				onServerAddedListener.onServerAdded(server);
			}

			return server;
		}

		return null;
	}

	public void deleteServer(ServerBean server) {
		open();
		database.delete(TABLE, ID_COL + " = " + server.getId(), null);
		close();
	}

	public List<ServerBean> getAllServers() {

		List<ServerBean> servers = new ArrayList<>();

		open();
		Cursor cursor = database.query(TABLE, COLUMNS.toArray(new String[0]), null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ServerBean server = getFromCursor(cursor);
			servers.add(server);
			cursor.moveToNext();
		}
		cursor.close();

		close();
		return servers;
	}

	private ServerBean getFromCursor(Cursor cursor) {
		long id = cursor.getLong(COLUMNS.indexOf(ID_COL));
		String address = cursor.getString(COLUMNS.indexOf(ADDRESS_COL));
		int port = cursor.getInt(COLUMNS.indexOf(PORT_COL));

		return new ServerBean(id, address, port);
	}

	public void addOnServerAddedListener(OnServerAddedListener onServerAddedListener) {
		this.onServerAddedListeners.add(onServerAddedListener);
	}

	public interface OnServerAddedListener {

		void onServerAdded(ServerBean server);
	}
}