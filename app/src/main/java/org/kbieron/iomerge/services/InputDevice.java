package org.kbieron.iomerge.services;

import android.content.Context;
import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


@EBean(scope = EBean.Scope.Singleton)
public class InputDevice {

	private static final String DAEMON_NAME = "iomerge_daemon";

	static {
		System.loadLibrary("native");
	}

	@RootContext
	protected Context context;

	synchronized protected void startNativeDaemon() throws IOException, InterruptedException {
		stop();

		String outPath = context.getCacheDir().getAbsolutePath() + File.separator + DAEMON_NAME;
		try (FileOutputStream output = new FileOutputStream(new File(outPath))) {
			copy(context.getAssets().open(DAEMON_NAME), output);
		}

		Runtime.getRuntime().exec(new String[]{"su", "-C", "chmod 777 " + outPath}).waitFor();
		initializePipe();

		Runtime.getRuntime().exec(new String[]{"su", "-C", outPath});
	}

	synchronized public void stop() {

		try {
			//noinspection SpellCheckingInspection
			Runtime.getRuntime().exec(new String[]{"su", "-C", "killall " + DAEMON_NAME}).waitFor();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

	private native void initializePipe();

	native void mouseMove(int x, int y);

	native void mousePress();

	native void mouseRelease();

	native void keyPress(int c);

	native void keyRelease(int c);

	native void mouseWheel(int anInt);

	void emitKeyEvent(int event) {
		//noinspection SpellCheckingInspection
		String[] execParams = {"su", " -C", "input keyevent " + event};

		try {
			Runtime.getRuntime().exec(execParams);
		} catch (IOException e) {
			Log.e("IOManage", "Unable to run ", e);
		}
	}

	private void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[32768];
		int len;

		while ((len = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
	}

	native void keyClick(int i);
}
