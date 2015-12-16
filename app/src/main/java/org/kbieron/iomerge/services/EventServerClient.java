package org.kbieron.iomerge.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.kbieron.iomerge.Preferences_;
import org.kbieron.iomerge.io.InputDevice;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


@EService
public class EventServerClient extends Service {

    @Bean
    protected InputDevice inputDevice;

    @Pref
    Preferences_ preferences;

    @SystemService
    NotificationManager notificationManager;

    private Socket client;

    @Override
    public Binder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onDestroy() {
        disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Background
    public void connect(String host, int port) {
        if (client != null && client.isConnected()) {
            Log.w("EventServerClient", "already connected");
            return;
        }

        try {
            inputDevice.startNativeDaemon(getApplicationContext());

            client = new Socket();

            client.connect(new InetSocketAddress(preferences.serverAddress().get(), preferences.serverPort().get()));

            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());

            byte[] msg;
            while (true) {
                try {
                    msg = (byte[]) objectInputStream.readObject();

                    if (msg != null) {
                        inputDevice.process(msg);
                    } else break;
                } catch (ClassNotFoundException e) {
                    Log.w("EventServerClient", "problem while receiving msg", e);

                }
            }

        } catch (IOException e) {
            Log.i("EventServerClient", "disconnected");
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    @Background
    public void disconnect() {
        if (client != null && client.isConnected()) {
            Log.i("EventServerClient", "Disconnecting");
            try {
                client.close();
            } catch (IOException ignored) {}
            client = null;
        }
        inputDevice.stopGently();
    }


    public class Binder extends android.os.Binder {

        public EventServerClient getService() {
            return EventServerClient.this;
        }

    }
}
