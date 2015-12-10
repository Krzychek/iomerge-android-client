package org.kbieron.iomerge.android;

import android.os.AsyncTask;

import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import java.io.IOException;

import pl.kbieron.iomerge.model.RMIRemote;
import pl.kbieron.iomerge.model.RMIServerIface;

public class RMIConnector {
    public RMIConnector() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CallHandler callHandler = new CallHandler();

                    IOManager exportedObject = new IOManager();
                    callHandler.exportObject(RMIRemote.class, exportedObject);

                    RMIServerIface rmiServerIface = (RMIServerIface) new Client("192.168.1.135", 7777, callHandler).getGlobal(RMIServerIface.class);
                    rmiServerIface.setRemote(exportedObject);

                } catch (IOException | LipeRMIException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
