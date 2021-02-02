//
// Created by #Suyghur, on 2020/12/18.
//
#include <jni.h>
#include <string>
#include "include/json.h"
#include "include/params_map.h"
#include "include/logger.h"
#include "include/jtools.h"

#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))

static void init(JNIEnv *env, jclass clazz, jobject context) {
    Logger::init(env, context);
}

static jstring
invokeFuseJob(JNIEnv *env, jclass clazz, jobject context, jstring _url, jstring _data) {
    std::string data = ParamsMap::addCommon(env, context, _data);
    std::string result = JTools::encryptRequest(env, data);
    std::string url = JTools::jstring2str(env, _url);

    Logger::logHandler(env, "请求地址 : " + url + "\n");
    Logger::logHandler(env, "请求参数 : " + data + "\n");
    Logger::logd(env, "请求地址 : " + url);
    Logger::logd(env, "请求参数 : " + data);

    return env->NewStringUTF(result.c_str());
}

static jstring parseFuseJob(JNIEnv *env, jclass clazz, jobject context, jstring data) {
    std::string response = JTools::jstring2str(env, data);
    if (response == "{}") {
        Logger::logHandler(env, "返回内容 : {} \n");
        Logger::logd(env, "返回内容 : {}");
        return env->NewStringUTF("");
    }

    Json::CharReaderBuilder builder;
    Json::CharReader *reader_ptr(builder.newCharReader());
    JSONCPP_STRING errs;
    Json::Value root;
    std::string result;
    if (reader_ptr->parse(response.c_str(), response.c_str() + response.length(), &root, &errs)) {
        std::string p = root["p"].asString();
        std::string ts = root["ts"].asString();
        result = JTools::decryptResponse(env, p, ts);
    }

    if (result.empty()) {
        std::string msg = "parse fuse response data is empty";
        Logger::loge(msg);
        return env->NewStringUTF(msg.c_str());
    }

    Logger::logHandler(env, "返回内容 : " + result + "\n");
    Logger::logd(env, "返回内容 : " + result);
    return env->NewStringUTF(result.c_str());
}

static void putParam(JNIEnv *env, jclass clazz, jstring _key, jstring _value) {
    std::string key = JTools::jstring2str(env, _key);
    std::string value = JTools::jstring2str(env, _value);

    ParamsMap::put(key, value);
}

static jstring getParam(JNIEnv *env, jclass clazz, jstring _key) {
    std::string key = JTools::jstring2str(env, _key);
    std::string value = ParamsMap::get(key);
    return env->NewStringUTF(value.c_str());
}

static JNINativeMethod gMethods[] = {
        {
                "init",
                "(Landroid/content/Context;)V",
                (void *) init
        },
        {
                "putParam",
                "(Ljava/lang/String;Ljava/lang/String;)V",
                (void *) putParam
        },
        {
                "getParam",
                "(Ljava/lang/String;)Ljava/lang/String;",
                (void *) getParam
        },
        {
                "invokeFuseJob",
                "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
                (void *) invokeFuseJob
        },
        {
                "parseFuseJob",
                "(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;",
                (void *) parseFuseJob
        }
};

static int registerMethods(JNIEnv *env) {
    jclass clz = env->FindClass("cn/flyfun/gamesdk/core/utils/NTools");
    if (clz == nullptr) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clz, gMethods, NELEM(gMethods)) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    assert(env != nullptr);

    //注册native方法
    if (!registerMethods(env)) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}
