package org.kbieron.iomerge

import com.chibatching.kotpref.KotprefModel
import com.github.krzychek.iomerge.server.model.Edge
import java.lang.ref.WeakReference
import java.util.*

object MiscPrefs : KotprefModel() {

	private val changelisteners = ArrayList<WeakReference<() -> Unit>>()

	private var _edge: String by stringPrefVar(Edge.LEFT.name)
	var edge: Edge
		get() = Edge.valueOf(_edge)
		set(value) {
			_edge = edge.name
			changelisteners.forEach { it.get()?.invoke() }
		}

	fun addChangeListener(listener: () -> Unit) {
		changelisteners.add(WeakReference(listener))
	}
}
