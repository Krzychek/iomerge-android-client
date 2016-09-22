package org.kbieron.iomerge.services

import android.content.Context
import android.util.Log
import java.io.*
import java.lang.Runtime.getRuntime


class InputDevice(private val context: Context) {

	private val DAEMON_NAME = "iomerge_daemon"
	private var coppied = false

	init {
		System.loadLibrary("native")
	}

	private val daemonOutFile: File = context.cacheDir.resolve(DAEMON_NAME)

	@Synchronized @Throws(IOException::class, InterruptedException::class)
	internal fun startNativeDaemon() {
		stop()

		if (!coppied) {
			FileOutputStream(daemonOutFile).use {
				copy(context.assets.open(DAEMON_NAME), it)
			}
		}

		try {
			getRuntime().exec(arrayOf("su", "-C", "chmod 777 ${daemonOutFile.absolutePath}")).waitFor()
			coppied = true
			initializePipe()

			getRuntime().exec(arrayOf("su", "-C", daemonOutFile.absolutePath)).apply {
				Thread.sleep(100)
				val exitValue = exitValue() // throws IllegalThreadStateException if process was started
				throw RuntimeException("Daemon failed to start, exited with value: $exitValue")
			}
		} catch (ignored: IllegalThreadStateException) {
			// it means process was started succesfully
		}

	}

	@Synchronized internal fun stop() {

		try {
			//noinspection SpellCheckingInspection
			getRuntime().exec(arrayOf("su", "-C", "killall $DAEMON_NAME")).waitFor()
		} catch (e: InterruptedException) {
			e.printStackTrace()
		} catch (e: IOException) {
			e.printStackTrace()
		}

	}

	internal fun emitKeyEvent(event: Int) {
		try {
			getRuntime().exec(arrayOf("su", " -C", "input keyevent $event"))
		} catch (e: IOException) {
			Log.e("IOManage", "Unable to run ", e)
		}
	}

	@Throws(IOException::class)
	private fun copy(inputStream: InputStream, outputStream: OutputStream) {
		val buffer = ByteArray(32768)
		var len: Int = inputStream.read(buffer)

		while (-1 != len) {
			outputStream.write(buffer, 0, len)
			len = inputStream.read(buffer)
		}
	}

	external private fun initializePipe()

	external fun mouseMove(x: Int, y: Int)

	external fun mousePress(button: Int)

	external fun mouseRelease(button: Int)

	external fun keyPress(c: Int)

	external fun keyRelease(c: Int)

	external fun mouseWheel(anInt: Int)

	external fun keyClick(i: Int)
}
