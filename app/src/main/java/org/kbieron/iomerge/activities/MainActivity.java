package org.kbieron.iomerge.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.kbieron.iomerge.android.R;
import org.kbieron.iomerge.io.InputDevice;
import org.kbieron.iomerge.services.EventServerClient;
import org.kbieron.iomerge.services.EventServerClient_;

import java.io.IOException;


@EActivity(R.layout.main_activity)
public class MainActivity extends Activity implements NavigationView.OnNavigationItemSelectedListener {

    @ViewById(R.id.fab)
    protected FloatingActionButton fab;

    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;

    @ViewById(R.id.drawer_layout)
    protected DrawerLayout drawer;

    @ViewById(R.id.nav_view)
    protected NavigationView navigationView;

    @Bean
    protected InputDevice inputDevice;

    protected EventServerClient eventServerClient;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof EventServerClient.Binder) {
                eventServerClient = ((EventServerClient.Binder) service).getService();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @AfterInject
    protected void bindServices() {
        bindService(EventServerClient_.intent(getApplication()).get(), mConnection, BIND_AUTO_CREATE);
    }

    @AfterViews
    protected void afterViews() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Click(R.id.fab)
    protected void clickBtn(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT) //
                .show();
        new EventServerClient();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.iomerge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.action_settings) return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_connect:
                eventServerClient.connect("192.168.1.135", 7698);
                break;
            case R.id.nav_disconnect:
                eventServerClient.disconnect();
                break;
            default:
                Log.w("MainActivitt", "Not supported navigation item");
        }

        return true;
    }
}
