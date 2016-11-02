package org.kbieron.iomerge

import android.app.Activity
import android.app.Application
import android.app.Fragment
import com.squareup.leakcanary.LeakCanary
import org.kbieron.iomerge.database.ServerBean


class IOMergeApp : Application() {

	var connectedServer: ServerBean? = null

	override fun onCreate() {
		super.onCreate()
		LeakCanary.install(this)
	}
}

val Activity.ioMergeApplication: IOMergeApp
	get() = application as IOMergeApp

val Fragment.ioMergeApplication: IOMergeApp
	get() = activity.ioMergeApplication
