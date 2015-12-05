#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <linux/input.h>
#include <linux/uinput.h>
#include "IOManager.h"

#define die(str, args...) do { \
            perror(str); \
            exit(EXIT_FAILURE); \
        } while(0)

IOManager::IOManager(bool useKbd, bool useMouse) : kbdEnabled(useKbd), mouseEnabled(useMouse) {
    if (!(useKbd || useMouse)) {
        throw 20;
    }

    fd = open("/dev/uinput", O_WRONLY | O_NONBLOCK);
    if (fd < 0)
        die("error: open");

    initializeKbd();
    initializeMouse();

    if (write(fd, &device, sizeof(device)) < 0)
        die("error: write");

    if (ioctl(fd, UI_DEV_CREATE) < 0)
        die("error: ioctl");

}

IOManager::~IOManager() {
    if (ioctl(fd, UI_DEV_DESTROY) < 0)
        die("error: ioctl");

    close(fd);
}

void IOManager::moveMouse(int x, int y) {
    if (!mouseEnabled) return;
    struct input_event event;

    memset(&event, 0, sizeof(event));
    event.type = EV_REL;
    event.code = REL_X;
    event.value = x;
    if (write(fd, &event, sizeof(event)) < 0)
        die("error: write");

    memset(&event, 0, sizeof(event));
    event.type = EV_REL;
    event.code = REL_Y;
    event.value = y;
    if (write(fd, &event, sizeof(event)) < 0)
        die("error: write");

    memset(&event, 0, sizeof(event));
    event.type = EV_SYN;
    event.code = 0;
    event.value = 0;
    if (write(fd, &event, sizeof(event)) < 0)
        die("error: write");
}

void IOManager::typeKey(int key) {
    if (!kbdEnabled) return;

}

void IOManager::initializeKbd() {
    if (kbdEnabled) {
        // TODO
    }
}


void IOManager::initializeMouse() {
    if (kbdEnabled) {
        if (ioctl(fd, UI_SET_EVBIT, EV_KEY) < 0)
            die("error: ioctl");
        if (ioctl(fd, UI_SET_KEYBIT, BTN_LEFT) < 0)
            die("error: ioctl");

        if (ioctl(fd, UI_SET_EVBIT, EV_REL) < 0)
            die("error: ioctl");
        if (ioctl(fd, UI_SET_RELBIT, REL_X) < 0)
            die("error: ioctl");
        if (ioctl(fd, UI_SET_RELBIT, REL_Y) < 0)
            die("error: ioctl");

        memset(&device, 0, sizeof(device));
        snprintf(device.name, UINPUT_MAX_NAME_SIZE, "IOMerge_io_device");
        device.id.bustype = BUS_USB;
        device.id.vendor = 0x1;
        device.id.product = 0x1;
        device.id.version = 1;
//        device.absmax[REL_X] = 100;
//        device.absmin[REL_X] = -100;
//        device.absmax[REL_Y] = 100;
//        device.absmin[REL_Y] = -100;
    }
}
