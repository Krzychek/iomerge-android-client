package org.kbieron.iomerge.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.kbieron.iomerge.io.InputDevice;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


@EService
public class RMIConnector extends Service {

    @Bean
    protected InputDevice inputDevice;

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
        connect("192.168.1.135", 7698);
        return super.onStartCommand(intent, flags, startId);
    }

    @Background
    public void connect(String host, int port) {
        if (client != null && client.isConnected()) {
            Log.i("connect", "already connected");
            return;
        }

        client = new Socket();
        try {
            client.connect(new InetSocketAddress(host, port));

            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());

            byte[] msg;
            while (true) {
                msg = (byte[]) objectInputStream.readObject();

                if (msg != null) {
                    inputDevice.process(msg);
                } else break;
            }
            disconnect();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (client.isConnected()) {
            Log.i("RMIConnector Service", "Disconnecting");
            try {
                client.close();
            } catch (IOException ignored) {}
            client = null;
        }
        inputDevice.stop();
    }


    public class Binder extends android.os.Binder {

        public RMIConnector getService() {
            return RMIConnector.this;
        }

    }
}
