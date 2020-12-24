//
// Created by #Suyghur, on 2020/12/23.
//

#ifndef FLYFUNGAMESDK_PARAMS_MAP_H
#define FLYFUNGAMESDK_PARAMS_MAP_H

#include <string>
#include <jni.h>
#include <include/json/json.h>
#include "logger.h"

using namespace std;

class ParamsMap {
public:

    static bool init(JNIEnv *env, jobject context);

    static string get(const string &key);

    static void put(const string &key, const string &value);

    static Json::Value getCommon(JNIEnv *env, jobject context);

    static string addCommon(JNIEnv *env, jobject context, jstring param);

};

#endif //FLYFUNGAMESDK_PARAMS_MAP_H
