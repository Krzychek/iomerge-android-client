#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <linux/uinput.h>


/*---------- predeclared shared functions ----------*/
int get_named_socket();
void send_event(struct input_event &event);

/*---------- constants ----------*/
const char name[] = "\0iomerge-daemon";

/*---------- JNI functions ----------*/
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"

JNIEXPORT void JNICALL Java_org_kbieron_iomerge_rmi_IOManager_moveMouse(JNIEnv *env, jobject instance, jint x, jint y) {
    // TODO
}


JNIEXPORT void JNICALL Java_org_kbieron_iomerge_rmi_IOManager_stop(JNIEnv *env, jobject instance) {
    struct input_event event;
    event.type = KEY_DOWN;
    // TODO bla bla..
    send_event(event);
}
#pragma clang diagnostic pop

/*---------- shared functions implementation ----------*/
void send_event(struct input_event &event) {
    int socket = get_named_socket();
    //    mkfifo(cpath, 0);
    //    chmod(cpath, S_IRWXU | S_IROTH | S_IRGRP);
    //    addr.sun_family = AF_UNIX;
    //    memcpy(addr.sun_path, name, sizeof(name) - 1);
    //
    //
    //    int recv_len = sendto(socketId, &event, sizeof(event), 0, (struct sockaddr *) &addr, sizeof(addr.sun_family) + sizeof(name));
    // TODO: implement
}


int get_named_socket() {
    int sock = socket(AF_UNIX, SOCK_DGRAM, 0);

    if (sock < 0) {
        perror("socket");
        exit(EXIT_FAILURE);
    }
    return sock;
}