package org.kbieron.iomerge.gui.main

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.github.krzychek.iomerge.server.model.Edge
import com.github.krzychek.iomerge.server.model.message.misc.RemoteExit
import com.pawegio.kandroid.runOnUiThread
import com.pawegio.kandroid.windowManager
import org.kbieron.iomerge.MiscPrefs
import org.kbieron.iomerge.services.ConnectionHandler


class EdgeTrigger(context: Context, val connectionHandler: ConnectionHandler) {

	init {
		MiscPrefs.addChangeListener { moveIfShown() }
	}

	private val THICKNESS = 1

	private val windowManager = context.windowManager!!

	private val _view = object : View(context) {
		init {
			setOnHoverListener({ view: View, event: MotionEvent ->
				if (MotionEvent.ACTION_HOVER_ENTER == event.action)
					connectionHandler.sendMessage(RemoteExit(view.height / event.getAxisValue(MotionEvent.AXIS_Y)))
				false
			})
		}

		override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = when (MiscPrefs.edge) {
			Edge.RIGHT, Edge.LEFT -> setMeasuredDimension(THICKNESS, heightMeasureSpec)
			Edge.TOP, Edge.BOTTOM -> setMeasuredDimension(widthMeasureSpec, THICKNESS)
		}
	}

	private val windowLayoutParams: WindowManager.LayoutParams
		get() {
			return WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, // needed to display over lockscreen
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, // show above system elements
					PixelFormat.TRANSPARENT
			).recalculateGravity()
		}

	fun show() = runOnUiThread {
		windowManager.addView(_view, windowLayoutParams)
	}

	fun moveIfShown() = runOnUiThread {
		if (_view.isAttachedToWindow)
			windowManager.updateViewLayout(_view, windowLayoutParams)
	}

	fun hide() = runOnUiThread {
		if (_view.isAttachedToWindow)
			windowManager.removeView(_view)
	}

	private fun WindowManager.LayoutParams.recalculateGravity(): WindowManager.LayoutParams = apply {
		this.gravity = when (MiscPrefs.edge) {
			Edge.LEFT -> Gravity.FILL_VERTICAL or Gravity.RIGHT
			Edge.RIGHT -> Gravity.FILL_VERTICAL or Gravity.LEFT
			Edge.TOP -> Gravity.FILL_HORIZONTAL or Gravity.BOTTOM
			Edge.BOTTOM -> Gravity.FILL_HORIZONTAL or Gravity.TOP
		}
	}
}

