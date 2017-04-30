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

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_STATIC_JAVA_LIBRARIES := \
    android-support-v4 \
    android-support-v7-appcompat \
    android-support-v13 \
    android-support-design
	
LOCAL_SRC_FILES := \
    $(call all-java-files-under, app/src/main/java)
	
LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/app/src/main/res \
	prebuilts/sdk/current/support/v7/appcompat/res \
	prebuilts/sdk/current/support/design/res
	
LOCAL_AAPT_FLAGS := \
	--auto-add-overlay \
	--multi-dex \
	--extra-packages android.support.v7.appcompat

LOCAL_PACKAGE_NAME := TheNexus
LOCAL_PRIVILEGED_MODULE := true
LOCAL_RENDERSCRIPT_TARGET_API := 25
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)
