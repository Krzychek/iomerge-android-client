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

int const ENABLED_KEYS_LENGTH = 88;
int const ENABLED_KEYS[] = { //
        KEY_ESC, KEY_1, KEY_2, KEY_3, KEY_4, KEY_5, KEY_6, KEY_7, KEY_8, KEY_9, KEY_0, KEY_MINUS, KEY_EQUAL, KEY_BACKSPACE, //
        KEY_TAB, KEY_Q, KEY_W, KEY_E, KEY_R, KEY_T, KEY_Y, KEY_U, KEY_I, KEY_O, KEY_P, KEY_LEFTBRACE, KEY_RIGHTBRACE, KEY_ENTER, //
        KEY_A, KEY_S, KEY_D, KEY_F, KEY_G, KEY_H, KEY_J, KEY_K, KEY_L, KEY_SEMICOLON, KEY_APOSTROPHE, KEY_GRAVE, //
        KEY_LEFTSHIFT, KEY_BACKSLASH, KEY_Z, KEY_X, KEY_C, KEY_V, KEY_B, KEY_N, KEY_M, KEY_COMMA, KEY_DOT, KEY_SLASH, KEY_RIGHTSHIFT, KEY_KPASTERISK, //
        KEY_CAPSLOCK, //
        KEY_F1, KEY_F2, KEY_F3, KEY_F4, KEY_F5, KEY_F6, KEY_F7, KEY_F8, KEY_F9, KEY_F10, KEY_NUMLOCK, KEY_SCROLLLOCK, //
        KEY_KP7, KEY_KP8, KEY_KP9, KEY_KPMINUS, //
        KEY_KP4, KEY_KP5, KEY_KP6, KEY_KPPLUS, //
        KEY_KP1, KEY_KP2, KEY_KP3, KEY_KP0, //
        KEY_LEFTCTRL, KEY_LEFTALT,KEY_SPACE, KEY_RIGHTALT, KEY_RIGHTCTRL, // 1st row
        KEY_LEFT, KEY_RIGHT, KEY_UP, KEY_DOWN, // arrows
};

void IOManager::initializeKbd() {
    if (kbdEnabled) {
        if (ioctl(fd, UI_SET_EVBIT, EV_KEY) < 0)
            die("error: ioctl");

        for (int i = 0; i < ENABLED_KEYS_LENGTH; ++i) {
            if (ioctl(fd, UI_SET_KEYBIT, ENABLED_KEYS[i]) < 0)
                die("error: ioctl");

        }

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
        if (ioctl(fd, UI_SET_RELBIT, REL_WHEEL) < 0)
            die("error: ioctl");

    }
}

void IOManager::handleMsg(struct my_event* event) {

    if (event->type == EV_REL && event->code == REL_X) {
        event_x.value = event[0].value;
        write(fd, &event_x, sizeof(event_x));

        event_y.value = event[1].value;
        write(fd, &event_y, event_size);

        write(fd, &event_sync, event_size);
    } else {
        struct input_event data;
        memset(&data, 0, sizeof(data));

        data.code = event->code;
        data.type = event->type;
        data.value = event->value;
        write(fd, &data, sizeof(data));

        write(fd, &event_sync, sizeof(event_sync));
    }
}
