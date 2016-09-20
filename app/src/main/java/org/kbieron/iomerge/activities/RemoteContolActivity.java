package org.kbieron.iomerge.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.kbieron.iomerge.android.R;
import org.kbieron.iomerge.views.RemoteControlView;


@EActivity(R.layout.remote_control_activity)
public class RemoteContolActivity extends Activity {

	@ViewById(R.id.remote_control_view)
	RemoteControlView remoteControlView;

	@SystemService
	InputMethodManager inputMethodManager;

	@Click(R.id.keyboard_btn)
	void showKeyboard() {
		inputMethodManager.showSoftInput(remoteControlView, InputMethodManager.SHOW_FORCED);
	}

	@Override
	protected void onStart() {
		super.onStart();
		checkDrawOverlayPermission();
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void checkDrawOverlayPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext()))
			startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
	}
}
