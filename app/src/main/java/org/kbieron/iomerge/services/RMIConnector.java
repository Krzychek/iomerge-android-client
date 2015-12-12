package org.kbieron.iomerge.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;


import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;
import org.kbieron.iomerge.rmi.IOManager;

import java.io.IOException;

import pl.kbieron.iomerge.iLipeRMI.IClient;
import pl.kbieron.iomerge.iLipeRMI.IServer;

@EService
public class RMIConnector extends Service {

    private IServer server;

    private Client client;

    @Bean
    protected IOManager ioManager;

    @SystemService
    NotificationManager notificationManager;

    @Override
    public Binder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onDestroy() {
        disconnect();
    }

    public void connect(String address, int port) {
        CallHandler callHandler = new CallHandler();

        try {
            callHandler.exportObject(IClient.class, ioManager);
            Log.i("RMIConnector Service", "IClient exported");

            client = new Client(address, port, callHandler);
            Log.i("RMIConnector Service", "RMI connected to server");

            server = (IServer) client.getGlobal(IServer.class);
            server.setClient(ioManager, 400, 400);
            Log.i("RMIConnector Service", "ioManager connected to server");

        } catch (IOException | LipeRMIException e) {
            Log.e("RMIConnector Service", "Filed to connect", e);
            disconnect();
        }
        notificationManager.notify();
    }

    public void disconnect() {
        Log.i("RMIConnector Service", "Disconnecting");
        try {
            client.close();
        } catch (IOException ignored) {}
        client = null;
        server = null;
        ioManager.stop();
    }


    public class Binder extends android.os.Binder {
        public RMIConnector getService() {
            return RMIConnector.this;
        }

    }
}
