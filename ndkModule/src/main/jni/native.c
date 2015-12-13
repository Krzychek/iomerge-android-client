#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <linux/uinput.h>
#include <sys/stat.h>

struct my_event {
    int type;
    int code;
    int value;
};


/*------------ declare functions ------------*/
void send_event(struct my_event* event, int count);

/*---------------- constants ----------------*/
const char NAME[] = "/data/data/org.kbieron.iomerge/cache/iomerge_fifo";


/*-------------- JNI functions --------------*/
JNIEXPORT void JNICALL
Java_org_kbieron_iomerge_io_InputDevice_mouseMove(JNIEnv* env, jobject instance, jshort x, jshort y) {

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
Java_org_kbieron_iomerge_io_InputDevice_mousePress(JNIEnv* env, jobject instance) {
    struct my_event event;
    memset(&event, 0, sizeof(struct my_event));

    // x
    event.type = EV_KEY;
    event.code = BTN_LEFT;
    event.value = 1;

    send_event(&event, 1);
}


JNIEXPORT void JNICALL
Java_org_kbieron_iomerge_io_InputDevice_mouseRelease(JNIEnv* env, jobject instance) {
    struct my_event event;
    memset(&event, 0, sizeof(struct my_event));

    // x
    event.type = EV_KEY;
    event.code = BTN_LEFT;
    event.value = 0;

    send_event(&event, 1);

}

JNIEXPORT void JNICALL
Java_org_kbieron_iomerge_io_InputDevice_stop(JNIEnv* env, jobject instance) {
}

/*------------ implement functions ------------*/
void send_event(struct my_event* event, int count) {


    FILE * fp;

    if ((fp = fopen(NAME, "wb")) == NULL) {
        return;
    }
    fwrite(event, sizeof(struct my_event), count, fp);
    fclose(fp);
}

JNIEXPORT void JNICALL
Java_org_kbieron_iomerge_io_InputDevice_start(JNIEnv* env, jobject instance) {
    mknod(NAME, S_IFIFO | 0666, 0);
}