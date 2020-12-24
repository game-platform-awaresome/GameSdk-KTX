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

#表示把src/main/cpp加入到include目录，这样在代码中使用：#include <...>就能访问到头文件
include_directories(src/main/cpp)

file(GLOB native_srcs
        "*.cpp"
#        "*.h"
        "src/main/cpp/include/*.h"
        "src/main/cpp/include/*.cpp"
        )

add_library(
        flyfun_boost

        SHARED

        ${native_srcs}
        src/main/cpp/flyfun_boost.cpp

        src/main/cpp/include/constant.h

        src/main/cpp/include/bean/common.h

        src/main/cpp/include/encryption/base64.h
        src/main/cpp/include/encryption/base64.cpp

        src/main/cpp/include/encryption/url_utils.h
        src/main/cpp/include/encryption/url_utils.cpp

        src/main/cpp/include/encryption/aes/aes_utils.h
        src/main/cpp/include/encryption/aes/aes_utils.cpp

        src/main/cpp/include/encryption/rsa/rsa_utils.h
        src/main/cpp/include/encryption/rsa/rsa_utils.cpp

        src/main/cpp/include/logger.h
        src/main/cpp/include/logger.cpp

        src/main/cpp/include/jtools.h
        src/main/cpp/include/jtools.cpp

        src/main/cpp/include/json/json.h
        src/main/cpp/include/json/json-forwards.h
        src/main/cpp/include/json/jsoncpp.cpp

        src/main/cpp/include/params_map.h
        src/main/cpp/include/params_map.cpp
)

##添加两个预编译库
#add_library(# Sets the name of the library.
#        openssl-crypto
#        # Sets the library as a static library.
#        STATIC
#        IMPORTED)
#
#set_target_properties(
#        # Specifies the target library.
#        openssl-crypto
#        # Specifies the parameter you want to define.
#        PROPERTIES IMPORTED_LOCATION
#        # Provides the path to the library you want to import.
#        ${CMAKE_SOURCE_DIR}/src/main/cpp/openssl/libs/${ANDROID_ABI}/libcrypto.a)
#
#add_library(# Sets the name of the library.
#        openssl-ssl
#        # Sets the library as a static library.
#        STATIC
#        IMPORTED)
#
#set_target_properties(
#        # Specifies the target library.
#        openssl-ssl
#        # Specifies the parameter you want to define.
#        PROPERTIES IMPORTED_LOCATION
#        # Provides the path to the library you want to import.
#        ${CMAKE_SOURCE_DIR}/src/main/cpp/openssl/libs/${ANDROID_ABI}/libssl.a)

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
#file(GLOB native_srcs
#        "*.cpp"
#        "src/main/cpp/include/*.cpp"
#        "src/main/cpp/include/encrypt/*.cpp"
#        "src/main/cpp/include/encrypt/rsa/*.cpp"
#        )

#file(GLOB encrypt_srcs "include/encrypt/*.c")
#file(GLOB include_srcs "include/*.cpp")

#add_library( # Sets the name of the library.
#        ffg_support
#        SHARED
#        #新加cpp后可能会遇到链接不了动态库的问题，注释这行解开下行手动添加下，编译可以后再切换回来
#        #        ${native_srcs} #${include_srcs}
#        /flyfun.cpp
#        #        native-lib.cpp
#        #        jsoncpp.cpp
#        #        kkk_core.cpp
#        #        include/kkk_tools.cpp
#        #        kkk_param_tools.cpp
#        #        kkk_fuse_manager.cpp
#        #        include/encrypt/kkk_aes.h
#        )

#add_library( # Sets the name of the library.
#             native-lib
#             SHARED
#            #${native_srcs}
#        native-lib.cpp
#        )
##生成so动态库
#add_library(
#        device-lib
#        SHARED
#        device-lib.cpp  )
#add_library(
#        kkk_core
#        SHARED
#        kkk_core.cpp  )

#include_directories(openssl)

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