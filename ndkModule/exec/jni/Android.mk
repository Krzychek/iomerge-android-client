LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CFLAGS += -fPIE
LOCAL_LDFLAGS += -fPIE -pie

LOCAL_MODULE    := iomerge
LOCAL_SRC_FILES := IOManager.cpp main.cpp
TARGET_PLATFORM := android-21
TARGET_ARCH_ABI := armeabi


include $(BUILD_EXECUTABLE)

