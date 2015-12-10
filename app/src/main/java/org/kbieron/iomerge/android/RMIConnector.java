package org.kbieron.iomerge.android;

import android.os.AsyncTask;

import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import java.io.IOException;

import pl.kbieron.iomerge.iLipeRMI.IClient;
import pl.kbieron.iomerge.iLipeRMI.IServer;

public class RMIConnector {
    public RMIConnector() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CallHandler callHandler = new CallHandler();

                    IOManager exportedObject = new IOManager();
                    callHandler.exportObject(IClient.class, exportedObject);

                    IServer server = (IServer) new Client("192.168.1.135", 7777, callHandler).getGlobal(IServer.class);
                    server.setClient(exportedObject, 400, 400);

                } catch (IOException | LipeRMIException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
