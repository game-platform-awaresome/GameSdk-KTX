//
// Created by #Suyghur, on 2020/12/18.
//

#ifndef FLYFUNGAMESDK_JTOOLS_H
#define FLYFUNGAMESDK_JTOOLS_H

#include <string>
#include <jni.h>
#include <include/json/json.h>
#include <sys/system_properties.h>

using namespace std;

#define KEY_RANDOM_SOURCE_POOL "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"


class JTools {
public:

    static char *jbytearray2chars(JNIEnv *env, jbyteArray byte_array);

    static string jstring2str(JNIEnv *env, jstring jstr);

    static string n2j(JNIEnv *env, jobject context, int method_id);

    static string get_game_code(JNIEnv *env, jobject context);

    static string get_package_name(JNIEnv *env, jobject context);

    static string get_imei(JNIEnv *env, jobject context);

    static string get_local_language(JNIEnv *env, jobject context);

    static string get_network(JNIEnv *env, jobject context);

    static string get_mac(JNIEnv *env, jobject context);

    static string get_android_id(JNIEnv *env, jobject context);

    static string get_server_version(JNIEnv *env, jobject context);

    static string get_client_version(JNIEnv *env, jobject context);

    static string get_version_code(JNIEnv *env, jobject context);

    static string get_version_name(JNIEnv *env, jobject context);

    static string get_os_version();

    static string is_simulator(JNIEnv *env, jobject context);

    static string get_mobile_brand();

    static string get_model();

    static string get_manufacturer();

    static string encrypt_request(JNIEnv *env, const string &data);

    static string decrypt_response(JNIEnv *env, const string &p, const string &ts);
};

#endif //FLYFUNGAMESDK_JTOOLS_H
