#include <linux/uinput.h>

struct my_event {
    int type;
    int code;
    int value;
};

class IOManager {
public:
    IOManager(bool useKbd, bool useMouse);
    ~IOManager();
    void handleMsg(struct my_event *event);

private:

    // event prototypes
    size_t event_size;
    struct input_event event_x;
    struct input_event event_y;
    struct input_event event_sync;

    // enabled features
    bool kbdEnabled, mouseEnabled;

    // device
    int fd;
    struct uinput_user_dev device;

    // initializer
    void initializeKbd();
    void initializeMouse();
};