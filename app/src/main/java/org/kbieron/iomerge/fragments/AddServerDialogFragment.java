package org.kbieron.iomerge.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.kbieron.iomerge.android.R;
import org.kbieron.iomerge.database.ServerDAO_;

import java.util.regex.Pattern;


public class AddServerDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setMessage(R.string.add_server_dialog_header)
				.setView(inflate(R.layout.add_server_dialog_layout))
				.setPositiveButton(R.string.add, new PositiveButtonListener())
				.setNegativeButton(android.R.string.cancel, null)
				.create();
	}


	private View inflate(int i) {
		return getActivity().getLayoutInflater().inflate(i, null);
	}

	private class PositiveButtonListener implements DialogInterface.OnClickListener {

		private static final String IP_PART_PATTERN = "(\\d?[1-9]|1\\d\\d|2[0-4]\\d|25[0-5]|0)";
		private static final String ADDRESS_PATTERN =
				"^" + IP_PART_PATTERN + "\\." + IP_PART_PATTERN + "\\." + IP_PART_PATTERN + "\\." + IP_PART_PATTERN + "$";
		private static final String PORT_PATTERN = "^\\d{4}$";

		@Override
		public void onClick(DialogInterface dialog, int which) {

			CharSequence port = ((TextView) getDialog().findViewById(R.id.port)).getText();

			CharSequence address1 = ((TextView) getDialog().findViewById(R.id.address1)).getText();
			CharSequence address2 = ((TextView) getDialog().findViewById(R.id.address2)).getText();
			CharSequence address3 = ((TextView) getDialog().findViewById(R.id.address3)).getText();
			CharSequence address4 = ((TextView) getDialog().findViewById(R.id.address4)).getText();

			String address = address1 + "." + address2 + "." + address3 + "." + address4;

			if (Pattern.matches(ADDRESS_PATTERN, address) && Pattern.matches(PORT_PATTERN, port)) {

				ServerDAO_.getInstance_(getActivity())
						.createServer(address, Integer.valueOf(port.toString()));

			} else {
				Toast.makeText(getActivity(), R.string.wrong_address_format, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
