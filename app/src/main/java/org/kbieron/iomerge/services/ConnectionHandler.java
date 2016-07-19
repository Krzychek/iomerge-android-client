package org.kbieron.iomerge.services;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import com.github.krzychek.server.model.Edge;
import com.github.krzychek.server.model.MessageProcessorAdapter;
import com.github.krzychek.server.model.message.misc.ClipboardSync;
import com.github.krzychek.server.model.message.misc.Heartbeat;
import com.github.krzychek.server.model.message.misc.RemoteExit;
import com.github.krzychek.server.model.serialization.MessageSocketWrapper;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.kbieron.iomerge.database.ServerBean;
import org.kbieron.iomerge.views.EdgeTriggerView;

import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@EBean(scope = EBean.Scope.Singleton)
public class ConnectionHandler extends MessageProcessorAdapter implements ClipboardManager.OnPrimaryClipChangedListener {

	@Bean
	InputDevice inputDevice;

	@Bean
	EdgeTriggerView edgeTrigger;

	@SystemService
	ClipboardManager clipboardManager;

	private MessageSocketWrapper socket;
	private ScheduledThreadPoolExecutor heartbeatTimer;

	private void startHeartbeatTimer() {
		final Heartbeat message = new Heartbeat();
		heartbeatTimer = new ScheduledThreadPoolExecutor(1);
		heartbeatTimer.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					socket.sendMessage(message);
				} catch (IOException e) {
					Log.i("Heartbeattimer", "IOException while sending hearbeat", e);
					disconnect();
				}
			}
		}, 2, 2, TimeUnit.SECONDS);
	}


	void connect(ServerBean server) throws IOException, InterruptedException {
		socket = new MessageSocketWrapper(server.getAddress(), server.getPort());
		clipboardManager.addPrimaryClipChangedListener(this);
		startHeartbeatTimer();

		// start daemon
		inputDevice.startNativeDaemon();

		new Thread(new Runnable() {
			@Override
			public void run() {

				while (!socket.isClosed()) {
					try {
						socket.getMessage().process(ConnectionHandler.this);

					} catch (EOFException e) {
						disconnect();

					} catch (ClassNotFoundException | IOException e) {
						Log.w("NetworkManager", "problem while receiving msg", e);
					}
				}

			}
		}).start();
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
	public void mouseMove(int x, int y) {
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
	public void keyClick(int i) {
		inputDevice.keyClick(i);
	}

	@Override
	public void onPrimaryClipChanged() {
		String clipboardText = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
		try {
			socket.sendMessage(new ClipboardSync(clipboardText));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendExit(float v) {
		try {
			socket.sendMessage(new RemoteExit(v));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void disconnect() {
		try {
			if (socket != null) socket.close();
		} catch (IOException ignored) {
		}
		inputDevice.stop();
		if (heartbeatTimer != null) heartbeatTimer.shutdownNow();

		clipboardManager.removePrimaryClipChangedListener(this);
	}

	boolean isConnected() {
		return socket != null && !socket.isClosed();
	}
}
