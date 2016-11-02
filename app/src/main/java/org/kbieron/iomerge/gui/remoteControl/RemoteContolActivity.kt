package org.kbieron.iomerge.gui.remoteControl

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import com.pawegio.kandroid.IntentFor
import com.pawegio.kandroid.inputMethodManager
import kotlinx.android.synthetic.main.remote_control_activity.*
import org.kbieron.iomerge.IOMergeApp
import org.kbieron.iomerge.android.R
import org.kbieron.iomerge.services.NetworkManager


open class RemoteContolActivity : AppCompatActivity() {

	private var mConnection: ServiceConnection? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		if ((application as IOMergeApp).connectedServer != null) {
			setContentView(R.layout.remote_control_activity)
			keyboard_btn.setOnClickListener {
				inputMethodManager?.showSoftInput(remote_control_view, InputMethodManager.SHOW_FORCED)
			}

		} else {
			finish()
		}

	}

	override fun onStart() {
		super.onStart()

		bindService(IntentFor<NetworkManager>(this), object : ServiceConnection {
			override fun onServiceDisconnected(name: ComponentName?) {
				this@RemoteContolActivity.finish()
			}

			override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
				val networkManagerBinder = service as NetworkManager.NetworkManagerBinder
				remote_control_view.sendMessageFun = networkManagerBinder.sendMessageFun
				mConnection = this
			}
		}, Context.BIND_IMPORTANT)
	}

	override fun onStop() {
		super.onStop()
		mConnection?.let {
			unbindService(it)
			mConnection = null
		}
	}
}
