package org.kbieron.iomerge.notifications;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.kbieron.iomerge.android.R;

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;


@EBean
public class NotificationFactory {

    @StringRes(R.string.app_name)
    protected static String appName;

    @StringRes(R.string.server_connected_ticker)
    protected static String connectedTicker;

    @StringRes(R.string.server_connected_text)
    protected static String connectedText;

    public Notification serverConnected(final Context context) {
        return new NotificationCompat.Builder(context) //
                .setContentTitle(appName) //
                .setContentText(connectedText) //
                .setPriority(PRIORITY_MIN) //
                .setTicker(connectedTicker) //
                .build();
    }
}
