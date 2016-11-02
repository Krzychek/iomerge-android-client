package org.kbieron.iomerge.gui.main

import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import com.pawegio.kandroid.fromApi
import org.kbieron.iomerge.android.R
import org.kbieron.iomerge.gui.main.connectedFragment.ConnectedFragment
import org.kbieron.iomerge.gui.main.serverList.ServerListFragment
import org.kbieron.iomerge.ioMergeApplication


open class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)

		fragmentManager.beginTransaction().apply {
			val fragment =
					if (ioMergeApplication.connectedServer == null) ServerListFragment()
					else ConnectedFragment()
			replace(android.R.id.content, fragment)
		}.commit()
	}

	override fun onStart() {
		super.onStart()
		fromApi(Build.VERSION_CODES.M) { checkDrawOverlayPermission() }
	}

	@TargetApi(Build.VERSION_CODES.M)
	private fun checkDrawOverlayPermission() {
		if (!Settings.canDrawOverlays(applicationContext))
			startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName)))
	}
}
