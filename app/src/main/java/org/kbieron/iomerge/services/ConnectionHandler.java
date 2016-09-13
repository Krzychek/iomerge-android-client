package org.kbieron.iomerge.services;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import com.github.krzychek.iomerge.server.model.Edge;
import com.github.krzychek.iomerge.server.model.MessageProcessorAdapter;
import com.github.krzychek.iomerge.server.model.message.misc.ClipboardSync;
import com.github.krzychek.iomerge.server.model.message.misc.Heartbeat;
import com.github.krzychek.iomerge.server.model.message.misc.RemoteExit;
import com.github.krzychek.iomerge.server.model.message.mouse.MouseButton;
import com.github.krzychek.iomerge.server.model.message.mouse.MouseMove;
import com.github.krzychek.iomerge.server.model.message.mouse.MousePress;
import com.github.krzychek.iomerge.server.model.message.mouse.MouseRelease;
import com.github.krzychek.iomerge.server.model.message.mouse.MouseWheel;
import com.github.krzychek.iomerge.server.model.serialization.MessageSocketWrapper;
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

	private void startHeartbeatTimer(final NetworkManager networkManager) {
		final Heartbeat message = new Heartbeat();
		heartbeatTimer = new ScheduledThreadPoolExecutor(1);
		heartbeatTimer.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					socket.sendMessage(message);
				} catch (IOException e) {
					Log.i("Heartbeattimer", "IOException while sending hearbeat", e);
					disconnect(networkManager);
				}
			}
		}, 2, 2, TimeUnit.SECONDS);
	}


	void connect(ServerBean server, final NetworkManager networkManager) {
		try {
			socket = new MessageSocketWrapper(server.getAddress(), server.getPort());

			clipboardManager.addPrimaryClipChangedListener(this);
			startHeartbeatTimer(networkManager);

			// start daemon
			inputDevice.startNativeDaemon();

			new Thread(new Runnable() {
				@Override
				public void run() {

					while (isConnected()) {
						try {
							socket.readMessage().process(ConnectionHandler.this);

						} catch (EOFException e) {
							disconnect(networkManager);

						} catch (ClassNotFoundException | IOException e) {
							Log.w("NetworkManager", "problem while receiving msg", e);
						}
					}

				}
			}).start();

		} catch (IOException | InterruptedException e) {
			Log.e("ConnectionHandler", "problem with connection", e);
			disconnect(networkManager);
		}
	}

	@Override
	public void mousePress(MouseButton button) {
		inputDevice.mousePress(getButton(button));
	}

	private int getButton(MouseButton button) {
		return 0;
	}

	@Override
	public void mouseRelease(MouseButton button) {
		inputDevice.mouseRelease(getButton(button));
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

	public void sendMouseMove(int x, int y) {
		try {
			socket.sendMessage(new MouseMove(x, y));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMousePress(MouseButton button) {
		try {
			socket.sendMessage(new MousePress(button));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMouseRelease(MouseButton button) {
		try {
			socket.sendMessage(new MouseRelease(button));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void sendMouseWheel(int dx) {
		try {
			socket.sendMessage(new MouseWheel(dx));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void disconnect(NetworkManager networkManager) {
		try {
			if (socket != null) socket.close();
		} catch (IOException ignored) {
		}
		socket = null;

		if (heartbeatTimer != null) heartbeatTimer.shutdownNow();
		clipboardManager.removePrimaryClipChangedListener(this);
		networkManager.disconnect();
	}

	boolean isConnected() {
		return socket != null && !socket.isClosed();
	}
}
