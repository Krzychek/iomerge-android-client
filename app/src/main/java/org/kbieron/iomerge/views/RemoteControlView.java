package org.kbieron.iomerge.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.github.krzychek.iomerge.server.model.message.mouse.MouseButton;
import org.kbieron.iomerge.services.ConnectionHandler;
import org.kbieron.iomerge.services.ConnectionHandler_;


public class RemoteControlView extends View implements View.OnTouchListener, MultiClickDetector.MultiClickListener {

	private static final int MOUSE_WHEEL_SCALE = 10;

	private final ConnectionHandler connectionHandler;

	private int oldX;
	private int oldY;

	public RemoteControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		connectionHandler = ConnectionHandler_.getInstance_(context);
		setOnTouchListener(createOnTouchListener());
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
			connectionHandler.sendMouseMove((int) (event.getX() - oldX), (int) (event.getY() - oldY));

		} else if (event.getPointerCount() == 2) {
			int yDiff = (int) (event.getY() - oldY) / MOUSE_WHEEL_SCALE;
			connectionHandler.sendMouseWheel(yDiff);
		}

		oldX = (int) event.getX();
		oldY = (int) event.getY();
	}

	@Override
	public void onClick(int pointerCount) {
		if (pointerCount == 1)
			sendClick(MouseButton.LEFT);
		else if (pointerCount == 2)
			sendClick(MouseButton.CENTER);
		else if (pointerCount == 3)
			sendClick(MouseButton.RIGHT);
	}

	private void sendClick(MouseButton btn) {
		connectionHandler.sendMousePress(btn);
		connectionHandler.sendMouseRelease(btn);
	}
}