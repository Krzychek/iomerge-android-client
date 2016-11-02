package org.kbieron.iomerge.gui.remoteControl

import android.view.MotionEvent
import android.view.View


internal class MultiClickDetector(private val listener: (pointerCount: Int) -> Unit) : View.OnTouchListener {

	private var downMilis: Long = 0

	override fun onTouch(v: View, event: MotionEvent): Boolean {
		val actionMasked = event.actionMasked
		if (actionMasked == MotionEvent.ACTION_DOWN)
			onDown(event)
		else if (actionMasked == MotionEvent.ACTION_POINTER_UP || actionMasked == MotionEvent.ACTION_UP)
			onUp(event)

		return false
	}

	private fun onDown(event: MotionEvent) {
		downMilis = event.eventTime
	}

	private fun onUp(event: MotionEvent) {
		if (event.eventTime - downMilis < TAP_TIMEOUT) {
			listener(event.pointerCount)
			downMilis = 0
		}
	}

	companion object {

		private val TAP_TIMEOUT = 100
	}
}
