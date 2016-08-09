package org.kbieron.iomerge.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;
import android.view.WindowManager;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.kbieron.iomerge.database.ServerBean;
import org.kbieron.iomerge.notifications.NotificationFactory;
import org.kbieron.iomerge.views.EdgeTriggerView;


@EService
public class NetworkManager extends Service {

	public static final String DISCONNECT_ACTION = "DISCONNECT";
	public static final String CONNECT_ACTION = "CONNECT";

	public static final String SERVER_EXTRA = "SERVER";

	@Bean
	ConnectionHandler connectionHandler;

	@Bean
	InputDevice inputDevice;

	@Bean
	NotificationFactory notificationFactory;

	@SystemService
	WindowManager windowManager;

	@Bean
	EdgeTriggerView edgeTriggerView;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent.getAction() != null) switch (intent.getAction()) {
			case DISCONNECT_ACTION:
				disconnect();
				break;
			case CONNECT_ACTION:
				connect((ServerBean) intent.getSerializableExtra(SERVER_EXTRA));
				break;
			default:
				Log.w("NetworkManager", "Uknown intent action: " + intent.getAction());
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public Binder onBind(Intent intent) {
		return new Binder();
	}

	@Override
	public void onDestroy() {
		disconnect();
	}

	@Background
	void connect(ServerBean server) {
		if (!connectionHandler.isConnected()) {

			try {
				// connect
				connectionHandler.connect(server, this);
				// show notification
				startForeground(1, notificationFactory.serverConnected(server));

			} catch (InterruptedException e) {
				Log.i("NetworkManager", "disconnected", e);
				disconnect();
			}

		} else {
			Log.i("NetworkManager", "already connected");
		}
	}


	@Background
	public void disconnect() {
		Log.i("NetworkManager", "Disconnecting");
		edgeTriggerView.hide();
		inputDevice.stop();
		stopForeground(true);
	}
}
