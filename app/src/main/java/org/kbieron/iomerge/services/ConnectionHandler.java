package org.kbieron.iomerge.services;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import com.github.krzychek.server.model.Edge;
import com.github.krzychek.server.model.MessageProcessorAdapter;
import com.github.krzychek.server.model.message.misc.ClipboardSync;
import com.github.krzychek.server.model.message.misc.RemoteExit;
import com.github.krzychek.server.model.serialization.MessageIOFacade;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.kbieron.iomerge.views.EdgeTriggerView;

import java.io.IOException;


@EBean(scope = EBean.Scope.Singleton)
class ConnectionHandler extends MessageProcessorAdapter implements ClipboardManager.OnPrimaryClipChangedListener, EdgeTriggerView.OnTrigListener {

	@Bean
	InputDevice inputDevice;

	@Bean
	EdgeTriggerView edgeTrigger;

	@SystemService
	ClipboardManager clipboardManager;

	private MessageIOFacade messageIOFacade;


	void connect(MessageIOFacade client) throws IOException {
		messageIOFacade = client;
		clipboardManager.addPrimaryClipChangedListener(this);

		while (true) {
			try {
				messageIOFacade.getMessage().process(this);

			} catch (ClassNotFoundException e) {
				Log.w("NetworkManager", "problem while receiving msg", e);
			}
		}
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
			messageIOFacade.sendMessage(new ClipboardSync(clipboardText));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendExit() {
		try {
			messageIOFacade.sendMessage(new RemoteExit());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			if (messageIOFacade != null) messageIOFacade.close();
		} catch (IOException ignored) {
		}

		clipboardManager.removePrimaryClipChangedListener(this);
	}

	public boolean isConnected() {
		return messageIOFacade != null && !messageIOFacade.isClosed() && !messageIOFacade.isStopped();
	}

	@Override
	public void onTrig() {
		sendExit();
	}
}
