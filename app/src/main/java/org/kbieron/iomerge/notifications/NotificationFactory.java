package org.kbieron.iomerge.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.res.StringRes;
import org.kbieron.iomerge.activities.MainActivity_;
import org.kbieron.iomerge.activities.RemoteContolActivity_;
import org.kbieron.iomerge.android.R;
import org.kbieron.iomerge.database.ServerBean;
import org.kbieron.iomerge.services.NetworkManager;
import org.kbieron.iomerge.services.NetworkManager_;

import static android.support.v4.app.NotificationCompat.PRIORITY_LOW;


@EBean(scope = EBean.Scope.Singleton)
public class NotificationFactory {

	@RootContext
	Context context;

	@StringRes(R.string.server_connected)
	String connectedText;

	@StringRes(R.string.app_name)
	String appName;

	@StringRes(R.string.server_connected_ticker)
	String connectedTicker;

	public Notification serverConnected(ServerBean server) {
		return new NotificationCompat.Builder(context)
				.setContentIntent(getClickIntent())
				.setSmallIcon(android.R.drawable.ic_menu_camera)
				.setContentTitle(appName)
				.setContentText(connectedText + " " + server.getAddress() + ":" + server.getPort())
				.setPriority(PRIORITY_LOW)
				.setTicker(connectedTicker)
				.addAction(getDisconnectAction())
				.addAction(getRemoteControlAction())
				.build();
	}

	private NotificationCompat.Action getDisconnectAction() {
		return new NotificationCompat.Action(
				android.R.drawable.ic_menu_close_clear_cancel,
				"disconnect",
				PendingIntent.getService(context, 0, NetworkManager_.intent(context).action(NetworkManager.DISCONNECT_ACTION).get(), 0));
	}

	private PendingIntent getClickIntent() {
		return PendingIntent.getActivity(context, 0, MainActivity_.intent(context).get(), 0);
	}

	private NotificationCompat.Action getRemoteControlAction() {
		return new NotificationCompat.Action(
				android.R.drawable.ic_media_play,
				"remote control",
				PendingIntent.getActivity(context, 0, RemoteContolActivity_.intent(context).get(), 0));
	}
}
