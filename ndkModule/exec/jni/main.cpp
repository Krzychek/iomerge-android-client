#include <unistd.h>
#include <stddef.h>
#include <stdio.h>
#include <errno.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <linux/uinput.h>
#include "IOManager.h"

int make_named_socket();

const char name[] = "/data/data/org.kbieron.iomerge.android/daemon-socket";


int main() {
    int socketId = make_named_socket();
    struct input_event event;
    IOManager ioManager(false, true);

    while (1) {
        int recv_len = recvfrom(socketId, &event, sizeof(event), 0, NULL, NULL);

        if (recv_len == -1) {
            printf("Kucze kucze, to chyba jakiś błąd!!");
            fflush(stdout);
        }

        ioManager.handleMsg(event);

    }
}

int make_named_socket() {
    struct sockaddr_un addr;
    int sock;
    size_t size;

    /* Create the socket. */
    sock = socket(AF_LOCAL, SOCK_DGRAM, 0);
    if (sock < 0) {
        printf("socket");
        fflush(stdout);
        exit(EXIT_FAILURE);
    }

    /* Bind a name to the socket. */
    addr.sun_family = AF_LOCAL;
    memcpy(addr.sun_path, name, sizeof(name));

    if (bind(sock, (struct sockaddr *) &addr, sizeof(addr)) < 0) {
        printf("bind");
        fflush(stdout);
        exit(EXIT_FAILURE);
    }

    return sock;
}