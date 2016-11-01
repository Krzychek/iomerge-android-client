package org.kbieron.iomerge

import android.app.Application
import com.squareup.leakcanary.LeakCanary


class IOMergeApp : Application() {

	var connectionState: ConnectionState = ConnectionState.DISCONNECTED

	override fun onCreate() {
		super.onCreate()
		LeakCanary.install(this)
	}
}

enum class ConnectionState {
	CONNECTED, DISCONNECTED
}
