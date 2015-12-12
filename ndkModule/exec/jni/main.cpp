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


/*---------- constants ----------*/
const char NAME[] = "/data/data/org.kbieron.iomerge/cache/iomerge_fifo";

int main() {
    FILE* fp;

    struct my_event buff[4];
    memset(&buff, 0, sizeof(buff));

    IOManager ioManager(false, true);

    while (1) {
        fp = fopen(NAME, "rb");
        fgets((char*) &buff, sizeof(buff), fp);

        ioManager.handleMsg(buff);

        fclose(fp);
    }
}
