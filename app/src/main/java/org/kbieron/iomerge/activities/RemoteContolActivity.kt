package org.kbieron.iomerge.activities

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.pawegio.kandroid.IntentFor
import com.pawegio.kandroid.inputMethodManager
import kotterknife.bindView
import org.kbieron.iomerge.android.R
import org.kbieron.iomerge.services.NetworkManager
import org.kbieron.iomerge.views.RemoteControlView


open class RemoteContolActivity : Activity() {

	private val remoteControlView by bindView<RemoteControlView>(R.id.remote_control_view)
	private val showKeyboardBtn by bindView<Button>(R.id.keyboard_btn)

	private var mConnection: ServiceConnection? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.remote_control_activity)

		showKeyboardBtn.setOnClickListener {
			inputMethodManager?.showSoftInput(remoteControlView, InputMethodManager.SHOW_FORCED)
		}
	}

	override fun onStart() {
		super.onStart()

		bindService(IntentFor<NetworkManager>(this), object : ServiceConnection {
			override fun onServiceDisconnected(name: ComponentName?) = Unit
			override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
				val networkManagerBinder = service as NetworkManager.NetworkManagerBinder
				remoteControlView.sendMessageFun = networkManagerBinder.sendMessageFun
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
