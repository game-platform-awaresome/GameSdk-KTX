//
// Created by #Suyghur, on 2020/12/18.
//

#include "logger.h"

bool Logger::is_debug = false;

void Logger::init(JNIEnv *env, jobject context) {
    jclass _clz = env->FindClass("cn/flyfun/support/jarvis/OwnDebugUtils");
    if (_clz == NULL) {
        loge("OwnDebugUtils impl clz is NULL !!!");
        return;
    }

    const char *method_name = "isOwnDebug";
    const char *sig = "(Landroid/content/Context;)Z";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jboolean jresult = env->CallStaticBooleanMethod(_clz, jmethod_id, context);
    is_debug = jresult == JNI_TRUE;
}


void Logger::logd(const string &msg) {
    if(is_debug){
        LOGD("JNI -> %s", msg.c_str());
    }
}

void Logger::loge(const string &msg) {
    LOGE("JNI -> %s", msg.c_str());
}

void Logger::log_handler(JNIEnv *env, const string &msg) {
    jclass _clz = env->FindClass("cn/flyfun/gamesdk/base/utils/Logger");
    if (_clz == NULL) {
        loge("Logger impl clz is NULL !!!");
        return;
    }
    const char *method_name = "logHandler";
    const char *sig = "(Ljava/lang/String;)V";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jmsg = env->NewStringUTF(msg.c_str());
    env->CallStaticVoidMethod(_clz, jmethod_id, jmsg);
}