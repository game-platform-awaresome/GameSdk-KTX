//
// Created by #Suyghur, on 2020/12/18.
//

#include <include/encryption/aes/aes_utils.h>
#include <include/encryption/rsa/rsa_utils.h>
#include <include/encryption/url_utils.h>
#include <include/bean/common.h>
#include "jtools.h"
#include "logger.h"
#include "constant.h"

char *JTools::jbytearray2chars(JNIEnv *env, jbyteArray byte_array) {
    char *chars = NULL;
    jbyte *bytes = env->GetByteArrayElements(byte_array, JNI_FALSE);
    int len = env->GetArrayLength(byte_array);
    chars = new char[len + 1];
    memset(chars, 0, len + 1);
    memcpy(chars, bytes, len);
    chars[len] = 0;
    env->ReleaseByteArrayElements(byte_array, bytes, JNI_FALSE);
    return chars;
}

string JTools::jstring2str(JNIEnv *env, jstring jstr) {
    char *tmp_ptr = NULL;
    jclass _clz = env->FindClass("java/lang/String");
    jstring encode_model = env->NewStringUTF("UTF-8");
    char const *_sig = "(Ljava/lang/String;)[B";
    jmethodID jmethod_id = env->GetMethodID(_clz, "getBytes", _sig);
    jbyteArray byte_array = (jbyteArray) env->CallObjectMethod(jstr, jmethod_id, encode_model);
    jsize _len = env->GetArrayLength(byte_array);
    if (_len == 0) {
        free(tmp_ptr);
        return "";
    }

    jbyte *byte = env->GetByteArrayElements(byte_array, JNI_FALSE);
    if (_len > 0) {
        tmp_ptr = (char *) malloc(_len + 1);
        memcpy(tmp_ptr, byte, _len);
        tmp_ptr[_len] = 0;
    }
    env->ReleaseByteArrayElements(byte_array, byte, JNI_FALSE);
    string _result(tmp_ptr);
    free(tmp_ptr);
    return _result;
}

string generate_random_str(int len) {
    string _str = KEY_RANDOM_SOURCE_POOL;
    string _result;
    for (int i = 0; i < len; ++i) {
        int _num = rand() % _str.length();
        string _tmp(1, _str.at(_num));
        _result.append(_tmp);
    }
    return _result;
}

string JTools::n2j(JNIEnv *env, jobject context, int method_id) {
    jclass _clz = env->FindClass("cn/flyfun/gamesdk/core/utils/NTools");
    if (_clz == NULL) {
        Logger::loge("NTools impl clz is NULL !!!");
        return "";
    }

    const char *method_name = "n2j";
    const char *sig = "(Landroid/content/Context;I)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, context, method_id);
    return jstring2str(env, jresult);
}

string JTools::get_game_code(JNIEnv *env, jobject context) {
    jclass _clz = env->FindClass("cn/flyfun/gamesdk/base/utils/ParamsUtils");
    if (_clz == NULL) {
        Logger::loge("ParamsUtils impl clz is NULL !!!");
        return "";
    }

    const char *method_name = "getGameCode";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

string JTools::get_package_name(JNIEnv *env, jobject context) {
    jclass _clz = env->GetObjectClass(context);
    if (_clz == NULL) {
        Logger::loge("Context clz is NULL !!!");
        return "";
    }
    const char *method_name = "getPackageName";
    const char *sig = "()Ljava/lang/String;";
    jmethodID jmethod_id = env->GetMethodID(_clz, method_name, sig);
    jstring jresult = (jstring) env->CallObjectMethod(context, jmethod_id);
    return jstring2str(env, jresult);
}

string JTools::get_imei(JNIEnv *env, jobject context) {
    jclass _clz = env->FindClass("cn/flyfun/support/device/DeviceInfoUtils");
    if (_clz == NULL) {
        Logger::loge("DeviceInfoUtils impl clz is NULL !!!");
        return "";
    }

    const char *method_name = "getImei";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

string JTools::get_local_language(JNIEnv *env, jobject context) {
    jclass _clz = env->FindClass("cn/flyfun/support/LocaleUtils");
    if (_clz == NULL) {
        Logger::loge("LocaleUtils impl clz is NULL !!!");
        return "";
    }

    const char *method_name = "getLocaleCountry";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

string JTools::get_network(JNIEnv *env, jobject context) {
    jclass _clz = env->FindClass("cn/flyfun/support/device/DeviceInfoUtils");
    if (_clz == NULL) {
        Logger::loge("DeviceInfoUtils impl clz is NULL !!!");
        return "";
    }

    const char *method_name = "getNet";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

string JTools::get_mac(JNIEnv *env, jobject context) {
    jclass _clz = env->FindClass("cn/flyfun/support/device/DeviceInfoUtils");
    if (_clz == NULL) {
        Logger::loge("DeviceInfoUtils impl clz is NULL !!!");
        return "";
    }

    const char *method_name = "getMac";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

string JTools::get_android_id(JNIEnv *env, jobject context) {
    jclass _clz = env->FindClass("cn/flyfun/support/device/DeviceInfoUtils");
    if (_clz == NULL) {
        Logger::loge("DeviceInfoUtils impl clz is NULL !!!");
        return "";
    }

    const char *method_name = "getAndroidDeviceId";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

string JTools::get_server_version(JNIEnv *env, jobject context) {
    return n2j(env, context, N2J_METHOD_GET_SERVER_VERSION);
}

string JTools::get_client_version(JNIEnv *env, jobject context) {
    return n2j(env, context, N2J_METHOD_GET_CLIENT_VERSION);
}

string JTools::get_version_code(JNIEnv *env, jobject context) {
    jclass _clz = env->FindClass("cn/flyfun/support/AppUtils");
    if (_clz == NULL) {
        Logger::loge("DeviceInfoUtils impl clz is NULL !!!");
        return "0";
    }

    const char *method_name = "getVersionCode";
    const char *sig = "(Landroid/content/Context;)I";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jint jresult = (jint) env->CallStaticIntMethod(_clz, jmethod_id, context);
    char *result = (char *) malloc(sizeof(char) * 16);
    sprintf(result, "%d", jresult);
    return result;
}

string JTools::get_version_name(JNIEnv *env, jobject context) {
    jclass _clz = env->FindClass("cn/flyfun/support/AppUtils");
    if (_clz == NULL) {
        Logger::loge("DeviceInfoUtils impl clz is NULL !!!");
        return "";
    }

    const char *method_name = "getVersionName";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

string JTools::get_os_version() {
    char version[128] = "0";
    __system_property_get("ro.build.version.release", version);
    return string(version);
}

string JTools::is_simulator(JNIEnv *env, jobject context) {

    jclass _clz = env->FindClass("cn/flyfun/support/device/DeviceInfoUtils");
    if (_clz == NULL) {
        Logger::loge("DeviceInfoUtils impl clz is NULL !!!");
        return "0";
    }

    const char *method_name = "isEmulator";
    const char *sig = "(Landroid/content/Context;)Z";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jboolean jresult = (jboolean) env->CallStaticBooleanMethod(_clz, jmethod_id, context);
    if (jresult == JNI_TRUE) {
        return "1";
    } else {
        return "0";
    }
}

string JTools::get_mobile_brand() {
    char brand[128] = "0";
    __system_property_get("ro.product.brand", brand);
    return string(brand);
}

string JTools::get_model() {
    char model[128] = "0";
    __system_property_get("ro.product.model", model);
    return string(model);
}

string JTools::get_manufacturer() {
    char manufacturer[128] = "0";
    __system_property_get("ro.product.manufacturer", manufacturer);
    return string(manufacturer);
}

string JTools::encrypt_request(JNIEnv *env, const string &data) {

    string aes_key = generate_random_str(16);

    string _p = url_encode(AES::encrypt(env, aes_key, data));
    string _ts = url_encode(RSA::encrypt_by_public_key(env, aes_key));

    Json::Value _root;
    Json::StreamWriterBuilder _builder;
    ostringstream _oss;
    //无格式输出
    _builder.settings_["indentation"] = "";

    _root["p"] = _p;
    _root["ts"] = _ts;

    unique_ptr<Json::StreamWriter> json_writer(_builder.newStreamWriter());
    json_writer->write(_root, &_oss);
    return _oss.str();
}

string JTools::decrypt_response(JNIEnv *env, const string &p, const string &ts) {
    string aes_key = RSA::decrypt_by_public_key(env, url_decode(ts));
    return AES::decrypt(env, aes_key, url_decode(p));
}