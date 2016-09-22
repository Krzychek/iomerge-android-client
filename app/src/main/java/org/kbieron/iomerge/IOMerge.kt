package org.kbieron.iomerge

import android.app.Application
import com.squareup.leakcanary.LeakCanary


class IOMerge : Application() {

	override fun onCreate() {
		super.onCreate()
		LeakCanary.install(this)
	}
}
