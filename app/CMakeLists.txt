cmake_minimum_required(VERSION 3.4.1)

add_library( # Specifies the name of the library.
             hdhomerun

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/hdhomerun_channels.c
             src/main/cpp/hdhomerun_channelscan.c
             src/main/cpp/hdhomerun_config.c
             src/main/cpp/hdhomerun_control.c
             src/main/cpp/hdhomerun_debug.c
             src/main/cpp/hdhomerun_device.c
             src/main/cpp/hdhomerun_device_selector.c
             src/main/cpp/hdhomerun_discover.c
             src/main/cpp/hdhomerun_os_posix.c
             src/main/cpp/hdhomerun_pkt.c
             src/main/cpp/hdhomerun_sock_posix.c
             src/main/cpp/hdhomerun_video.c
             src/main/cpp/hdhr_jni_interface.c)

# Include libraries needed for hdhomerun lib
target_link_libraries(hdhomerun
                      android
                      log)