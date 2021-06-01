cmake_minimum_required(VERSION 3.10.2)


project("imu_9d_ekf")

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp)

include_directories(src/main/cpp/Eigen)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries( # Specifies the target library.
        native-lib
        ${log-lib}
        # Links the target library to the log library
        # included in the NDK.
        log
        )