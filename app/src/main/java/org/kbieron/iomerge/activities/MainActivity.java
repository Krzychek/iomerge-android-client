package org.kbieron.iomerge.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.kbieron.iomerge.Preferences_;
import org.kbieron.iomerge.android.R;
import org.kbieron.iomerge.services.InputDevice;
import org.kbieron.iomerge.services.NetworkManager;
import org.kbieron.iomerge.services.NetworkManager_;


@EActivity(R.layout.main_activity)
public class MainActivity extends Activity implements NavigationView.OnNavigationItemSelectedListener {

	@ViewById(R.id.toolbar)
	Toolbar toolbar;

	@ViewById(R.id.drawer_layout)
	DrawerLayout drawer;

	@Pref
	Preferences_ prefs;

	@ViewById(R.id.active_server_address)
	TextView activeServerView;

	@ViewById(R.id.nav_view)
	NavigationView navigationView;

	@Bean
	InputDevice inputDevice;

	private NetworkManager.Binder networkManager;

	private ServiceConnection serviceConnection = new ServiceConnection() {


		@Override
		public void onServiceConnected(ComponentName name, IBinder iBinder) {
			if (iBinder instanceof NetworkManager.Binder) {
				networkManager = ((NetworkManager.Binder) iBinder);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};

	@Override
	protected void onStart() {
		super.onStart();

		// FIXME
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getApplicationContext()))
			checkDrawOverlayPermission();
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void checkDrawOverlayPermission() {
		startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
	}

	@AfterInject
	void bindServices() {
		startService(NetworkManager_.intent(this).get());
		bindService(NetworkManager_.intent(this).get(), serviceConnection, BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection);
	}

	@AfterViews
	void afterViews() {
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
			@Override
			public void onDrawerStateChanged(int newState) {
				if (newState != DrawerLayout.STATE_IDLE) {
					((TextView) drawer.findViewById(R.id.active_server_address)) //
							.setText(prefs.serverAddress().get() + ":" + prefs.serverPort().get());
				}
				super.onDrawerStateChanged(newState);
			}
		};
		//noinspection deprecation
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		navigationView.setNavigationItemSelectedListener(this);
	}

	@Override
	public void onBackPressed() {
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.nav_connect:
				networkManager.connect();
				break;
			case R.id.nav_disconnect:
				networkManager.disconnect();
				break;
			default:
				Log.w("MainActivity", "Not supported navigation item");
		}

		return true;
	}
}
