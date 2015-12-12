#include <linux/uinput.h>

class IOManager {
public:
    IOManager(bool useKbd, bool useMouse);
    ~IOManager();
    void handleMsg(struct input_event &event);

private:
    bool kbdEnabled, mouseEnabled;
    int fd;
    struct uinput_user_dev device;
    void initializeKbd();
    void initializeMouse();
};