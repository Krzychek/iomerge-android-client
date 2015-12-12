#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <linux/uinput.h>

/*---------- predeclared shared functions ----------*/
FILE* get_fifo();

void send_event(struct input_event *event);

/*---------- constants ----------*/
const char NAME[] = "/data/local/tmp/iomerge_fifo";


/*---------- JNI functions ----------*/
JNIEXPORT void JNICALL Java_org_kbieron_iomerge_rmi_IOManager_moveMouse(JNIEnv* env, jobject instance, jint x, jint y) {
    struct input_event event;
    event.type = KEY_DOWN;
    // TODO bla bla..
    send_event(&event);
}

JNIEXPORT void JNICALL Java_org_kbieron_iomerge_rmi_IOManager_stop(JNIEnv* env, jobject instance) {
}

/*---------- shared functions implementation ----------*/
void send_event(struct input_event *event) {
    FILE* fp = get_fifo();

    fputs("blabla", fp);

    fclose(fp);
}


FILE* get_fifo() {
    static FILE* fp = NULL;

    if (fp != NULL) return fp;

    if ((fp = fopen(NAME, "w")) == NULL) {
        perror("fopen");
        exit(1);
    }

    return fp;
}