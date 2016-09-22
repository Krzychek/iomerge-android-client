package org.kbieron.iomerge.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pawegio.kandroid.IntentFor
import kotterknife.bindView
import org.kbieron.iomerge.android.R
import org.kbieron.iomerge.database.ServerBean
import org.kbieron.iomerge.database.ServerDAO
import org.kbieron.iomerge.services.NetworkManager


open class ServerListFragment : Fragment(), OnServerListAdapter.ItemClickListener {

	private val recyclerView by bindView<RecyclerView>(R.id.server_list_view)

	private val floatingActionButton by bindView<FloatingActionButton>(R.id.fab)

	private val serverDAO by lazy { ServerDAO(activity) }

	private val listAdapter by lazy { OnServerListAdapter(serverDAO, this) }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
			= inflater.inflate(R.layout.server_list_fragment_layout, container, false)

	override fun onResume() {
		super.onResume()
		createList()
		setupFAB()
	}

	private fun createList() {
		recyclerView.apply {
			adapter = listAdapter
			layoutManager = LinearLayoutManager(activity)
		}
		ItemTouchHelper(SwipeCallback()).attachToRecyclerView(recyclerView)
	}

	private fun setupFAB() {
		floatingActionButton.apply {
			setOnClickListener {
				val addServerDialogFragment = AddServerDialogFragment()
				addServerDialogFragment.show(fragmentManager, "add server")
			}
		}.show()
	}

	override fun onItemClick(server: ServerBean) {
		IntentFor<NetworkManager>(activity).apply {
			action = NetworkManager.CONNECT_ACTION
			putExtra(NetworkManager.SERVER_EXTRA, server)

		}.let { activity.startService(it) }
	}

	private fun removeServer(server: ServerBean?) {
		server?.let {
			serverDAO.deleteServer(it)
			listAdapter.removeServer(it)
		}
	}

	private inner class SwipeCallback() : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

		override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
			return false
		}

		override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
			removeServer((viewHolder as OnServerListAdapter.ServerViewHolder).server)
		}

	}
}