package org.kbieron.iomerge.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.kbieron.iomerge.Preferences_;
import org.kbieron.iomerge.notifications.NotificationFactory;
import org.kbieron.iomerge.ui.EdgeTriggerView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


@EService
public class NetworkManager extends Service {

    @Bean
    protected ConnectionHandler connectionHandler;

    @Bean
    protected InputDevice inputDevice;

    @Bean
    protected NotificationFactory notificationFactory;

    @Pref
    protected Preferences_ prefs;

    @SystemService
    protected WindowManager windowManager;

    private EdgeTriggerView edgeTriggerView;

    @Override
    public Binder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onDestroy() {
        disconnect();
    }

    @AfterInject
    protected void init() {
        edgeTriggerView = new EdgeTriggerView(this, new Runnable() {
            @Override
            public void run() {
                connectionHandler.sendExit();
            }
        });
    }

    @Background
    public void connect() {
        if (!connectionHandler.isConnected()) {
            startForeground(1, notificationFactory.serverConnected(this));

            try {
                inputDevice.startNativeDaemon();

                Socket client = new Socket();
                client.connect(new InetSocketAddress(prefs.serverAddress().get(), prefs.serverPort().get()));

                showEdgeTrigger();

                connectionHandler.startReceiving(client);

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
    protected void showEdgeTrigger() {
        windowManager.addView(edgeTriggerView, EdgeTriggerView.getDefaultLayoutParams());
    }

    @Background
    public void disconnect() {
        Log.i("NetworkManager", "Disconnecting");
        windowManager.removeView(edgeTriggerView);
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
