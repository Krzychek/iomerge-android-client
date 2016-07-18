package org.kbieron.iomerge.services;

import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
import com.github.krzychek.server.model.Edge;
import com.github.krzychek.server.model.serialization.MessageSocketWrapper;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.kbieron.iomerge.Preferences_;
import org.kbieron.iomerge.notifications.NotificationFactory;
import org.kbieron.iomerge.views.EdgeTriggerView;

import java.io.IOException;


@EService
public class NetworkManager extends Service {

	public static final String DISCONNECT_ACTION = "DISCONNECT";

	@Bean
	ConnectionHandler connectionHandler;

	@Bean
	InputDevice inputDevice;

	@Bean
	NotificationFactory notificationFactory;

	@Pref
	Preferences_ prefs;

	@SystemService
	WindowManager windowManager;

	@Bean
	EdgeTriggerView edgeTriggerView;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (DISCONNECT_ACTION.equals(intent.getAction()))
			disconnect();

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
	public void connect() {
		if (!connectionHandler.isConnected()) {
			String address = prefs.serverAddress().get();
			Integer port = prefs.serverPort().get();

			startForeground(1, notificationFactory.serverConnected(address, port));

			try {
				inputDevice.startNativeDaemon();

				showEdgeTrigger(connectionHandler);
				connectionHandler.connect(new MessageSocketWrapper(address, port));

			} catch (IOException | InterruptedException e) {
				Log.i("NetworkManager", "disconnected", e);
			}

		} else {
			Log.i("NetworkManager", "already connected");
		}
	}

	@UiThread
	protected void showEdgeTrigger(EdgeTriggerView.OnTrigListener onTrigListener) {
		edgeTriggerView.setOnTrigListener(onTrigListener);
		edgeTriggerView.showOrMove(Edge.LEFT);
	}

	@Background
	public void disconnect() {
		Log.i("NetworkManager", "Disconnecting");
		if (edgeTriggerView.isAttachedToWindow()) {
			windowManager.removeView(edgeTriggerView);
		}
		connectionHandler.disconnect();
		inputDevice.stop();
		stopForeground(true);
	}


	public class Binder extends android.os.Binder {

		public void connect() {
			NetworkManager.this.connect();
		}

		public void disconnect() {
			NetworkManager.this.disconnect();
		}
	}
}
