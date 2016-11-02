package org.kbieron.iomerge.gui.main.serverList

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.pawegio.kandroid.longToast
import org.kbieron.iomerge.android.R
import org.kbieron.iomerge.database.ServerDAO
import java.util.regex.Pattern


class AddServerDialogFragment : DialogFragment() {

	private val IP_PART_PATTERN = "(\\d?[1-9]|1\\d\\d|2[0-4]\\d|25[0-5]|0)"
	private val ADDRESS_PATTERN =
			"^$IP_PART_PATTERN\\.$IP_PART_PATTERN\\.$IP_PART_PATTERN\\.$IP_PART_PATTERN$"
	private val PORT_PATTERN = "^\\d{4}$"

	override fun onCreateDialog(savedInstanceState: Bundle): Dialog = AlertDialog.Builder(activity)
			.setMessage(R.string.add_server_dialog_header)
			.setView(inflate(R.layout.add_server_dialog_layout))
			.setPositiveButton(R.string.add, PositiveButtonListener())
			.setNegativeButton(android.R.string.cancel, null)
			.create()


	private fun inflate(i: Int): View {
		return activity.layoutInflater.inflate(i, null)
	}

	private inner class PositiveButtonListener : DialogInterface.OnClickListener {
		private val port: CharSequence
			get() = (dialog.findViewById(R.id.port) as TextView).text

		private val address1: CharSequence
			get() = (dialog.findViewById(R.id.address1) as TextView).text

		private val address2: CharSequence
			get() = (dialog.findViewById(R.id.address2) as TextView).text

		private val address3: CharSequence
			get() = (dialog.findViewById(R.id.address3) as TextView).text

		private val address4: CharSequence
			get() = (dialog.findViewById(R.id.address4) as TextView).text


		override fun onClick(dialog: DialogInterface, which: Int) {
			val address = "$address1.$address2.$address3.$address4"

			if (Pattern.matches(ADDRESS_PATTERN, address) && Pattern.matches(PORT_PATTERN, port)) {

				ServerDAO(activity)
						.createServer(address, Integer.valueOf(port.toString())!!)

			} else {
				longToast(activity.resources.getString(R.string.wrong_address_format))
			}
		}

	}
}

