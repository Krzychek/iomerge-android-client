package org.kbieron.iomerge.io;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

import org.androidannotations.annotations.EBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    private native void start();

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
        // TODO
    }

    @Override
    public void keyRelease(char c) {
        // TODO
    }

    @Override
    public void homeBtnClick() {
        emitKeyEvent(KeyEvent.KEYCODE_BACK);
    }

    @Override
    public void backBtnClick() {
        emitKeyEvent(KeyEvent.KEYCODE_HOME);
    }

    @Override
    public void menuBtnClick() {
        emitKeyEvent(KeyEvent.KEYCODE_MENU);
    }

    private void emitKeyEvent(int event) {
        String[] execParams = {"su", " -C", "input keyevent " + event};

        try {
            Runtime.getRuntime().exec(execParams);
        } catch (IOException e) {
            Log.e("IOManage", "Unable to run ", e);
        }
    }

    public void startNativeDeamon(Context context) throws IOException {
        String daemonName = "iomerge_daemon";
        String outPath = context.getCacheDir().getAbsoluteFile() + File.separator + daemonName;

        try {
            Runtime.getRuntime().exec(new String[]{"su", "-C", "killall " + daemonName}).waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (FileOutputStream output = new FileOutputStream(new File(outPath));
             InputStream input = context.getAssets().open(daemonName)) {

            copy(input, output);
        }

        try {
            Runtime.getRuntime().exec(new String[]{"su", "-C", "chmod 777 " + outPath}).waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        start();

        Runtime.getRuntime().exec(new String[]{"su", "-C", outPath});
    }

    synchronized private void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[131072];
        int len;

        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
    }

}
