//
// Created by #Suyghur, on 2020/12/18.
//

#include "include/logger.h"

bool Logger::is_debug = false;

void Logger::init(JNIEnv *env, jobject context) {
    jclass clz = env->FindClass("cn/flyfun/support/jarvis/OwnDebugUtils");
    if (clz == nullptr) {
        loge("OwnDebugUtils impl clz is nullptr !!!");
        return;
    }

    const char *method_name = "isOwnDebug";
    const char *sig = "(Landroid/content/Context;)Z";
    jmethodID jmethod_id = env->GetStaticMethodID(clz, method_name, sig);
    jboolean jresult = env->CallStaticBooleanMethod(clz, jmethod_id, context);
    is_debug = jresult == JNI_TRUE;
}


void Logger::logd(const std::string &msg) {
    if (is_debug) {
        LOGD("JNI -> %s", msg.c_str());
    }
}

void Logger::logd(JNIEnv *env, const std::string &msg) {
    if (is_debug) {
        jclass clz = env->FindClass("cn/flyfun/gamesdk/base/utils/Logger");
        if (clz == nullptr) {
            loge("Logger impl clz is NULL !!!");
            return;
        }
        const char *method_name = "d";
        const char *sig = "(Ljava/lang/String;Ljava/lang/Object;)V";
        jmethodID jmethod_id = env->GetStaticMethodID(clz, method_name, sig);
        jstring jtag = env->NewStringUTF(TAG);
        jstring jmsg = env->NewStringUTF(msg.c_str());
        env->CallStaticVoidMethod(clz, jmethod_id, jtag, jmsg);
    }
}

void Logger::loge(const std::string &msg) {
    LOGE("JNI -> %s", msg.c_str());
}

void Logger::logHandler(JNIEnv *env, const std::string &msg) {
    if (is_debug) {
        jclass clz = env->FindClass("cn/flyfun/gamesdk/base/utils/Logger");
        if (clz == nullptr) {
            loge("Logger impl clz is NULL !!!");
            return;
        }
        const char *method_name = "logHandler";
        const char *sig = "(Ljava/lang/String;)V";
        jmethodID jmethod_id = env->GetStaticMethodID(clz, method_name, sig);
        jstring jmsg = env->NewStringUTF(msg.c_str());
        env->CallStaticVoidMethod(clz, jmethod_id, jmsg);
    }
}


