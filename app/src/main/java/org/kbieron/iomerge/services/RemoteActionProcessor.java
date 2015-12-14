package org.kbieron.iomerge.services;

import java.nio.ByteBuffer;

import static pl.kbieron.iomerge.model.RemoteActionType.BACK_BTN_CLICK;
import static pl.kbieron.iomerge.model.RemoteActionType.HOME_BTN_CLICK;
import static pl.kbieron.iomerge.model.RemoteActionType.KEY_PRESS;
import static pl.kbieron.iomerge.model.RemoteActionType.KEY_RELEASE;
import static pl.kbieron.iomerge.model.RemoteActionType.MENU_BTN_CLICK;
import static pl.kbieron.iomerge.model.RemoteActionType.MOUSE_PRESS;
import static pl.kbieron.iomerge.model.RemoteActionType.MOUSE_RELEASE;
import static pl.kbieron.iomerge.model.RemoteActionType.MOUSE_SYNC;


public abstract class RemoteActionProcessor {

    public void process(byte[] actionBytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(actionBytes);
        byte actionType = byteBuffer.get();

        switch (actionType) {
            case MOUSE_SYNC:
                mouseMove(byteBuffer.getShort(), byteBuffer.getShort());
                break;
            case MOUSE_PRESS:
                mousePress();
                break;
            case MOUSE_RELEASE:
                mouseRelease();
                break;
            case KEY_PRESS:
                keyPress(byteBuffer.getInt());
                break;
            case KEY_RELEASE:
                keyRelease(byteBuffer.getInt());
                break;
            case HOME_BTN_CLICK:
                homeBtnClick();
                break;
            case BACK_BTN_CLICK:
                backBtnClick();
                break;
            case MENU_BTN_CLICK:
                menuBtnClick();
                break;
        }
    }

    public abstract void mouseMove(short x, short y);

    public abstract void mousePress();

    public abstract void mouseRelease();

    public abstract void keyPress(int key);

    public abstract void keyRelease(int key);

    public abstract void homeBtnClick();

    public abstract void backBtnClick();

    public abstract void menuBtnClick();
}
