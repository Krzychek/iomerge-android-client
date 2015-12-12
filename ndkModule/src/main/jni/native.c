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
void send_event(struct my_event* event);

void start();

void stop();

/*---------------- constants ----------------*/
const char NAME[] = "/data/data/org.kbieron.iomerge/cache/iomerge_fifo";

/*----------------- globals -----------------*/
FILE* fp;



/*-------------- JNI functions --------------*/
JNIEXPORT void JNICALL Java_org_kbieron_iomerge_rmi_IOManager_moveMouse(JNIEnv* env, jobject instance, jint x, jint y) {
    mknod(NAME, S_IFIFO | 0666, 0);

    struct my_event event[2];

    memset(&event, 0, sizeof(struct my_event));

    // send x
    event[0].type = EV_REL;
    event[0].code = REL_X;
    event[0].value = x;

    // send y
    event[1].type = EV_REL;
    event[1].code = REL_Y;
    event[1].value = y;

    send_event(event);
}

JNIEXPORT void JNICALL Java_org_kbieron_iomerge_rmi_IOManager_stop(JNIEnv* env, jobject instance) {
}

/*------------ implement functions ------------*/
void send_event(struct my_event* event) {
    start();
    fwrite(event, sizeof(struct my_event), 2, fp);
    stop();
}

void start() {
    if ((fp = fopen(NAME, "wb")) == NULL) {
        perror("fopen");
        exit(1);
    }
}

void stop() {
    fclose(fp);
}
