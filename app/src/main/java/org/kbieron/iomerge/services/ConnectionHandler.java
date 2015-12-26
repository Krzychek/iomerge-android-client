package org.kbieron.iomerge.services;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import pl.kbieron.iomerge.model.RemoteMsgTypes;

import static pl.kbieron.iomerge.model.RemoteMsgTypes.BACK_BTN_CLICK;
import static pl.kbieron.iomerge.model.RemoteMsgTypes.CLIPBOARD_SYNC;
import static pl.kbieron.iomerge.model.RemoteMsgTypes.HOME_BTN_CLICK;
import static pl.kbieron.iomerge.model.RemoteMsgTypes.KEY_PRESS;
import static pl.kbieron.iomerge.model.RemoteMsgTypes.KEY_RELEASE;
import static pl.kbieron.iomerge.model.RemoteMsgTypes.MENU_BTN_CLICK;
import static pl.kbieron.iomerge.model.RemoteMsgTypes.MOUSE_PRESS;
import static pl.kbieron.iomerge.model.RemoteMsgTypes.MOUSE_RELEASE;
import static pl.kbieron.iomerge.model.RemoteMsgTypes.MOUSE_SYNC;
import static pl.kbieron.iomerge.model.RemoteMsgTypes.MOUSE_WHEEL;


@EBean(scope = EBean.Scope.Singleton)
class ConnectionHandler implements ClipboardManager.OnPrimaryClipChangedListener {

    @Bean
    protected InputDevice inputDevice;

    @SystemService
    protected ClipboardManager clipboardManager;

    private ObjectOutputStream serverOutputStream;

    private Socket client;


    public void startReceiving(Socket client) throws IOException {
        this.client = client;
        serverOutputStream = new ObjectOutputStream(client.getOutputStream());
        clipboardManager.addPrimaryClipChangedListener(this);

        ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
        byte[] msg;
        while (true) {
            try {
                msg = (byte[]) objectInputStream.readObject();

                if (msg != null) {
                    process(msg);
                } else break;
            } catch (ClassNotFoundException | ClassCastException e) {
                Log.w("NetworkManager", "problem while receiving msg", e);

            }
        }
    }

    public void process(byte[] msg) {
        ByteBuffer msgBuffer = ByteBuffer.wrap(msg);
        byte actionType = msgBuffer.get();

        switch (actionType) {
            case MOUSE_SYNC:
                inputDevice.mouseMove(msgBuffer.getShort(), msgBuffer.getShort());
                break;
            case MOUSE_PRESS:
                inputDevice.mousePress();
                break;
            case MOUSE_RELEASE:
                inputDevice.mouseRelease();
                break;
            case KEY_PRESS:
                inputDevice.keyPress(msgBuffer.getInt());
                break;
            case KEY_RELEASE:
                inputDevice.keyRelease(msgBuffer.getInt());
                break;
            case HOME_BTN_CLICK:
                inputDevice.emitKeyEvent(KeyEvent.KEYCODE_HOME);
                break;
            case BACK_BTN_CLICK:
                inputDevice.emitKeyEvent(KeyEvent.KEYCODE_BACK);
                break;
            case MENU_BTN_CLICK:
                inputDevice.emitKeyEvent(KeyEvent.KEYCODE_MENU);
                break;
            case MOUSE_WHEEL:
                inputDevice.mouseWheel(msgBuffer.getInt());
                break;
            case CLIPBOARD_SYNC:
                setClipboardText(new String(msgBuffer.array(), msgBuffer.arrayOffset() + msgBuffer.position(), msgBuffer.remaining()));
                break;
        }
    }


    private void setClipboardText(String text) {
        clipboardManager.removePrimaryClipChangedListener(this);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("IOMerge", text));
        clipboardManager.addPrimaryClipChangedListener(this);
    }

    @Override
    public void onPrimaryClipChanged() {
        byte[] clipboardBytes = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString().getBytes();
        byte[] msg = new byte[clipboardBytes.length + 1];
        msg[0] = CLIPBOARD_SYNC;
        System.arraycopy(clipboardBytes, 0, msg, 1, clipboardBytes.length);
        try {
            serverOutputStream.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendExit() {
        try {
            serverOutputStream.writeObject(new byte[]{RemoteMsgTypes.REMOTE_EXIT});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.close();
        } catch (IOException ignored) {}

        clipboardManager.removePrimaryClipChangedListener(this);
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }
}
