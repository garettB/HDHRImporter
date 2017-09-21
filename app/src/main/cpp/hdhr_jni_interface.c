/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>

#include "hdhomerun.h"

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   hello-jni/app/src/main/java/com/example/hellojni/HelloJni.java
 */
JNIEXPORT jstring JNICALL
Java_com_dbiapps_hdhr_hdhrimporter_MainActivity_hello(JNIEnv *env,
                                                     jobject thiz) {


    struct hdhomerun_discover_device_t discover_array[10];
    int num_found = 0;

    num_found = hdhomerun_discover_find_devices_custom_v2(0, HDHOMERUN_DEVICE_TYPE_TUNER, HDHOMERUN_DEVICE_ID_WILDCARD, discover_array, 10);
    //hdhomerun_discover_find_devices_custom(0, HDHOMERUN_DEVICE_TYPE_TUNER, HDHOMERUN_DEVICE_ID_WILDCARD, discover_array, 10);

    printf("discover(): num_found %d",num_found );


    jstring result;
    if(num_found < 0)
    {
        printf("Error discovering devices");
        result = (*env)->NewStringUTF(env,"nadda");
    } else {

        printf("device_auth: %s", discover_array[0].device_auth);
        result = (*env)->NewStringUTF(env,("{\"device_url\":\"%s\", \"device_auth\" : \"%s\"}", discover_array[0].base_url, discover_array[0].device_auth));
        //stream URL example http://192.168.86.215:5004/auto/v23.1
    }

    return result;
    //return 4;
}