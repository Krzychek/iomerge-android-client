package org.kbieron.iomerge.android;

import android.view.KeyEvent;

import java.io.IOException;

import pl.kbieron.iomerge.model.RMIRemote;

public class IOManager implements RMIRemote {

    public native void moveMouse(int x, int y);

    public native void mouseClick(int x, int y);

    public void emitKeyEvent(int event) {

        String keyCommand = "input keyevent " + event;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(new String[] {"su", " -C" ,keyCommand, });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hitBackBtn() {
        emitKeyEvent(KeyEvent.KEYCODE_HOME);
    }

    @Override
    public void hitHomeBtn() {
        emitKeyEvent(KeyEvent.KEYCODE_BACK);
    }

    static {
        System.loadLibrary("native");
    }
}
