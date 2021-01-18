# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#打印LOG
message(STATUS "Cmake build type is: " ${CMAKE_BUILD_TYPE})
message(STATUS "Cmake build android abi is: " ${ANDROID_ABI})

aux_source_directory(src/main/cpp/ DIR_LIB_SOURCE)
add_library(flyfun_boost SHARED ${DIR_LIB_SOURCE})
include_directories(src/main/cpp/include)

find_library(
        # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries( # Specifies the target library.
        flyfun_boost

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

#file(GLOB INCLUDE_FILES "/include/json/*.h")
#install(FILES
#        ${INCLUDE_FILES}
#        DESTINATION ${CMAKE_INSTALL_INCLUDEDIR}/json)


##查找log库
#find_library( # Sets the name of the path variable.
#        log-lib
#
#        # Specifies the name of the NDK library that
#        # you want CMake to locate.
#        log)


# 工程名
#project(library_commonsdk_base)
#静态方式加载
#add_library(crypto STATIC IMPORTED )
# 设置库包含目录（头文件目录）
#include_directories("include/thirdpart/openssl/include") # Not needed for CMake >= 2.8.11
#引入第三方.a库
#set_target_properties(crypto PROPERTIES IMPORTED_LOCATION include/thirdpart/openssl/lib/libcrypto.lib)


# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

#target_link_libraries(
#        ffg_support
#        ${log-lib}
#        ${CMAKE_CURRENT_SOURCE_DIR}/openssl/libs/libcrypto.a
#        ${CMAKE_CURRENT_SOURCE_DIR}/openssl/libs/libssl.a
#)
#target_link_libraries( library_commonsdk_base include/thirdpart/openssl/lib/libcrypto.lib )
#target_link_libraries(  device-lib  ${log-lib} )
#target_link_libraries(  kkk_core  ${log-lib} )