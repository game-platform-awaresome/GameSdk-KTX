//
// Created by #Suyghur, on 2020/12/18.
//

#ifndef FLYFUNGAMESDK_RSA_UTILS_H
#define FLYFUNGAMESDK_RSA_UTILS_H

#include <string>
#include <jni.h>

class RSA {
public:
    static std::string encryptByPublicKey(JNIEnv *env, const std::string &raw);

    static std::string decryptByPublicKey(JNIEnv *env, const std::string &enc);
};

#endif //FLYFUNGAMESDK_RSA_UTILS_H
