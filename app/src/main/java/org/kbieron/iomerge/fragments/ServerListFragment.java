package org.kbieron.iomerge.fragments;

import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.kbieron.iomerge.Preferences_;
import org.kbieron.iomerge.android.R;
import org.kbieron.iomerge.database.ServerBean;
import org.kbieron.iomerge.database.ServerDAO;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@EFragment(R.layout.server_list_fragment_layout)
public class ServerListFragment extends Fragment {

	@Pref
	Preferences_ prefs;

	@ViewById(R.id.server_list_view)
	RecyclerView recyclerView;

	@ViewById(R.id.address)
	TextView addressView;

	@ViewById(R.id.port)
	TextView portView;

	@Bean
	ServerDAO serverDAO;

	private Pattern addressPattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
													 "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
													 "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
													 "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	private Pattern portPattern = Pattern.compile("^\\d{1,4}$");
	private ServerListAdapter listAdapter;

	@Click(R.id.addBtn)
	void add() {
		Matcher addressMatcher = addressPattern.matcher(addressView.getText().toString());
		Matcher portMatcher = portPattern.matcher(portView.getText().toString());

		if (!(addressMatcher.matches() && portMatcher.matches())) {
			showWrongFormatToast();
			return;
		}

		serverDAO.open();
		ServerBean server = serverDAO.createServer(addressMatcher.group(), Integer.parseInt(portMatcher.group()));
		serverDAO.close();

		listAdapter.addServer(server);

		addressView.setText("");
		portView.setText("");
	}

	private void showWrongFormatToast() {
		Toast.makeText(getActivity(), R.string.wrong_address_format, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	private void refresh() {
		serverDAO.open();
		listAdapter = new ServerListAdapter();
		recyclerView.setAdapter(listAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		serverDAO.close();
	}


	private class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ServerViewHolder> {

		private final List<ServerBean> serverBeens;

		private ServerListAdapter() {
			this.serverBeens = serverDAO.getAllServers();
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

		void addServer(ServerBean server) {
			serverBeens.add(server);
			notifyItemInserted(serverBeens.size() - 1);
		}

		class ServerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

			private final TextView address;
			private final TextView port;
			private ServerBean server;

			ServerViewHolder(View view) {
				super(view);
				address = (TextView) itemView.findViewById(R.id.address);
				port = (TextView) itemView.findViewById(R.id.port);

				view.setOnClickListener(this);
				view.setOnLongClickListener(this);
			}

			private void setServer(ServerBean server) {
				address.setText(server.getAddress());
				port.setText(Integer.toString(server.getPort()));

				this.server = server;
			}


			@Override
			public void onClick(View v) {
				prefs.edit()
						.serverAddress().put(server.getAddress())
						.serverPort().put(server.getPort())
						.apply();
			}

			@Override
			public boolean onLongClick(View v) {

				serverDAO.open();
				serverDAO.deleteServer(server);
				serverDAO.close();
				serverBeens.remove(server);
				notifyItemRemoved(serverBeens.indexOf(server));

				return false;
			}
		}
	}
}