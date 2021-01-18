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
invokeFuseJob(JNIEnv *env, jclass clazz, jobject context, jstring url, jstring data) {
    std::string _data = ParamsMap::addCommon(env, context, data);
    std::string _result = JTools::encryptRequest(env, _data);
    std::string _url = JTools::jstring2str(env, url);

    Logger::logHandler(env, "请求地址 : " + _url + "\n");
    Logger::logHandler(env, "请求参数 : " + _data + "\n");
    Logger::logd(env, "请求地址 : " + _url);
    Logger::logd(env, "请求参数 : " + _data);

    return env->NewStringUTF(_result.c_str());
}

static jstring parseFuseJob(JNIEnv *env, jclass clazz, jobject context, jstring data) {
    std::string _response = JTools::jstring2str(env, data);
    if (_response == "{}") {
        Logger::logHandler(env, "返回内容 : {} \n");
        Logger::logd(env, "返回内容 : {}");
        return env->NewStringUTF("");
    }

    Json::CharReaderBuilder _builder;
    Json::CharReader *reader_ptr(_builder.newCharReader());
    JSONCPP_STRING _errs;
    Json::Value _root;
    std::string _result;
    if (reader_ptr->parse(_response.c_str(), _response.c_str() + _response.length(), &_root,
                          &_errs)) {
        std::string _p = _root["p"].asString();
        std::string _ts = _root["ts"].asString();
        _result = JTools::decryptResponse(env, _p, _ts);
    }

    if (_result.empty()) {
        std::string msg = "parse fuse response data is empty";
        Logger::loge(msg);
        return env->NewStringUTF(msg.c_str());
    }

    Logger::logHandler(env, "返回内容 : " + _result + "\n");
    Logger::logd(env, "返回内容 : " + _result);
    return env->NewStringUTF(_result.c_str());
}

static void putParam(JNIEnv *env, jclass clazz, jstring key, jstring value) {
    std::string _key = JTools::jstring2str(env, key);
    std::string _value = JTools::jstring2str(env, value);

    ParamsMap::put(_key, _value);
}

static jstring getParam(JNIEnv *env, jclass clazz, jstring key) {
    std::string _key = JTools::jstring2str(env, key);
    std::string _value = ParamsMap::get(_key);
    return env->NewStringUTF(_value.c_str());
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
    jclass _clz = env->FindClass("cn/flyfun/gamesdk/core/utils/NTools");
    if (_clz == nullptr) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(_clz, gMethods, NELEM(gMethods)) < 0) {
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
