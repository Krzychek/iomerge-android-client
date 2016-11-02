package org.kbieron.iomerge.services


import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.github.krzychek.iomerge.server.model.message.Message
import com.github.krzychek.iomerge.server.model.message.misc.ClipboardSync
import com.github.krzychek.iomerge.server.model.message.misc.Heartbeat
import com.github.krzychek.iomerge.server.model.serialization.MessageSocketWrapper
import com.pawegio.kandroid.*
import org.kbieron.iomerge.database.ServerBean
import org.kbieron.iomerge.gui.main.EdgeTrigger
import java.io.EOFException
import java.io.IOException
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit


class ConnectionHandler(context: Context, server: ServerBean,
						private val disconnectClbk: () -> Unit)
: ClipboardManager.OnPrimaryClipChangedListener {

	private var isConnected: Boolean = true

	private val socket: MessageSocketWrapper = MessageSocketWrapper(server.address, server.port)

	private val clipboardManager: ClipboardManager = context.clipboardManager!!

	private val inputDevice = InputDevice(context)

	private val messageProcessor = MessageProcessorImpl(inputDevice, clipboardManager, this)

	private val edgeTrigger = EdgeTrigger(context, this)

	private val executor = ScheduledThreadPoolExecutor(1)

	private val heartbeatSendingTask: ScheduledFuture<*> = executor.scheduleWithFixedDelay(
			{
				try {
					socket.sendMessage(Heartbeat.INSTANCE)
				} catch (e: IOException) {
					w("IOException while sending hearbeat: $e")
					disconnect()
				}
			}, 0, HEARBEAT_DELAY, TimeUnit.SECONDS)

	private var lastHeartbeatTime = System.currentTimeMillis()
	private val heartbeatTimeoutTask: ScheduledFuture<*> = executor.scheduleWithFixedDelay(object : Runnable {
		private var lastCheck: Long = 0

		override fun run() {
			if (lastCheck > lastHeartbeatTime) {
				w("Connection timeout")
				Toast.makeText(context, "IOMerge: connection timeout", Toast.LENGTH_LONG).show()
				disconnect()
			}
			lastCheck = System.currentTimeMillis()
		}
	}, HEARTBEAT_TIMEOUT, HEARTBEAT_TIMEOUT, TimeUnit.SECONDS)

	init {
		clipboardManager.addPrimaryClipChangedListener(this)

		// start daemon
		inputDevice.startNativeDaemon()

		runAsync {
			while (isConnected) {
				try {
					socket.readMessage().process(messageProcessor)
					lastHeartbeatTime = System.currentTimeMillis()

				} catch (e: EOFException) {
					disconnect()

				} catch (e: ClassNotFoundException) {
					w("problem while receiving msg: $e")
				} catch (e: IOException) {
					w("problem while receiving msg: $e")
				}

			}
		}

		edgeTrigger.show()
	}

	override fun onPrimaryClipChanged() {
		val clipboardText = clipboardManager.primaryClip.getItemAt(0).text.toString()
		sendMessage(ClipboardSync(clipboardText))
	}

	fun sendMessage(message: Message) {
		try {
			socket.sendMessage(message)
		} catch (e: IOException) {
			e("Problem with sending message: $e")
		}
	}

	fun disconnect() {
		if (!isConnected) return
		isConnected = false
		i("Disconnecting")
		edgeTrigger.hide()
		inputDevice.stop()
		disconnectClbk()
		try {
			socket.close()
		} catch (ignored: IOException) {
		}

		heartbeatSendingTask.cancel(true)
		heartbeatTimeoutTask.cancel(true)
		executor.purge()

		clipboardManager.removePrimaryClipChangedListener(this)
	}

	companion object {
		private val HEARBEAT_DELAY = 2L
		private val HEARTBEAT_TIMEOUT = HEARBEAT_DELAY * 2
	}
}
