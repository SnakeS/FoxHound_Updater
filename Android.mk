LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_SRC_FILES += \
	src/foxhoundupdaterapp/interfaces/IDownloadService.aidl \
	src/foxhoundupdaterapp/interfaces/IDownloadServiceCallback.aidl \
	src/foxhoundupdaterapp/interfaces/IUpdateCheckService.aidl \
	src/foxhoundupdaterapp/interfaces/IUpdateCheckServiceCallback.aidl \

aidl_files := \
	packages/apps/appfoxhound/src/foxhoundupdaterapp/interfaces/IDownloadService.aidl \
	packages/apps/appfoxhound/src/foxhoundupdaterapp/interfaces/IDownloadServiceCallback.aidl \
	packages/apps/appfoxhound/src/foxhoundupdaterapp/interfaces/IUpdateCheckService.aidl \
	packages/apps/appfoxhound/src/foxhoundupdaterapp/interfaces/IUpdateCheckServiceCallback.aidl \

LOCAL_PACKAGE_NAME := foxhound
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
