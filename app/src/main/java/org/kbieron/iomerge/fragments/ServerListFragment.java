package org.kbieron.iomerge.fragments;

import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.kbieron.iomerge.android.R;
import org.kbieron.iomerge.database.ServerBean;
import org.kbieron.iomerge.database.ServerDAO;
import org.kbieron.iomerge.services.NetworkManager;
import org.kbieron.iomerge.services.NetworkManager_;


@EFragment(R.layout.server_list_fragment_layout)
public class ServerListFragment extends Fragment implements OnServerListAdapter.ItemClickListener {

	@ViewById(R.id.server_list_view)
	RecyclerView recyclerView;

	@ViewById(R.id.fab)
	FloatingActionButton floatingActionButton;

	@ViewById(R.id.address)
	TextView addressView;

	@ViewById(R.id.port)
	TextView portView;

	@Bean
	ServerDAO serverDAO;
	private OnServerListAdapter listAdapter;


	@AfterViews
	public void createList() {

		// setup recycler view
		listAdapter = new OnServerListAdapter(serverDAO);
		listAdapter.setItemClickListener(this);
		recyclerView.setAdapter(listAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		new ItemTouchHelper(new SwipeCallback())
				.attachToRecyclerView(recyclerView);

		// setup fab
		floatingActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AddServerDialogFragment addServerDialogFragment = new AddServerDialogFragment();
				addServerDialogFragment.show(getFragmentManager(), "add server");
			}
		});
		floatingActionButton.show();
	}

	@Override
	public void onItemClick(ServerBean server) {

		NetworkManager_.intent(getActivity())
				.action(NetworkManager.CONNECT_ACTION)
				.extra(NetworkManager.SERVER_EXTRA, server)
				.start();
	}

	private void removeServer(ServerBean server) {
		serverDAO.deleteServer(server);
		listAdapter.removeServer(server);
	}

	private class SwipeCallback extends ItemTouchHelper.SimpleCallback {

		SwipeCallback() {
			super(0, ItemTouchHelper.LEFT);
		}

		@Override
		public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
			return false;
		}

		@Override
		public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
			removeServer(((OnServerListAdapter.ServerViewHolder) viewHolder).getServer());
		}

	}
}