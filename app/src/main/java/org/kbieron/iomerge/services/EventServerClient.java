package org.kbieron.iomerge.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.kbieron.iomerge.Preferences_;
import org.kbieron.iomerge.io.InputDevice;
import org.kbieron.iomerge.ui.EdgeTriggerView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import pl.kbieron.iomerge.model.RemoteMsgTypes;


@EService
public class EventServerClient extends Service {

    @Bean
    protected InputDevice inputDevice;

    @Pref
    protected Preferences_ prefs;

    @SystemService
    protected NotificationManager notificationManager;

    private Socket client;

    private ObjectOutputStream serverOutputStream;

    private EdgeTriggerView edgeTriggerView;

    @SystemService
    protected WindowManager windowManager;

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
        if (client != null && client.isConnected()) {
            Log.w("EventServerClient", "already connected");
            return;
        }

        try {
            inputDevice.startNativeDaemon(getApplicationContext());

            client = new Socket();
            client.connect(new InetSocketAddress(prefs.serverAddress().get(), prefs.serverPort().get()));

            serverOutputStream = new ObjectOutputStream(client.getOutputStream());
            createEdgeTrigger();

            startReceiving();

        } catch (IOException | InterruptedException e) {
            Log.i("EventServerClient", "disconnected", e);
        } finally {
            disconnect();
        }
    }

    private void startReceiving() throws IOException {
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
    }

    @UiThread
    public void createEdgeTrigger() {
        edgeTriggerView = new EdgeTriggerView(this, new Runnable() {
            @Override
            public void run() {
                sendExit();
            }
        });
        windowManager.addView(edgeTriggerView, EdgeTriggerView.getDefaultLayoutParams());
    }

    private void sendExit() {
        try {
            serverOutputStream.writeObject(new byte[]{RemoteMsgTypes.REMOTE_EXIT});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Background
    public void disconnect() {
        if (edgeTriggerView != null) windowManager.removeView(edgeTriggerView);

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
