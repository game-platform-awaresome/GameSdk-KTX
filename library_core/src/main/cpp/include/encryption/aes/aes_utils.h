//
// Created by #Suyghur, on 2020/12/18.
//

#ifndef FLYFUNGAMESDK_AES_UTILS_H
#define FLYFUNGAMESDK_AES_UTILS_H

#include <string>
#include <jni.h>

using namespace std;

class AES {

public:
    static string encrypt(JNIEnv *env, const string & key, const string & raw);

    static string decrypt(JNIEnv *env, const string &key, const string &enc);
};


#endif //FLYFUNGAMESDK_AES_UTILS_H
