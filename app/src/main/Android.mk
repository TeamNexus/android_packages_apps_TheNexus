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

# LOCAL_PATH:= $(call my-dir)
# include $(CLEAR_VARS)
#
# LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res \
#     frameworks/support/design/res \
#     frameworks/support/v7/appcompat/res
#
# LOCAL_AAPT_FLAGS := \
#     --auto-add-overlay \
#     --extra-packages android.support.design \
#     --extra-packages android.support.v7.appcompat \
#     --extra-packages android.support.v4.support
#
# LOCAL_STATIC_JAVA_LIBRARIES += \
#     android-support-design \
#     android-support-v4 \
#     android-support-v7-appcompat
#
# LOCAL_SRC_FILES := $(call all-java-files-under, java)
#
# LOCAL_PACKAGE_NAME       := TheNexus
# LOCAL_OVERRIDES_PACKAGES := TheNexus-prebuilt
# LOCAL_CERTIFICATE        := platform
# LOCAL_PRIVILEGED_MODULE  := true
#
# LOCAL_PROGUARD_FLAG_FILES := $(LOCAL_PATH)/../../proguard-rules.pro
#
# include $(BUILD_PACKAGE)
