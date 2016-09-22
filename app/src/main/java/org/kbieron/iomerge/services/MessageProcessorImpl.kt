package org.kbieron.iomerge.services

import android.content.ClipData
import android.content.ClipboardManager
import android.view.KeyEvent
import com.github.krzychek.iomerge.server.model.Edge
import com.github.krzychek.iomerge.server.model.MouseButton
import com.github.krzychek.iomerge.server.model.SpecialKey
import com.github.krzychek.iomerge.server.model.processors.MessageProcessor
import com.pawegio.kandroid.e
import org.kbieron.iomerge.MiscPrefs

class MessageProcessorImpl(private val inputDevice: InputDevice,
						   private val clipboardManager: ClipboardManager,
						   private val onPrimaryClipChangedListener: ClipboardManager.OnPrimaryClipChangedListener)
: MessageProcessor {


	override fun clipboardSync(text: String) {
		clipboardManager.removePrimaryClipChangedListener(onPrimaryClipChangedListener)
		clipboardManager.primaryClip = ClipData.newPlainText("IOMerge", text)
		clipboardManager.addPrimaryClipChangedListener(onPrimaryClipChangedListener)
	}

	override fun keyClick(i: Int) {
		inputDevice.keyClick(i)
	}

	override fun stringTyped(s: String) {
		e("WTF I should do with it?! 0o" + s)
	}

	override fun specialKeyClick(specialKey: SpecialKey) {
		e("WTF I should do with it?! 0o" + specialKey)
	}

	override fun mousePress(button: MouseButton) {
		inputDevice.mousePress(0) // TODO
	}

	override fun mouseRelease(button: MouseButton) {
		inputDevice.mouseRelease(0) // TODO
	}

	override fun mouseMove(x: Int, y: Int) {
		inputDevice.mouseMove(x, y)
	}

	override fun mouseWheel(move: Int) {
		inputDevice.mouseWheel(move)
	}

	override fun backBtnClick() {
		inputDevice.emitKeyEvent(KeyEvent.KEYCODE_BACK)
	}

	override fun homeBtnClick() {
		inputDevice.emitKeyEvent(KeyEvent.KEYCODE_HOME)
	}

	override fun menuBtnClick() {
		inputDevice.emitKeyEvent(KeyEvent.KEYCODE_MENU)
	}

	override fun edgeSync(edge: Edge) {
		MiscPrefs.edge = edge
	}

	override fun keyPress(keyCode: Int) {
		inputDevice.keyPress(keyCode)
	}

	override fun keyRelease(keyCode: Int) {
		inputDevice.keyRelease(keyCode)
	}


	override fun returnToLocal(v: Float) {
		throw IllegalStateException("not implemented")
	}


}
