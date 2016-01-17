#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <linux/uinput.h>
#include <sys/stat.h>


/*--------------- key map ----------------*/ // TODO fill whole map
int getEventCode(int keyCode) {
    switch (keyCode) {
        case 27: return KEY_ESC;
        case 49: return KEY_1;
        case 50: return KEY_2;
        case 51: return KEY_3;
        case 52: return KEY_4;
        case 53: return KEY_5;
        case 54: return KEY_6;
        case 55: return KEY_7;
        case 56: return KEY_8;
        case 57: return KEY_9;
        case 48: return KEY_0;
        case 45: return KEY_MINUS;
        case 61: return KEY_EQUAL;
        case 8: return KEY_BACKSPACE;
            //        case 27: return KEY_TAB;
        case 81: return KEY_Q;
        case 87: return KEY_W;
        case 69: return KEY_E;
        case 82: return KEY_R;
        case 84: return KEY_T;
        case 89: return KEY_Y;
        case 85: return KEY_U;
        case 73: return KEY_I;
        case 79: return KEY_O;
        case 80: return KEY_P;
        case 91: return KEY_LEFTBRACE;
        case 93: return KEY_RIGHTBRACE;
        case 10: return KEY_ENTER;
        case 17: return KEY_LEFTCTRL;
        case 65: return KEY_A;
        case 83: return KEY_S;
        case 68: return KEY_D;
        case 70: return KEY_F;
        case 71: return KEY_G;
        case 72: return KEY_H;
        case 74: return KEY_J;
        case 75: return KEY_K;
        case 76: return KEY_L;
        case 59: return KEY_SEMICOLON;
        case 222: return KEY_APOSTROPHE;
        case 65406: return KEY_RIGHTALT;
            //        case 65406: return KEY_GRAVE;
        case 16: return KEY_LEFTSHIFT;
            //        case 15: return KEY_BACKSLASH;
        case 90: return KEY_Z;
        case 88: return KEY_X;
        case 67: return KEY_C;
        case 86: return KEY_V;
        case 66: return KEY_B;
        case 78: return KEY_N;
        case 77: return KEY_M;
        case 44: return KEY_COMMA;
        case 46: return KEY_DOT;
        case 47: return KEY_SLASH;
            //        case 54: return KEY_RIGHTSHIFT;
            //        case 55: return KEY_KPASTERISK;
        case 18: return KEY_LEFTALT;
        case 32: return KEY_SPACE;
            //        case 58: return KEY_CAPSLOCK;
            //        case 59: return KEY_F1;
            //        case 60: return KEY_F2;
            //        case 61: return KEY_F3;
            //        case 62: return KEY_F4;
            //        case 63: return KEY_F5;
            //        case 64: return KEY_F6;
            //        case 65: return KEY_F7;
            //        case 66: return KEY_F8;
            //        case 67: return KEY_F9;
            //        case 68: return KEY_F10;
            //        case 69: return KEY_NUMLOCK;
            //        case 70: return KEY_SCROLLLOCK;
            //        case 71: return KEY_KP7;
            //        case 72: return KEY_KP8;
            //        case 73: return KEY_KP9;
            //        case 74: return KEY_KPMINUS;
            //        case 75: return KEY_KP4;
            //        case 76: return KEY_KP5;
            //        case 77: return KEY_KP6;
            //        case 78: return KEY_KPPLUS;
            //        case 79: return KEY_KP1;
            //        case 80: return KEY_KP2;
            //        case 81: return KEY_KP3;
            //        case 82: return KEY_KP0;
        case 37: return KEY_LEFT;
        case 38: return KEY_UP;
        case 39: return KEY_RIGHT;
        case 40: return KEY_DOWN;
            //        case 83: return KEY_KPDOT;
        default:return -1;
    }
}


/*--------------- structures ----------------*/
struct my_event {
    int type;
    int code;
    int value;
};

/*---------------- constants ----------------*/
const char NAME[] = "/data/data/org.kbieron.iomerge/cache/iomerge_fifo";


/*------------ shared functions -------------*/
void send_event(struct my_event* event, size_t count) {
    FILE* fp;

    if ((fp = fopen(NAME, "wb")) == NULL) {
        return;
    }
    fwrite(event, sizeof(struct my_event), count, fp);
    fclose(fp);
}



/*-------------- JNI functions --------------*/
/*------------------ MOUSE ------------------*/
JNIEXPORT void JNICALL
Java_org_kbieron_iomerge_services_InputDevice_mouseMove(JNIEnv* env, jobject instance, jshort x, jshort y) {

    struct my_event event[2];
    memset(&event, 0, sizeof(struct my_event) * 2);

    // x
    event[0].type = EV_REL;
    event[0].code = REL_X;
    event[0].value = x;
    // y
    event[1].type = EV_REL;
    event[1].code = REL_Y;
    event[1].value = y;

    send_event(event, 2);
}

JNIEXPORT void JNICALL
Java_org_kbieron_iomerge_services_InputDevice_mousePress(JNIEnv* env, jobject instance) {
    struct my_event event;
    memset(&event, 0, sizeof(struct my_event));

    // x
    event.type = EV_KEY;
    event.code = BTN_LEFT;
    event.value = 1;

    send_event(&event, 1);
}

JNIEXPORT void JNICALL
Java_org_kbieron_iomerge_services_InputDevice_mouseRelease(JNIEnv* env, jobject instance) {
    struct my_event event;
    memset(&event, 0, sizeof(struct my_event));

    // x
    event.type = EV_KEY;
    event.code = BTN_LEFT;
    event.value = 0;

    send_event(&event, 1);

}

JNIEXPORT void JNICALL
Java_org_kbieron_iomerge_services_InputDevice_mouseWheel(JNIEnv* env, jobject instance, jint value) {

    struct my_event event;
    memset(&event, 0, sizeof(struct my_event));

    event.type = EV_REL;
    event.code = REL_WHEEL;
    event.value = value;

    send_event(&event, 1);
}

/*----------------- KEYBOARD ----------------*/
JNIEXPORT void JNICALL
Java_org_kbieron_iomerge_services_InputDevice_keyPress(JNIEnv* env, jobject instance, jint c) {
    int eventCode = getEventCode(c);
    if (eventCode == -1) return;

    struct my_event event;
    memset(&event, 0, sizeof(struct my_event));

    // x
    event.type = EV_KEY;
    event.code = eventCode;
    event.value = 1;

    send_event(&event, 1);
}

JNIEXPORT void JNICALL
Java_org_kbieron_iomerge_services_InputDevice_keyRelease(JNIEnv* env, jobject instance, jint c) {
    int eventCode = getEventCode(c);
    if (eventCode == -1) return;

    struct my_event event;
    memset(&event, 0, sizeof(struct my_event));

    // x
    event.type = EV_KEY;
    event.code = eventCode;
    event.value = 0;

    send_event(&event, 1);
}

/*------------------ SYSTEM -----------------*/
JNIEXPORT void JNICALL
Java_org_kbieron_iomerge_services_MessageProcessor_stop(JNIEnv* env, jobject instance) {
    // TODO
}

JNIEXPORT void JNICALL
Java_org_kbieron_iomerge_services_InputDevice_initializePipe(JNIEnv* env, jobject instance) {
    mknod(NAME, S_IFIFO | 0666, 0);
}