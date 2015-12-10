package org.kbieron.iomerge.android;

import android.util.Log;
import android.view.KeyEvent;

import java.io.IOException;

import pl.kbieron.iomerge.iLipeRMI.IClient;
import pl.kbieron.iomerge.model.ClientAction;

public class IOManager implements IClient {

    static {
        System.loadLibrary("native");
    }

    @Override
    public void action(ClientAction clientAction) {
        switch (clientAction) {
            case HOME_BTN:
                emitKeyEvent(KeyEvent.KEYCODE_HOME);
                break;
            case BACK_BTN:
                emitKeyEvent(KeyEvent.KEYCODE_BACK);
                break;
            case MENU_BTN:
                emitKeyEvent(KeyEvent.KEYCODE_BACK);
                break;
            case MOUSE_PRESSED:
                // TODO
                break;
            case MOUSE_RELEASED:
                // TODO
                break;
            case MOUSE_CLICK:
                // TODO
                break;
        }
    }

    @Override
    public native void moveMouse(int x, int y);

    private void emitKeyEvent(int event) {
        String[] execParams = {"su", " -C", "input keyevent " + event};

        try {
            Runtime.getRuntime().exec(execParams);
        } catch (IOException e) {
            Log.e("IOManage", "Unable to run ", e);
        }
    }
}
