package org.kbieron.iomerge;

import org.androidannotations.annotations.sharedpreferences.SharedPref;


@SharedPref()
public interface Preferences {

    String serverAddress();

    int serverPort();

}
