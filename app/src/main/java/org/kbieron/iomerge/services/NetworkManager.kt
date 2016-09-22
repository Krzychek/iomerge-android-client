package org.kbieron.iomerge.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import com.github.krzychek.iomerge.server.model.message.Message
import com.pawegio.kandroid.i
import com.pawegio.kandroid.w
import org.kbieron.iomerge.database.ServerBean
import org.kbieron.iomerge.notifications.NotificationFactory


open class NetworkManager : Service() {

	private var connectionHandler: ConnectionHandler? = null

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

		if (intent != null && intent.action != null)
			when (intent.action) {
				DISCONNECT_ACTION -> connectionHandler?.disconnect()
				CONNECT_ACTION -> connect(intent.getSerializableExtra(SERVER_EXTRA) as ServerBean)
				else -> w("Uknown intent action: ${intent.action}")
			}

		return super.onStartCommand(intent, flags, startId)
	}

	override fun onBind(intent: Intent): Binder = NetworkManagerBinder()

	override fun onDestroy() {
		connectionHandler?.disconnect()
	}

	internal fun connect(server: ServerBean) {
		if (connectionHandler != null)
			i("already connected")
		else {
			connectionHandler = ConnectionHandler(
					context = applicationContext,
					server = server,
					disconnectClbk = {
						i("Disconnecting")
						stopForeground(true)
						connectionHandler = null
					}
			)
			startForeground(1, NotificationFactory(applicationContext).serverConnected(server))
		}
	}

	inner class NetworkManagerBinder : Binder() {
		val sendMessageFun = { message: Message -> connectionHandler?.sendMessage(message) }
	}

	companion object {
		val DISCONNECT_ACTION = "DISCONNECT"
		val CONNECT_ACTION = "CONNECT"
		val SERVER_EXTRA = "SERVER"
	}
}
