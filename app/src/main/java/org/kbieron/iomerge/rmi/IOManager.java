package org.kbieron.iomerge.rmi;

import android.util.Log;
import android.view.KeyEvent;

import org.androidannotations.annotations.EBean;

import java.io.IOException;

import pl.kbieron.iomerge.iLipeRMI.IClient;
import pl.kbieron.iomerge.model.ClientAction;


@EBean(scope = EBean.Scope.Singleton)
public class IOManager implements IClient {

    static {
        System.loadLibrary("native");
    }

    @Override
    public native void moveMouse(int x, int y);

    public native void stop();

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

    private void emitKeyEvent(int event) {
        String[] execParams = {"su", " -C", "input keyevent " + event};

        try {
            Runtime.getRuntime().exec(execParams);
        } catch (IOException e) {
            Log.e("IOManage", "Unable to run ", e);
        }
    }
}
