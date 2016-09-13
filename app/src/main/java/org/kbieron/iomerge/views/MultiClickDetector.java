package org.kbieron.iomerge.views;

import android.view.MotionEvent;
import android.view.View;


class MultiClickDetector implements View.OnTouchListener {

	private static final int TAP_TIMEOUT = 100;

	private MultiClickListener listener;

	private long downMilis;

	MultiClickDetector(MultiClickListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int actionMasked = event.getActionMasked();
		if (actionMasked == MotionEvent.ACTION_DOWN) {
			onDown(event);

		} else if (actionMasked == MotionEvent.ACTION_POINTER_UP
				   || actionMasked == MotionEvent.ACTION_UP) {
			onUp(event);
		}
		return false;
	}

	private void onDown(MotionEvent event) {
		downMilis = event.getEventTime();
	}

	private void onUp(MotionEvent event) {
		if (event.getEventTime() - downMilis < TAP_TIMEOUT) {
			listener.onClick(event.getPointerCount());
			downMilis = 0;
		}
	}

	interface MultiClickListener {

		void onClick(int pointerCount);
	}
}
