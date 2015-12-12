#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/un.h>
#include <sys/stat.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <linux/uinput.h>
#include "IOManager.h"

/*---------- predeclared shared functions ----------*/
FILE* get_fifo();

void send_event(struct input_event &event);

/*---------- constants ----------*/
const char NAME[] = "/data/local/tmp/iomerge_fifo";

int main() {
    struct input_event event;
    IOManager ioManager(false, true);

    FILE *fp;
    char readbuf[80];

    /* Create the FIFO if it does not exist */
    umask(0);
    mknod(NAME, S_IFIFO|0666, 0);
    printf("created fifo");
    fflush(stdout);

    while(1)
    {
        fp = fopen(NAME, "r");
        fgets(readbuf, 80, fp);
        printf("Received string: %s\n", readbuf);
        fflush(stdout);
        fclose(fp);
    }
}

