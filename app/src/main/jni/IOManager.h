#ifndef IOMERGE_IOMANAGER_H
#define IOMERGE_IOMANAGER_H


class IOManager {
public:
    IOManager(bool useKbd, bool useMouse);
    void moveMouse(int x, int y);
    void typeKey(int key);


private:
    bool kbdEnabled, mouseEnabled;
    int fd;
    struct uinput_user_dev device;
    ~IOManager();
    void initializeKbd();
    void initializeMouse();
};


#endif //IOMERGE_IOMANAGER_H
