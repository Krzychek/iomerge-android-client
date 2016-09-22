package org.kbieron.iomerge.database

import java.io.Serializable


data class ServerBean(val id: Long, val address: String, val port: Int) : Serializable