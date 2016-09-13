package org.kbieron.iomerge.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import org.androidannotations.annotations.EActivity;
import org.kbieron.iomerge.android.R;


@EActivity(R.layout.remote_control_activity)
public class RemoteContolActivity extends Activity {

	@Override
	protected void onStart() {
		super.onStart();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext()))
			checkDrawOverlayPermission();
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void checkDrawOverlayPermission() {
		startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
	}
}
