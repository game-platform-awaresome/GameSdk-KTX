//
// Created by #Suyghur, on 2020/12/18.
//

#include "include/constant.h"
#include "include/aes_utils.h"
#include "include/logger.h"
#include "include/jtools.h"


std::string AES::encrypt(JNIEnv *env, const std::string &key, const std::string &raw) {
    jclass _clz = env->FindClass(AES_CLZ_NAME);
    if (_clz == nullptr) {
        Logger::loge("aes impl clz is nullptr !!!");
        return "";
    }
    const char *method_name = "encrypt";
    const char *sig = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jkey = env->NewStringUTF(key.c_str());
    jstring jraw = env->NewStringUTF(raw.c_str());
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, jkey, jraw);
    return JTools::jstring2str(env, jresult);
}

std::string AES::decrypt(JNIEnv *env, const std::string &key, const std::string &enc) {
    jclass _clz = env->FindClass(AES_CLZ_NAME);
    if (_clz == nullptr) {
        Logger::loge("aes impl clz is nullptr !!!");
        return "";
    }
    const char *method_name = "decrypt";
    const char *sig = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jkey = env->NewStringUTF(key.c_str());
    jstring jraw = env->NewStringUTF(enc.c_str());
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, jkey, jraw);
    return JTools::jstring2str(env, jresult);
}