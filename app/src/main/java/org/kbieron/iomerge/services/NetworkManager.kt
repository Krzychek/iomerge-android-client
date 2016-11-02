package org.kbieron.iomerge.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Looper
import android.widget.Toast
import com.github.krzychek.iomerge.server.model.message.Message
import com.pawegio.kandroid.i
import com.pawegio.kandroid.runAsync
import com.pawegio.kandroid.w
import org.kbieron.iomerge.IOMergeApp
import org.kbieron.iomerge.database.ServerBean
import org.kbieron.iomerge.notifications.NotificationFactory
import java.net.ConnectException


class NetworkManager : Service() {

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
		(application as IOMergeApp).connectedServer = null
	}

	private val disconnectCallback = {
		i("Disconnecting")
		stopForeground(true)
		connectionHandler = null
		(application as IOMergeApp).connectedServer = null
	}

	internal fun connect(server: ServerBean) = runAsync {
		Looper.prepare()
		if (connectionHandler != null)
			w("already connected")
		else try {
			connectionHandler = ConnectionHandler(
					context = applicationContext,
					server = server,
					disconnectClbk = disconnectCallback
			)
			(application as IOMergeApp).connectedServer = server
			startForeground(1, NotificationFactory(applicationContext).serverConnected(server))

		} catch (e: ConnectException) {
			Toast.makeText(applicationContext, "Not able to connect to: ${server.address}:${server.port}", Toast.LENGTH_LONG)
					.show()
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
