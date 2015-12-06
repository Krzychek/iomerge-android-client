#include <unistd.h>
#include "IOManager.h"

int
main(void) {
    IOManager ioManager(false, true);

    int dy = 0;
    int dx = 10;
    while (dx != 0) {
        dx = -dx;

        ioManager.moveMouse(dx, dy);
        sleep(1);
    }

    sleep(2);


    return 0;
}
