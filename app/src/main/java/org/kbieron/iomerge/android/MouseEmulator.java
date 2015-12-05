package org.kbieron.iomerge.android;

public class MouseEmulator {
    public native void emulate();

    static {
        System.loadLibrary("native");
    }
}
