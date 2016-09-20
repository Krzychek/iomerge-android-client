package org.kbieron.iomerge.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.github.krzychek.iomerge.server.model.MouseButton;
import com.github.krzychek.iomerge.server.model.SpecialKey;
import com.github.krzychek.iomerge.server.model.message.keyboard.SpecialKeyClick;
import com.github.krzychek.iomerge.server.model.message.keyboard.StringTyped;
import com.github.krzychek.iomerge.server.model.message.mouse.MouseMove;
import com.github.krzychek.iomerge.server.model.message.mouse.MousePress;
import com.github.krzychek.iomerge.server.model.message.mouse.MouseRelease;
import com.github.krzychek.iomerge.server.model.message.mouse.MouseWheel;
import org.kbieron.iomerge.services.ConnectionHandler;
import org.kbieron.iomerge.services.ConnectionHandler_;


public class RemoteControlView extends View implements View.OnTouchListener, MultiClickDetector.MultiClickListener, View.OnKeyListener {

	private static final int MOUSE_WHEEL_SCALE = 10;

	private final ConnectionHandler connectionHandler;

	private int oldX;
	private int oldY;

	public RemoteControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		connectionHandler = ConnectionHandler_.getInstance_(context);
		setOnTouchListener(createOnTouchListener());
		setOnKeyListener(this);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	private OnTouchListenerChain createOnTouchListener() {
		MultiClickDetector multiClickDetector = new MultiClickDetector(this);
		return new OnTouchListenerChain(this, multiClickDetector);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			onMove(event);
			return true;
		} else if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			oldX = (int) event.getX();
			oldY = (int) event.getY();
		}

		return false;
	}

	private void onMove(MotionEvent event) {
		if (event.getPointerCount() == 1) {
			connectionHandler.sendMessage(new MouseMove((int) (event.getX() - oldX), (int) (event.getY() - oldY)));

		} else if (event.getPointerCount() == 2) {
			int yDiff = (int) (event.getY() - oldY) / MOUSE_WHEEL_SCALE;
			connectionHandler.sendMessage(new MouseWheel(yDiff));
		}

		oldX = (int) event.getX();
		oldY = (int) event.getY();
	}

	@Override
	public void onClick(int pointerCount) {
		if (pointerCount == 1)
			sendMouseClick(MouseButton.LEFT);
		else if (pointerCount == 2)
			sendMouseClick(MouseButton.CENTER);
		else if (pointerCount == 3)
			sendMouseClick(MouseButton.RIGHT);
	}

	private void sendMouseClick(MouseButton btn) {
		connectionHandler.sendMessage(new MousePress(btn));
		connectionHandler.sendMessage(new MouseRelease(btn));
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			int unicodeChar = event.getUnicodeChar();
			if (unicodeChar != 0)
				connectionHandler.sendMessage(new StringTyped(new String(new int[]{unicodeChar}, 0, 1)));
			else {
				processSpecialKey(keyCode);
			}

		} else if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			connectionHandler.sendMessage(new StringTyped(event.getCharacters()));

		return false;
	}

	private void processSpecialKey(int keyCode) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_DEL:
				connectionHandler.sendMessage(new SpecialKeyClick(SpecialKey.BACKSPACE));
		}
	}

}