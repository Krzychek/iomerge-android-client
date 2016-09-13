package org.kbieron.iomerge.views;

import android.view.MotionEvent;
import android.view.View;


class OnTouchListenerChain implements View.OnTouchListener {

	private final View.OnTouchListener[] onTouchListeners;

	OnTouchListenerChain(View.OnTouchListener... onTouchListeners) {
		this.onTouchListeners = onTouchListeners;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		for (View.OnTouchListener onTouchListener : onTouchListeners)
			if (onTouchListener.onTouch(v, event))
				return true;

		return false;
	}
}
