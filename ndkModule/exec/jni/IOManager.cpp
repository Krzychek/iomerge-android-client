#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <linux/input.h>
#include <linux/uinput.h>
#include "IOManager.h"

#define die(str, args...) do { \
            printf(str); \
            fflush(stdout);\
            exit(EXIT_FAILURE); \
        } while(0)

IOManager::IOManager(bool useKbd, bool useMouse) : kbdEnabled(useKbd), mouseEnabled(useMouse) {
    if (!(useKbd || useMouse)) {
        // FIXME throw error;
    }

    fd = open("/dev/uinput", O_WRONLY | O_NONBLOCK);
    if (fd < 0)
        die("error: open");

    initializeKbd();
    initializeMouse();

    memset(&device, 0, sizeof(device));
    snprintf(device.name, UINPUT_MAX_NAME_SIZE, "IOMerge_io_device");
    device.id.bustype = BUS_USB;
    device.id.vendor = 0x1;
    device.id.product = 0x1;
    device.id.version = 1;

    if (write(fd, &device, sizeof(device)) < 0)
        die("error: write");

    if (ioctl(fd, UI_DEV_CREATE) < 0)
        die("error: ioctl");

    // setup event prototypes
    event_size = sizeof(struct input_event);

    memset(&event_x, 0, event_size);
    event_x.type = EV_REL;
    event_x.code = REL_X;

    memset(&event_y, 0, event_size);
    event_y.type = EV_REL;
    event_y.code = REL_Y;

    memset(&event_sync, 0, event_size);
    event_sync.type = EV_SYN;
}

IOManager::~IOManager() {
    if (ioctl(fd, UI_DEV_DESTROY) < 0)
        die("error: ioctl");

    close(fd);
}

void IOManager::initializeKbd() {
    if (kbdEnabled) {
        // TODO
    }
}

void IOManager::initializeMouse() {
    if (mouseEnabled) {
        if (ioctl(fd, UI_SET_EVBIT, EV_KEY) < 0)
            die("error: ioctl");
        if (ioctl(fd, UI_SET_KEYBIT, BTN_LEFT) < 0)
            die("error: ioctl");
        if (ioctl(fd, UI_SET_KEYBIT, BTN_RIGHT) < 0)
            die("error: ioctl");

        if (ioctl(fd, UI_SET_EVBIT, EV_REL) < 0)
            die("error: ioctl");
        if (ioctl(fd, UI_SET_RELBIT, REL_X) < 0)
            die("error: ioctl");
        if (ioctl(fd, UI_SET_RELBIT, REL_Y) < 0)
            die("error: ioctl");

    }
}

void IOManager::handleMsg(struct my_event* event) {

    switch (event->type) {
        case EV_REL:

            event_x.value = event[0].value;
            write(fd, &event_x, sizeof(event_x));

            event_y.value = event[1].value;
            write(fd, &event_y, event_size);

            write(fd, &event_sync, event_size);

            break;
        default:

            struct input_event data;
            memset(&data, 0, sizeof(data));

            data.code = event->code;
            data.type = event->type;
            data.value = event->value;
            write(fd, &data, sizeof(data));

            write(fd, &event_sync, sizeof(event_sync));
    }


}
