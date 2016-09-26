package org.kbieron.iomerge.activities

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import com.pawegio.kandroid.IntentFor
import com.pawegio.kandroid.inputMethodManager
import kotlinx.android.synthetic.main.remote_control_activity.*
import org.kbieron.iomerge.android.R
import org.kbieron.iomerge.services.NetworkManager


open class RemoteContolActivity : Activity() {

	private var mConnection: ServiceConnection? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.remote_control_activity)

		keyboard_btn.setOnClickListener {
			inputMethodManager?.showSoftInput(remote_control_view, InputMethodManager.SHOW_FORCED)
		}
	}

	override fun onStart() {
		super.onStart()

		bindService(IntentFor<NetworkManager>(this), object : ServiceConnection {
			override fun onServiceDisconnected(name: ComponentName?) = Unit
			override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
				val networkManagerBinder = service as NetworkManager.NetworkManagerBinder
				remote_control_view.sendMessageFun = networkManagerBinder.sendMessageFun
				mConnection = this
			}
		}, 0)
	}

	override fun onStop() {
		super.onStop()
		mConnection?.let { unbindService(it) }
		mConnection = null
	}
}
