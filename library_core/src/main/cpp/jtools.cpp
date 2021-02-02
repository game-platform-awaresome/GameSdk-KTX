//
// Created by #Suyghur, on 2020/12/18.
//

#include "include/aes_utils.h"
#include "include/rsa_utils.h"
#include "include/url_utils.h"
#include "include/common.h"
#include "include/jtools.h"
#include "include/logger.h"
#include "include/constant.h"

char *JTools::jbytearray2chars(JNIEnv *env, jbyteArray byte_array) {
    char *chars = nullptr;
    jbyte *bytes = env->GetByteArrayElements(byte_array, JNI_FALSE);
    int len = env->GetArrayLength(byte_array);
    chars = new char[len + 1];
    memset(chars, 0, len + 1);
    memcpy(chars, bytes, len);
    chars[len] = 0;
    env->ReleaseByteArrayElements(byte_array, bytes, JNI_FALSE);
    return chars;
}

std::string JTools::jstring2str(JNIEnv *env, jstring jstr) {
    char *tmp_ptr = nullptr;
    jclass clz = env->FindClass("java/lang/String");
    jstring encode_model = env->NewStringUTF("UTF-8");
    char const *sig = "(Ljava/lang/String;)[B";
    jmethodID jmethod_id = env->GetMethodID(clz, "getBytes", sig);
    jbyteArray byte_array = (jbyteArray) env->CallObjectMethod(jstr, jmethod_id, encode_model);
    jsize len = env->GetArrayLength(byte_array);
    if (len == 0) {
        free(tmp_ptr);
        return "";
    }

    jbyte *byte = env->GetByteArrayElements(byte_array, JNI_FALSE);
    if (len > 0) {
        tmp_ptr = (char *) malloc(len + 1);
        memcpy(tmp_ptr, byte, len);
        tmp_ptr[len] = 0;
    }
    env->ReleaseByteArrayElements(byte_array, byte, JNI_FALSE);
    std::string result(tmp_ptr);
    free(tmp_ptr);
    return result;
}

std::string generateRandomStr(int len) {
    std::string str = KEY_RANDOM_SOURCE_POOL;
    std::string result;
    for (int i = 0; i < len; ++i) {
        int num = rand() % str.length();
        std::string tmp(1, str.at(num));
        result.append(tmp);
    }
    return result;
}

std::string JTools::getGameCode(JNIEnv *env, jobject context) {
    jclass clz = env->FindClass("cn/flyfun/gamesdk/base/utils/ParamsUtils");
    if (clz == nullptr) {
        Logger::loge("ParamsUtils impl clz is nullptr !!!");
        return "";
    }

    const char *method_name = "getGameCode";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

std::string JTools::getPackageName(JNIEnv *env, jobject context) {
    jclass clz = env->GetObjectClass(context);
    if (clz == nullptr) {
        Logger::loge("Context clz is nullptr !!!");
        return "";
    }
    const char *method_name = "getPackageName";
    const char *sig = "()Ljava/lang/String;";
    jmethodID jmethod_id = env->GetMethodID(clz, method_name, sig);
    jstring jresult = (jstring) env->CallObjectMethod(context, jmethod_id);
    return jstring2str(env, jresult);
}

std::string JTools::getImei(JNIEnv *env, jobject context) {
    jclass clz = env->FindClass("cn/flyfun/support/device/DeviceInfoUtils");
    if (clz == nullptr) {
        Logger::loge("DeviceInfoUtils impl clz is nullptr !!!");
        return "";
    }

    const char *method_name = "getImei";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

std::string JTools::getLocalLanguage(JNIEnv *env, jobject context) {
    jclass clz = env->FindClass("cn/flyfun/support/LocaleUtils");
    if (clz == nullptr) {
        Logger::loge("LocaleUtils impl clz is nullptr !!!");
        return "";
    }

    const char *method_name = "getLocaleCountry";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

std::string JTools::getNetwork(JNIEnv *env, jobject context) {
    jclass clz = env->FindClass("cn/flyfun/support/device/DeviceInfoUtils");
    if (clz == nullptr) {
        Logger::loge("DeviceInfoUtils impl clz is nullptr !!!");
        return "";
    }

    const char *method_name = "getNet";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

std::string JTools::getMac(JNIEnv *env, jobject context) {
    jclass clz = env->FindClass("cn/flyfun/support/device/DeviceInfoUtils");
    if (clz == nullptr) {
        Logger::loge("DeviceInfoUtils impl clz is nullptr !!!");
        return "";
    }

    const char *method_name = "getMac";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

std::string JTools::getAndroidId(JNIEnv *env, jobject context) {
    jclass clz = env->FindClass("cn/flyfun/support/device/DeviceInfoUtils");
    if (clz == nullptr) {
        Logger::loge("DeviceInfoUtils impl clz is nullptr !!!");
        return "";
    }

    const char *method_name = "getAndroidDeviceId";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

std::string JTools::getServerVersion(JNIEnv *env, jobject context) {
    jclass clz = env->FindClass("cn/flyfun/gamesdk/Version");
    if (clz == nullptr) {
        Logger::loge("Version impl clz is nullptr !!! ");
        return "";
    }

    const char *field_name = "SERVER_VERSION_NAME";
    const char *sig = "Ljava/lang/String;";
    jfieldID jfield_id = env->GetStaticFieldID(clz, field_name, sig);
    jstring jresult = (jstring) env->GetStaticObjectField(clz, jfield_id);
    return jstring2str(env, jresult);
}

std::string JTools::getClientVersion(JNIEnv *env, jobject context) {
    jclass clz = env->FindClass("cn/flyfun/gamesdk/Version");
    if (clz == nullptr) {
        Logger::loge("Version impl clz is nullptr !!! ");
        return "";
    }

    const char *field_name = "CORE_VERSION_NAME";
    const char *sig = "Ljava/lang/String;";
    jfieldID jfield_id = env->GetStaticFieldID(clz, field_name, sig);
    jstring jresult = (jstring) env->GetStaticObjectField(clz, jfield_id);
    return jstring2str(env, jresult);
}

std::string JTools::getVersionCode(JNIEnv *env, jobject context) {
    jclass clz = env->FindClass("cn/flyfun/support/AppUtils");
    if (clz == nullptr) {
        Logger::loge("DeviceInfoUtils impl clz is nullptr !!!");
        return "0";
    }

    const char *method_name = "getVersionCode";
    const char *sig = "(Landroid/content/Context;)I";
    jmethodID jmethod_id = env->GetStaticMethodID(clz, method_name, sig);
    jint jresult = (jint) env->CallStaticIntMethod(clz, jmethod_id, context);
    char *result = (char *) malloc(sizeof(char) * 16);
    sprintf(result, "%d", jresult);
    return result;
}

std::string JTools::getVersionName(JNIEnv *env, jobject context) {
    jclass _clz = env->FindClass("cn/flyfun/support/AppUtils");
    if (_clz == nullptr) {
        Logger::loge("DeviceInfoUtils impl clz is nullptr !!!");
        return "";
    }

    const char *method_name = "getVersionName";
    const char *sig = "(Landroid/content/Context;)Ljava/lang/String;";
    jmethodID jmethod_id = env->GetStaticMethodID(_clz, method_name, sig);
    jstring jresult = (jstring) env->CallStaticObjectMethod(_clz, jmethod_id, context);
    return jstring2str(env, jresult);
}

std::string JTools::getOsVersion() {
    char version[128] = "0";
    __system_property_get("ro.build.version.release", version);
    return std::string(version);
}

std::string JTools::isSimulator(JNIEnv *env, jobject context) {
    jclass clz = env->FindClass("cn/flyfun/support/device/DeviceInfoUtils");
    if (clz == nullptr) {
        Logger::loge("DeviceInfoUtils impl clz is nullptr !!!");
        return "0";
    }

    const char *method_name = "isEmulator";
    const char *sig = "(Landroid/content/Context;)Z";
    jmethodID jmethod_id = env->GetStaticMethodID(clz, method_name, sig);
    jboolean jresult = (jboolean) env->CallStaticBooleanMethod(clz, jmethod_id, context);
    if (jresult == JNI_TRUE) {
        return "1";
    } else {
        return "0";
    }
}

std::string JTools::getMobileBrand() {
    char brand[128] = "0";
    __system_property_get("ro.product.brand", brand);
    return std::string(brand);
}

std::string JTools::getModel() {
    char model[128] = "0";
    __system_property_get("ro.product.model", model);
    return std::string(model);
}

std::string JTools::getManufacturer() {
    char manufacturer[128] = "0";
    __system_property_get("ro.product.manufacturer", manufacturer);
    return std::string(manufacturer);
}

std::string JTools::encryptRequest(JNIEnv *env, const std::string &data) {
    std::string aes_key = generateRandomStr(16);

    std::string p = urlEncode(AES::encrypt(env, aes_key, data));
    std::string ts = urlEncode(RSA::encryptByPublicKey(env, aes_key));

    Json::Value root;
    Json::StreamWriterBuilder builder;
    std::ostringstream oss;
    //无格式输出
    builder.settings_["indentation"] = "";

    root["p"] = p;
    root["ts"] = ts;

    std::unique_ptr<Json::StreamWriter> json_writer(builder.newStreamWriter());
    json_writer->write(root, &oss);
    return oss.str();
}

std::string JTools::decryptResponse(JNIEnv *env, const std::string &p, const std::string &ts) {
    std::string aes_key = RSA::decryptByPublicKey(env, urlDecode(ts));
    return AES::decrypt(env, aes_key, urlDecode(p));
}