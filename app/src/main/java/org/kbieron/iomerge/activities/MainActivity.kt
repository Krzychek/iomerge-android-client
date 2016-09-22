package org.kbieron.iomerge.activities

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.pawegio.kandroid.fromApi
import org.kbieron.iomerge.android.R


open class MainActivity : Activity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)
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
