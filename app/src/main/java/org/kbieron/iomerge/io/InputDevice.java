package org.kbieron.iomerge.io;

import android.util.Log;
import android.view.KeyEvent;

import org.androidannotations.annotations.EBean;

import java.io.IOException;

import pl.kbieron.iomerge.model.RemoteActionProcessor;


@EBean(scope = EBean.Scope.Singleton)
public class InputDevice extends RemoteActionProcessor {

    static {
        System.loadLibrary("native");
    }

    @Override
    public native void mouseMove(short x, short y);

    public native void stop();

    @Override
    public native void mousePress();

    @Override
    public native void mouseRelease();

    @Override
    public void keyPress(char c) {
        String[] execParams = {"su", " -C", "input text " + c};

        try {
            Runtime.getRuntime().exec(execParams);
        } catch (IOException e) {
            Log.e("IOManage", "Unable to run ", e);
        }
    }

    @Override
    public void keyRelease(char c) {
        // TODO
    }

    public void action(int x) {
        switch (x) {
            case 0:
                emitKeyEvent(KeyEvent.KEYCODE_HOME);
                break;
            case 1:
                emitKeyEvent(KeyEvent.KEYCODE_BACK);
                break;
            case 2:
                emitKeyEvent(KeyEvent.KEYCODE_BACK);
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
