//
// Created by #Suyghur, on 2020/12/18.
//
#include <jni.h>
#include <string>
#include <include/json/json.h>
#include <include/params_map.h>
#include "include/logger.h"
#include "include/jtools.h"

using namespace std;

#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))


extern "C"
JNIEXPORT void JNICALL
init(JNIEnv *env, jclass clazz, jobject context) {
    Logger::init(env, context);
}

extern "C"
JNIEXPORT jstring JNICALL
invoke_fuse_job(JNIEnv *env, jclass clazz, jobject context, jstring url, jstring data) {
    string _data = ParamsMap::addCommon(env, context, data);
    string _result = JTools::encrypt_request(env, _data);
    string _url = JTools::jstring2str(env, url);

    Logger::log_handler(env, "请求地址 : " + _url + "\n");
    Logger::log_handler(env, "请求参数 : " + _data + "\n");
    Logger::logd(env, "请求地址 : " + _url);
    Logger::logd(env, "请求参数 : " + _data);

    return env->NewStringUTF(_result.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
parse_fuse_job(JNIEnv *env, jclass clazz, jobject context, jstring data) {

    string _response = JTools::jstring2str(env, data);
    if (_response == "{}") {
        Logger::log_handler(env, "返回内容 : {} \n");
        Logger::logd(env,"返回内容 : {}");
        return env->NewStringUTF("");
    }

    Json::CharReaderBuilder _builder;
    Json::CharReader *reader_ptr(_builder.newCharReader());
    JSONCPP_STRING _errs;
    Json::Value _root;
    string _result;
    if (reader_ptr->parse(_response.c_str(), _response.c_str() + _response.length(), &_root,
                          &_errs)) {
        string _p = _root["p"].asString();
        string _ts = _root["ts"].asString();
        _result = JTools::decrypt_response(env, _p, _ts);
    }

    if (_result.empty()) {
        string msg = "parse fuse response data is empty";
        Logger::loge(msg);
        return env->NewStringUTF(msg.c_str());
    }

    Logger::log_handler(env, "返回内容 : " + _result + "\n");
    Logger::logd(env, "返回内容 : " + _result);
    return env->NewStringUTF(_result.c_str());
}

extern "C"
JNIEXPORT void JNICALL
put_param(JNIEnv *env, jclass clazz, jstring key, jstring value) {

    string _key = JTools::jstring2str(env, key);
    string _value = JTools::jstring2str(env, value);

    ParamsMap::put(_key, _value);
}

static JNINativeMethod method_table[] = {
        {"init",          "(Landroid/content/Context;)V",                                                      (void *) init},
        {"putParam",      "(Ljava/lang/String;Ljava/lang/String;)V",                                           (void *) put_param},
        {"invokeFuseJob", "(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) invoke_fuse_job},
        {"parseFuseJob",  "(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/String;",                   (void *) parse_fuse_job}
};

static int register_methods(JNIEnv *env) {
    jclass _clz = env->FindClass("cn/flyfun/gamesdk/core/utils/NTools");
    if (_clz == nullptr) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(_clz, method_table, NELEM(method_table)) < 0) {
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
    if (!register_methods(env)) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}
