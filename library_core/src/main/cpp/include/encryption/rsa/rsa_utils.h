//
// Created by #Suyghur, on 2020/12/18.
//

#ifndef FLYFUNGAMESDK_RSA_UTILS_H
#define FLYFUNGAMESDK_RSA_UTILS_H

#include <string>
#include <jni.h>

using namespace std;

class RSA {
public:
    static string encrypt_by_public_key(JNIEnv *env, const string &raw);

    static string decrypt_by_public_key(JNIEnv *env, const string &enc);
};

#endif //FLYFUNGAMESDK_RSA_UTILS_H
