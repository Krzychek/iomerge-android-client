package org.kbieron.iomerge;

import org.androidannotations.annotations.sharedpreferences.SharedPref;


@SharedPref(SharedPref.Scope.UNIQUE)
public interface Preferences {

    String serverAddress();

    int serverPort();

}
