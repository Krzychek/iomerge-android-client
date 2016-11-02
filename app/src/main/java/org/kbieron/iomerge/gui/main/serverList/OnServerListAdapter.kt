package org.kbieron.iomerge.gui.main.serverList

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pawegio.kandroid.inflateLayout
import org.kbieron.iomerge.android.R
import org.kbieron.iomerge.database.ServerBean
import org.kbieron.iomerge.database.ServerDAO


internal class OnServerListAdapter(serverDAO: ServerDAO, var itemClickListener: ItemClickListener)
: RecyclerView.Adapter<OnServerListAdapter.ServerViewHolder>(), ServerDAO.OnServerAddedListener {

	init {
		serverDAO.addOnServerAddedListener(this)
	}

	private val serverBeens = serverDAO.allServers

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder
			= ServerViewHolder(parent.context.inflateLayout(R.layout.server_card_layout, parent))

	override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
		holder.server = serverBeens[position]
	}

	override fun getItemCount(): Int = serverBeens.size

	override fun onServerAdded(server: ServerBean) {
		serverBeens.add(0, server)
		notifyItemInserted(0)
	}

	fun removeServer(server: ServerBean) {
		val idx = serverBeens.indexOf(server)
		if (idx >= 0) {
			serverBeens.removeAt(idx)
			notifyItemRemoved(idx)
		}
	}

	internal interface ItemClickListener {
		fun onItemClick(server: ServerBean)
	}

	internal inner class ServerViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

		init {
			view.setOnClickListener(this)
		}

		private val address: TextView = view.findViewById(R.id.address) as TextView
		private val port: TextView = view.findViewById(R.id.port) as TextView

		var server: ServerBean? = null
			set(server) {
				address.text = server?.address
				port.text = server?.port.toString()
				field = server
			}

		override fun onClick(v: View) {
			server?.let { itemClickListener.onItemClick(it) }
		}
	}
}


