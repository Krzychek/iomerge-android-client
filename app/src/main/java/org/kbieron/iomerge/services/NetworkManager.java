package org.kbieron.iomerge.services;

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
import org.kbieron.iomerge.views.EdgeTriggerView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import pl.kbieron.iomerge.model.Edge;


@EService
public class NetworkManager extends Service implements EdgeTriggerView.OnTrigListener {

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

    @Bean
    protected EdgeTriggerView edgeTriggerView;

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
        edgeTriggerView.setOnTrigListener(this);
    }

    @Background
    public void connect() {
        if (!connectionHandler.isConnected()) {
            startForeground(1, notificationFactory.serverConnected(prefs.serverAddress().get(), prefs.serverPort().get()));

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
        edgeTriggerView.showOrMove(Edge.LEFT);
    }

    @Background
    public void disconnect() {
        Log.i("NetworkManager", "Disconnecting");
        windowManager.removeView(edgeTriggerView);
        connectionHandler.disconnect();
        inputDevice.stop();
        stopForeground(true);
    }

    @Override
    public void onTrig() {
        connectionHandler.sendExit();
    }


    public class Binder extends android.os.Binder {

        public NetworkManager getService() {
            return NetworkManager.this;
        }
    }
}
