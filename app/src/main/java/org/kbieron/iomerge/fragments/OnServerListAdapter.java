package org.kbieron.iomerge.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.kbieron.iomerge.android.R;
import org.kbieron.iomerge.database.ServerBean;
import org.kbieron.iomerge.database.ServerDAO;

import java.util.List;


class OnServerListAdapter extends RecyclerView.Adapter<OnServerListAdapter.ServerViewHolder> implements ServerDAO.OnServerAddedListener {

	private final List<ServerBean> serverBeens;
	private ItemClickListener itemClickListener;

	OnServerListAdapter(ServerDAO serverDAO) {
		this.serverBeens = serverDAO.getAllServers();
		serverDAO.addOnServerAddedListener(this);
	}

	@Override
	public ServerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.server_card_layout, parent, false);

		return new ServerViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ServerViewHolder holder, int position) {
		holder.setServer(serverBeens.get(position));
	}

	@Override
	public int getItemCount() {
		return serverBeens.size();
	}

	void removeServer(ServerBean server) {
		int idx = serverBeens.indexOf(server);
		if (idx >= 0) {
			serverBeens.remove(idx);
			notifyItemRemoved(idx);
		}
	}

	void setItemClickListener(ItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	@Override
	public void onServerAdded(ServerBean server) {
		this.serverBeens.add(0, server);
		notifyItemInserted(0);
	}

	interface ItemClickListener {

		void onItemClick(ServerBean server);
	}

	class ServerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		private final TextView address;
		private final TextView port;
		private ServerBean server;

		ServerViewHolder(View view) {
			super(view);
			address = (TextView) view.findViewById(R.id.address);
			port = (TextView) view.findViewById(R.id.port);

			view.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			itemClickListener.onItemClick(server);
		}

		public ServerBean getServer() {
			return server;
		}

		private void setServer(ServerBean server) {
			address.setText(server.getAddress());
			port.setText(String.valueOf(server.getPort()));

			this.server = server;
		}
	}
}
