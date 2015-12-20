package org.kbieron.iomerge.io;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

import org.androidannotations.annotations.EBean;
import org.kbieron.iomerge.services.RemoteActionProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


@EBean(scope = EBean.Scope.Singleton)
public class InputDevice extends RemoteActionProcessor {

    public static final String DAEMON_NAME = "iomerge_daemon";

    static {
        System.loadLibrary("native");
    }

    private native void initializePipe();

    public void stopGently() {

        try {
            //noinspection SpellCheckingInspection
            Runtime.getRuntime().exec(new String[]{"su", "-C", "killall " + DAEMON_NAME}).waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public native void mouseMove(short x, short y);

    @Override
    public native void mousePress();

    @Override
    public native void mouseRelease();

    @Override
    public native void keyPress(int c);

    @Override
    public native void keyRelease(int c);

    @Override
    public void homeBtnClick() {
        emitKeyEvent(KeyEvent.KEYCODE_HOME);
    }

    @Override
    public void backBtnClick() {
        emitKeyEvent(KeyEvent.KEYCODE_BACK);
    }

    @Override
    public void menuBtnClick() {
        emitKeyEvent(KeyEvent.KEYCODE_MENU);
    }

    private void emitKeyEvent(int event) {
        //noinspection SpellCheckingInspection
        String[] execParams = {"su", " -C", "input keyevent " + event};

        try {
            Runtime.getRuntime().exec(execParams);
        } catch (IOException e) {
            Log.e("IOManage", "Unable to run ", e);
        }
    }

    public void startNativeDaemon(Context context) throws IOException {
        String outPath = context.getCacheDir().getAbsoluteFile() + File.separator + DAEMON_NAME;

        stopGently();

        try (FileOutputStream output = new FileOutputStream(new File(outPath));
             InputStream input = context.getAssets().open(DAEMON_NAME)) {

            copy(input, output);

            Runtime.getRuntime().exec(new String[]{"su", "-C", "chmod 777 " + outPath}).waitFor();

            initializePipe();

            Runtime.getRuntime().exec(new String[]{"su", "-C", outPath});
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized private void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[32768];
        int len;

        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
    }

}
