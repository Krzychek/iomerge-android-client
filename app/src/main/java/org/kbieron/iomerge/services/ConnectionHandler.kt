package org.kbieron.iomerge.services


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Looper
import android.view.KeyEvent
import android.widget.Toast
import com.github.krzychek.iomerge.server.model.Edge
import com.github.krzychek.iomerge.server.model.MouseButton
import com.github.krzychek.iomerge.server.model.SpecialKey
import com.github.krzychek.iomerge.server.model.message.Message
import com.github.krzychek.iomerge.server.model.message.misc.ClipboardSync
import com.github.krzychek.iomerge.server.model.message.misc.Heartbeat
import com.github.krzychek.iomerge.server.model.processors.MessageProcessor
import com.github.krzychek.iomerge.server.model.serialization.MessageSocketWrapper
import com.pawegio.kandroid.clipboardManager
import com.pawegio.kandroid.e
import com.pawegio.kandroid.runAsync
import com.pawegio.kandroid.w
import org.kbieron.iomerge.MiscPrefs
import org.kbieron.iomerge.database.ServerBean
import java.io.EOFException
import java.io.IOException
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

open class ConnectionHandler(private val context: Context, private val inputDevice: InputDevice)
: MessageProcessor, ClipboardManager.OnPrimaryClipChangedListener {

	val clipboardManager: ClipboardManager by lazy { context.clipboardManager!! }

	private val executor = ScheduledThreadPoolExecutor(1)

	internal val isConnected: Boolean
		get() = socket?.isClosed?.not() ?: false

	private var socket: MessageSocketWrapper? = null

	private var heartbeatSendingTask: ScheduledFuture<*>? = null
	private var heartbeatTimeoutTask: ScheduledFuture<*>? = null

	private var lastHeartbeatTime: Long = 0


	private fun startHeartbeatTimer(networkManager: NetworkManager) {
		heartbeatSendingTask = executor.scheduleWithFixedDelay({
			try {
				socket!!.sendMessage(Heartbeat.INSTANCE)
			} catch (e: Exception) {
				w("IOException while sending hearbeat: $e")
				disconnect(networkManager)
			}
		}, 0, HEARBEAT_DELAY.toLong(), TimeUnit.SECONDS)

		lastHeartbeatTime = System.currentTimeMillis()
		heartbeatTimeoutTask = executor.scheduleWithFixedDelay(object : Runnable {
			private var lastCall: Long = 0

			override fun run() {
				if (lastCall > lastHeartbeatTime) {
					w("Connection timeout")
					Toast.makeText(context, "IOMerge: connection timeout", Toast.LENGTH_LONG).show()
					disconnect(networkManager)
				}
				lastCall = System.currentTimeMillis()
			}
		}, HEARTBEAT_TIMEOUT.toLong(), HEARTBEAT_TIMEOUT.toLong(), TimeUnit.SECONDS)
	}

	internal fun connect(server: ServerBean, networkManager: NetworkManager) = runAsync {
		Looper.prepare()
		try {

			socket = MessageSocketWrapper(server.address, server.port)

			clipboardManager.addPrimaryClipChangedListener(this)
			startHeartbeatTimer(networkManager)

			// start daemon
			inputDevice.startNativeDaemon()

			while (isConnected) {
				try {
					socket!!.readMessage().process(this@ConnectionHandler)
					lastHeartbeatTime = System.currentTimeMillis()

				} catch (e: EOFException) {
					disconnect(networkManager)

				} catch (e: ClassNotFoundException) {
					w("problem while receiving msg: $e")
				} catch (e: IOException) {
					w("problem while receiving msg: $e")
				}

			}

		} catch (e: IOException) {
			e("problem with connection: $e")
			disconnect(networkManager)
		} catch (e: InterruptedException) {
			e("problem with connection: $e")
			disconnect(networkManager)
		}

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

	override fun clipboardSync(text: String) {
		clipboardManager.removePrimaryClipChangedListener(this)
		clipboardManager.primaryClip = ClipData.newPlainText("IOMerge", text)
		clipboardManager.addPrimaryClipChangedListener(this)
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

	override fun onPrimaryClipChanged() {
		val clipboardText = clipboardManager.primaryClip.getItemAt(0).text.toString()
		sendMessage(ClipboardSync(clipboardText))
	}

	fun sendMessage(message: Message) {
		try {
			socket!!.sendMessage(message)
		} catch (e: IOException) {
			e("Problem with sending message: $e")
		}

	}

	private fun disconnect(networkManager: NetworkManager) {
		try {
			socket?.close()
		} catch (ignored: IOException) {
		}

		socket = null

		heartbeatSendingTask!!.cancel(true)
		heartbeatTimeoutTask!!.cancel(true)
		executor.purge()

		clipboardManager.removePrimaryClipChangedListener(this)
		networkManager.disconnect()
	}

	override fun returnToLocal(v: Float) {
		throw IllegalStateException("not implemented")
	}

	companion object {

		private val HEARBEAT_DELAY = 2
		private val HEARTBEAT_TIMEOUT = HEARBEAT_DELAY * 2
	}
}
