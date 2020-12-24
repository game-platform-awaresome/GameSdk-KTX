//
// Created by #Suyghur, on 2020/12/18.
//

#ifndef FLYFUNGAMESDK_LOGGER_H
#define FLYFUNGAMESDK_LOGGER_H

#include <string>
#include <android/log.h>
#include <jni.h>

#define TAG "flyfun_jni"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__) // 定义LOGD类型
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__) // 定义LOGD类型

using namespace std;

class Logger {
public:
    static void init(JNIEnv *env, jobject context);

    static void logd(const string &msg);

    static void loge(const string &msg);

    static void log_handler(JNIEnv *env, const string &msg);

private:
    static bool is_debug;
};

#endif //FLYFUNGAMESDK_LOGGER_H
