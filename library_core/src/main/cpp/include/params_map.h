//
// Created by #Suyghur, on 2020/12/23.
//

#ifndef FLYFUNGAMESDK_KTX_PARAMS_MAP_H
#define FLYFUNGAMESDK_KTX_PARAMS_MAP_H

#include <string>
#include <jni.h>
#include "json.h"
#include "logger.h"

class ParamsMap {
public:

    static bool init(JNIEnv *env, jobject context);

    static std::string get(const std::string &key);

    static void put(const std::string &key, const std::string &value);

    static Json::Value getCommon(JNIEnv *env, jobject context);

    static std::string addCommon(JNIEnv *env, jobject context, jstring param);

};

#endif //FLYFUNGAMESDK_KTX_PARAMS_MAP_H
