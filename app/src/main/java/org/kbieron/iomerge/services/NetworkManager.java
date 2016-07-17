package org.kbieron.iomerge.services;

import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
import com.github.krzychek.server.model.Edge;
import com.github.krzychek.server.model.serialization.MessageIOFacade;
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
public abstract class NetworkManager extends Service {

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
			startForeground(1, notificationFactory.serverConnected(prefs.serverAddress().get(), prefs.serverPort().get()));

			try {
				inputDevice.startNativeDaemon();

				showEdgeTrigger(connectionHandler);
				connectionHandler.connect(new MessageIOFacade(prefs.serverAddress().get(), prefs.serverPort().get()));

			} catch (IOException | InterruptedException e) {
				Log.i("NetworkManager", "disconnected", e);
			} finally {
				disconnect();
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

		public NetworkManager getService() {
			return NetworkManager.this;
		}
	}
}
