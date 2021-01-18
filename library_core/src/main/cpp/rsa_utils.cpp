//
// Created by #Suyghur, on 2020/12/18.
//

#include "include/constant.h"
#include "include/rsa_utils.h"
#include "include/logger.h"
#include "include/jtools.h"

std::string RSA::encryptByPublicKey(JNIEnv *env, const std::string &raw) {
    jclass _clz = env->FindClass(RSA_CLZ_NAME);
    if (_clz == nullptr) {
        Logger::loge("rsa impl clz is nullptr !!!");
        return "";
    }

    const char *method_name = "encryptByPublicKey";
    const char *sig = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jkey = env->NewStringUTF(RSA_PUBLIC_1024_X509_PEM);
    jstring jraw = env->NewStringUTF(raw.c_str());
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, jkey, jraw);
    return JTools::jstring2str(env, jresult);
}


std::string RSA::decryptByPublicKey(JNIEnv *env, const std::string & enc) {
    jclass _clz = env->FindClass(RSA_CLZ_NAME);
    if (_clz == nullptr) {
        Logger::loge("rsa impl clz is nullptr !!!");
        return "";
    }

    const char *method_name = "decryptByPublicKey";
    const char *sig = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jkey = env->NewStringUTF(RSA_PUBLIC_1024_X509_PEM);
    jstring jenc = env->NewStringUTF(enc.c_str());
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, jkey, jenc);
    return JTools::jstring2str(env, jresult);
}