package org.kbieron.iomerge.gui.remoteControl

import android.view.MotionEvent
import android.view.View


internal class OnTouchListenerChain(vararg val onTouchListeners: View.OnTouchListener) : View.OnTouchListener {

	override fun onTouch(v: View, event: MotionEvent): Boolean {
		onTouchListeners.forEach {
			if (it.onTouch(v, event))
				return true
		}
		return false
	}
}
