#
# Copyright (C) 2017 Lukas Berger
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

thenexus_lib_path := $(call my-dir)

#########################
# material-dialogs-core
LOCAL_PATH := $(thenexus_lib_path)/material-dialogs/core/src/main
include $(CLEAR_VARS)

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res \
    $(thenexus_lib_path)/MaterialProgressBar/library/src/main/res \
    frameworks/support/v7/appcompat/res \
    frameworks/support/v7/recyclerview/res

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.annotations \
    --extra-packages android.support.v7.appcompat \
    --extra-packages android.support.v7.recyclerview \
    --extra-packages android.support.v13.support

LOCAL_STATIC_JAVA_LIBRARIES += \
    android-support-annotations \
    android-support-v7-appcompat \
    android-support-v7-recyclerview \
    android-support-v13 \
    material-progressbar

LOCAL_SRC_FILES := $(call all-java-files-under, java)

LOCAL_MODULE      := material-dialogs-core
LOCAL_CERTIFICATE := platform

include $(BUILD_STATIC_JAVA_LIBRARY)

#########################
# material-progressbar
LOCAL_PATH := $(thenexus_lib_path)/MaterialProgressBar/library/src/main
include $(CLEAR_VARS)

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res \
    frameworks/support/v7/appcompat/res

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.compat \
    --extra-packages android.support.annotations \
    --extra-packages android.support.v4 \
    --extra-packages android.support.v7.appcompat

LOCAL_STATIC_JAVA_LIBRARIES += \
    android-support-compat \
    android-support-annotations \
    android-support-v4 \
    android-support-v7-appcompat

LOCAL_SRC_FILES := \
    $(call all-java-files-under, java)

LOCAL_MODULE      := material-progressbar
LOCAL_CERTIFICATE := platform

include $(BUILD_STATIC_JAVA_LIBRARY)
