package org.kbieron.iomerge.views

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import com.github.krzychek.iomerge.server.model.MouseButton
import com.github.krzychek.iomerge.server.model.SpecialKey
import com.github.krzychek.iomerge.server.model.message.Message
import com.github.krzychek.iomerge.server.model.message.keyboard.SpecialKeyClick
import com.github.krzychek.iomerge.server.model.message.keyboard.StringTyped
import com.github.krzychek.iomerge.server.model.message.mouse.MouseMove
import com.github.krzychek.iomerge.server.model.message.mouse.MousePress
import com.github.krzychek.iomerge.server.model.message.mouse.MouseRelease
import com.github.krzychek.iomerge.server.model.message.mouse.MouseWheel
import java.lang.System.currentTimeMillis


class RemoteControlView(context: Context, attrs: AttributeSet)
: View(context, attrs), View.OnTouchListener, View.OnKeyListener {

	lateinit var sendMessageFun: (Message) -> Unit?

	init {
		isFocusable = true
		isFocusableInTouchMode = true
		setOnTouchListener(createOnTouchListener())
		setOnKeyListener(this)
	}

	private var oldX: Int = 0
	private var oldY: Int = 0
	private var lastMutliTouchMove: Long = 0

	private fun createOnTouchListener(): View.OnTouchListener {
		val multiClickDetector = MultiClickDetector({ pointeCount -> onClick(pointeCount) })
		return OnTouchListenerChain(this, multiClickDetector)
	}

	override fun onTouch(v: View, event: MotionEvent): Boolean {
		if (event.action == MotionEvent.ACTION_MOVE) {
			onMove(event)
			return true
		} else if (event.actionMasked == MotionEvent.ACTION_DOWN) {
			oldX = event.x.toInt()
			oldY = event.y.toInt()
		}

		return false
	}

	private fun onMove(event: MotionEvent) {
		val y = event.y.toInt()
		val x = event.x.toInt()

		if (event.pointerCount == 1) {
			if (currentTimeMillis() - lastMutliTouchMove > POINTER_DOWN_CHANGE_DELAY)
				sendMessageFun(MouseMove(x - oldX, y - oldY))

		} else if (event.pointerCount == 2) {
			val yDiff = (event.y - oldY).toInt() / MOUSE_WHEEL_SCALE
			sendMessageFun(MouseWheel(yDiff))
			lastMutliTouchMove = currentTimeMillis()
		}

		oldX = x
		oldY = y
	}

	private fun onClick(pointerCount: Int) {
		if (pointerCount == 1)
			sendMouseClick(MouseButton.LEFT)
		else if (pointerCount == 2)
			sendMouseClick(MouseButton.CENTER)
		else if (pointerCount == 3)
			sendMouseClick(MouseButton.RIGHT)
	}

	private fun sendMouseClick(btn: MouseButton) {
		sendMessageFun.apply {
			sendMessageFun(MousePress(btn))
			sendMessageFun(MouseRelease(btn))
		}
	}

	override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
		if (event.action == KeyEvent.ACTION_UP) {
			val unicodeChar = event.unicodeChar
			if (unicodeChar != 0)
				sendMessageFun(StringTyped(String(intArrayOf(unicodeChar), 0, 1)))
			else {
				processSpecialKey(keyCode)
			}

		} else if (event.action == KeyEvent.ACTION_MULTIPLE)
			sendMessageFun(StringTyped(event.characters))

		return false
	}

	private fun processSpecialKey(keyCode: Int) {
		when (keyCode) {
			KeyEvent.KEYCODE_DEL -> sendMessageFun(SpecialKeyClick(SpecialKey.BACKSPACE))
		}
	}

	companion object {
		private val MOUSE_WHEEL_SCALE = -10
		private val POINTER_DOWN_CHANGE_DELAY = 100
	}


}