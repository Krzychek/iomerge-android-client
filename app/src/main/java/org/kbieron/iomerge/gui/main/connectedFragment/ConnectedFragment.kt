package org.kbieron.iomerge.gui.main.connectedFragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pawegio.kandroid.IntentFor
import com.pawegio.kandroid.startActivity
import kotlinx.android.synthetic.main.connected_fragment_layout.*
import org.kbieron.iomerge.android.R
import org.kbieron.iomerge.gui.remoteControl.RemoteContolActivity
import org.kbieron.iomerge.ioMergeApplication
import org.kbieron.iomerge.services.NetworkManager

class ConnectedFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
			= inflater.inflate(R.layout.connected_fragment_layout, container, false)

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		activity.ioMergeApplication.connectedServer?.let {
			connected_to_server_tv.text = "Connected to ${it.address}:${it.port}"
		}

		disconnect_btn.setOnClickListener { disconnect() }
		remote_control_btn.setOnClickListener { context.startActivity<RemoteContolActivity>() }
	}

	private fun disconnect() {
		startActivity(IntentFor<NetworkManager>(context).setAction(NetworkManager.DISCONNECT_ACTION))
	}
}