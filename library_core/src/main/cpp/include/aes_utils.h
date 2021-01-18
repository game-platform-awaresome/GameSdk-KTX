//
// Created by #Suyghur, on 2020/12/18.
//

#ifndef FLYFUNGAMESDK_KTX_AES_UTILS_H
#define FLYFUNGAMESDK_KTX_AES_UTILS_H

#include <string>
#include <jni.h>

class AES {

public:
    static std::string encrypt(JNIEnv *env, const std::string &key, const std::string &raw);

    static std::string decrypt(JNIEnv *env, const std::string &key, const std::string &enc);
};


#endif //FLYFUNGAMESDK_KTX_AES_UTILS_H
