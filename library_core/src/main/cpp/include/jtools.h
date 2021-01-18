//
// Created by #Suyghur, on 2020/12/18.
//

#ifndef FLYFUNGAMESDK_KTX_JTOOLS_H
#define FLYFUNGAMESDK_KTX_JTOOLS_H

#include <string>
#include <jni.h>
#include "json.h"
#include <sys/system_properties.h>

#define KEY_RANDOM_SOURCE_POOL "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"


class JTools {
public:

    static char *jbytearray2chars(JNIEnv *env, jbyteArray byte_array);

    static std::string jstring2str(JNIEnv *env, jstring jstr);


    static std::string getGameCode(JNIEnv *env, jobject context);

    static std::string getPackageName(JNIEnv *env, jobject context);

    static std::string getImei(JNIEnv *env, jobject context);

    static std::string getLocalLanguage(JNIEnv *env, jobject context);

    static std::string getNetwork(JNIEnv *env, jobject context);

    static std::string getMac(JNIEnv *env, jobject context);

    static std::string getAndroidId(JNIEnv *env, jobject context);

    static std::string getServerVersion(JNIEnv *env, jobject context);

    static std::string getClientVersion(JNIEnv *env, jobject context);

    static std::string getVersionCode(JNIEnv *env, jobject context);

    static std::string getVersionName(JNIEnv *env, jobject context);

    static std::string getOsVersion();

    static std::string isSimulator(JNIEnv *env, jobject context);

    static std::string getMobileBrand();

    static std::string getModel();

    static std::string getManufacturer();

    static std::string encryptRequest(JNIEnv *env, const std::string &data);

    static std::string decryptResponse(JNIEnv *env, const std::string &p, const std::string &ts);
};

#endif //FLYFUNGAMESDK_KTX_JTOOLS_H
