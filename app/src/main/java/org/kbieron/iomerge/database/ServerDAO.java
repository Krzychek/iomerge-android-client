package org.kbieron.iomerge.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import static org.kbieron.iomerge.database.MySQLiteServerOpenHelper.ADDRESS_COL;
import static org.kbieron.iomerge.database.MySQLiteServerOpenHelper.ID_COL;
import static org.kbieron.iomerge.database.MySQLiteServerOpenHelper.PORT_COL;
import static org.kbieron.iomerge.database.MySQLiteServerOpenHelper.TABLE;


@EBean
public class ServerDAO {

	private SQLiteDatabase database;

	@Bean
	protected MySQLiteServerOpenHelper dbHelper;

	private String[] COLUMNS = new String[]{ID_COL, ADDRESS_COL, PORT_COL};


	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public ServerBean createServer(String address, int port) {

		ContentValues values = new ContentValues();
		values.put(ADDRESS_COL, address);
		values.put(PORT_COL, port);

		long insertId = database.insert(TABLE, null, values);

		if (insertId != -1) {
			ServerBean server = new ServerBean();
			server.setId(insertId);
			server.setAddress(address);
			server.setPort(port);
			return server;
		} else {
			return null;
		}

	}

	public void deleteServer(ServerBean server) {
		database.delete(TABLE, ID_COL + " = " + server.getId(), null);
	}

	public List<ServerBean> getAllServers() {

		List<ServerBean> servers = new ArrayList<>();

		Cursor cursor = database.query(TABLE, COLUMNS, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ServerBean server = cursorToServer(cursor);
			servers.add(server);
			cursor.moveToNext();
		}

		cursor.close();
		return servers;
	}

	private ServerBean cursorToServer(Cursor cursor) {
		ServerBean comment = new ServerBean();
		comment.setId(cursor.getLong(0));
		comment.setAddress(cursor.getString(1));
		comment.setPort(cursor.getInt(2));
		return comment;
	}
}

