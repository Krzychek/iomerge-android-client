package org.kbieron.iomerge.services;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.kbieron.iomerge.views.EdgeTriggerView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import pl.kbieron.iomerge.model.Edge;
import pl.kbieron.iomerge.model.MessageProcessorAdapter;
import pl.kbieron.iomerge.model.message.Message;
import pl.kbieron.iomerge.model.message.misc.ClipboardSync;
import pl.kbieron.iomerge.model.message.misc.RemoteExit;


@EBean(scope = EBean.Scope.Singleton)
class ConnectionHandler extends MessageProcessorAdapter implements ClipboardManager.OnPrimaryClipChangedListener, EdgeTriggerView.OnTrigListener {

    @Bean
    protected InputDevice inputDevice;

    @Bean
    protected EdgeTriggerView edgeTrigger;

    @SystemService
    protected ClipboardManager clipboardManager;

    private ObjectOutputStream serverOutputStream;

    private Socket client;


    public void startReceiving(Socket client) throws IOException {
        this.client = client;
        serverOutputStream = new ObjectOutputStream(client.getOutputStream());
        clipboardManager.addPrimaryClipChangedListener(this);

        ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
        while (true) {
            try {
                ((Message) objectInputStream.readObject()).process(this);
            } catch (ClassNotFoundException | ClassCastException e) {
                Log.w("NetworkManager", "problem while receiving msg", e);
            }
        }
    }

    @Override
    public void mousePress() {
        inputDevice.mousePress();
    }

    @Override
    public void mouseRelease() {
        inputDevice.mouseRelease();
    }

    @Override
    public void mouseSync(int x, int y) {
        inputDevice.mouseMove(x, y);
    }

    @Override
    public void mouseWheel(int move) {
        inputDevice.mouseWheel(move);
    }

    @Override
    public void backBtnClick() {
        inputDevice.emitKeyEvent(KeyEvent.KEYCODE_BACK);
    }

    @Override
    public void homeBtnClick() {
        inputDevice.emitKeyEvent(KeyEvent.KEYCODE_HOME);
    }

    @Override
    public void menuBtnClick() {
        inputDevice.emitKeyEvent(KeyEvent.KEYCODE_MENU);
    }

    @Override
    public void edgeSync(Edge edge) {
        edgeTrigger.showOrMove(edge);
    }

    @Override
    public void keyPress(int keyCode) {
        inputDevice.keyPress(keyCode);
    }

    @Override
    public void keyRelease(int keyCode) {
        inputDevice.keyRelease(keyCode);
    }

    @Override
    public void clipboardSync(String text) {
        clipboardManager.removePrimaryClipChangedListener(this);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("IOMerge", text));
        clipboardManager.addPrimaryClipChangedListener(this);
    }

    @Override
    public void onPrimaryClipChanged() {
        String clipboardText = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
        try {
            serverOutputStream.writeObject(new ClipboardSync(clipboardText));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendExit() {
        try {
            serverOutputStream.writeObject(new RemoteExit());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (client != null) client.close();
        } catch (IOException ignored) {}

        clipboardManager.removePrimaryClipChangedListener(this);
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    @Override
    public void onTrig() {
        sendExit();
    }
}
