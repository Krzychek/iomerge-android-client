package org.kbieron.iomerge.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import com.pawegio.kandroid.i
import com.pawegio.kandroid.w
import org.kbieron.iomerge.database.ServerBean
import org.kbieron.iomerge.notifications.NotificationFactory
import org.kbieron.iomerge.views.EdgeTrigger


open class NetworkManager : Service() {

	private val inputDevice
			by lazy { InputDevice(applicationContext) }

	private val connectionHandler
			by lazy { ConnectionHandler(applicationContext, inputDevice) }

	private val edgeTriggerView: EdgeTrigger
			by lazy { EdgeTrigger(applicationContext, connectionHandler) }

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

		if (intent != null && intent.action != null)
			when (intent.action) {
				DISCONNECT_ACTION -> disconnect()
				CONNECT_ACTION -> connect(intent.getSerializableExtra(SERVER_EXTRA) as ServerBean)
				else -> w("Uknown intent action: ${intent.action}")
			}

		return super.onStartCommand(intent, flags, startId)
	}

	override fun onBind(intent: Intent): Binder = NetworkManagerBinder()

	override fun onDestroy() = disconnect()

	internal fun connect(server: ServerBean) {
		if (!connectionHandler.isConnected) {

			// connect
			connectionHandler.connect(server, this)
			// show notification
			startForeground(1, NotificationFactory(applicationContext).serverConnected(server))
			// showEdgeTrigger
			edgeTriggerView.show()

		} else {
			i("already connected")
		}

	}

	fun disconnect() {
		i("Disconnecting")
		edgeTriggerView.hide()
		inputDevice.stop()
		stopForeground(true)
	}

	inner class NetworkManagerBinder : Binder() {
		val connectionHandler: ConnectionHandler
			get() = this@NetworkManager.connectionHandler
	}

	companion object {
		val DISCONNECT_ACTION = "DISCONNECT"
		val CONNECT_ACTION = "CONNECT"
		val SERVER_EXTRA = "SERVER"
	}
}
