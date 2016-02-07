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

    IOManager ioManager(true, true);

    while (1) {
        fp = fopen(NAME, "rb");
        int read_count = fread(&buff, sizeof(struct my_event), 4, fp);
        fclose(fp);

        for (int i = 0; i < read_count; ++i) {
            ioManager.handleMsg(buff + i);
        }

    }
}
