package org.kbieron.iomerge.database;

import java.io.Serializable;


public class ServerBean implements Serializable {

	private final String address;
	private final int port;
	private final long id;

	public ServerBean(long id, String address, int port) {
		this.address = address;
		this.port = port;
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ServerBean that = (ServerBean) o;

		if (port != that.port) return false;
		return address.equals(that.address);

	}

	@Override
	public int hashCode() {
		int result = address.hashCode();
		result = 31 * result + port;
		return result;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public long getId() {
		return id;
	}
}
