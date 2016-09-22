package org.kbieron.iomerge.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.PRIORITY_LOW
import com.pawegio.kandroid.IntentFor
import org.kbieron.iomerge.activities.MainActivity
import org.kbieron.iomerge.activities.RemoteContolActivity
import org.kbieron.iomerge.android.R
import org.kbieron.iomerge.database.ServerBean
import org.kbieron.iomerge.services.NetworkManager


open class NotificationFactory(private val context: Context) {

	private val connectedText: String
		get() = context.resources.getString(R.string.server_connected)

	private val appName: String
		get() = context.resources.getString(R.string.app_name)

	private val connectedTicker: String
		get() = context.resources.getString(R.string.server_connected_ticker)

	fun serverConnected(server: ServerBean): Notification =
			NotificationCompat.Builder(context)
					.setContentIntent(clickIntent)
					.setSmallIcon(android.R.drawable.ic_menu_camera)
					.setContentTitle(appName)
					.setContentText("$connectedText ${server.address}:${server.port}")
					.setPriority(PRIORITY_LOW)
					.setTicker(connectedTicker)
					.addAction(disconnectAction)
					.addAction(remoteControlAction)
					.build()

	private val disconnectAction: NotificationCompat.Action
		get() = NotificationCompat.Action(
				android.R.drawable.ic_menu_close_clear_cancel,
				"disconnect",
				PendingIntent.getService(context, 0, IntentFor<NetworkManager>(context).setAction(NetworkManager.DISCONNECT_ACTION), 0))

	private val clickIntent: PendingIntent
		get() = PendingIntent.getActivity(context, 0, IntentFor<MainActivity>(context), 0)

	private val remoteControlAction: NotificationCompat.Action
		get() = NotificationCompat.Action(
				android.R.drawable.ic_media_play,
				"remote control",
				PendingIntent.getActivity(context, 0, IntentFor<RemoteContolActivity>(context), 0))
}
