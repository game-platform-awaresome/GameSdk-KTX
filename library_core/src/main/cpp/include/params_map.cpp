//
// Created by #Suyghur, on 2020/12/23.
//

#include <include/bean/common.h>
#include "params_map.h"
#include "constant.h"
#include "jtools.h"

bool initState = false;

bool ParamsMap::init(JNIEnv *env, jobject context) {
    if (initState) {
        return initState;
    }
    Common::get_instance().paramsMap["game_code"] = JTools::get_game_code(env, context);
    Common::get_instance().paramsMap["package_name"] = JTools::get_package_name(env, context);
    Common::get_instance().paramsMap["server_version"] = JTools::get_server_version(env, context);
    Common::get_instance().paramsMap["client_version"] = JTools::get_client_version(env, context);
    Common::get_instance().paramsMap["game_version_code"] = JTools::get_version_code(env, context);
    Common::get_instance().paramsMap["game_version_name"] = JTools::get_version_name(env, context);
    Common::get_instance().paramsMap["local_language"] = JTools::get_local_language(env, context);
    Common::get_instance().paramsMap["android_id"] = JTools::get_android_id(env, context);
    Common::get_instance().paramsMap["simulator"] = JTools::is_simulator(env, context);
    Common::get_instance().paramsMap["imei"] = JTools::get_imei(env, context);
    Common::get_instance().paramsMap["mac"] = JTools::get_mac(env, context);
    Common::get_instance().paramsMap["network"] = JTools::get_network(env, context);
    Common::get_instance().paramsMap["os_version"] = JTools::get_os_version();
    Common::get_instance().paramsMap["model"] = JTools::get_model();
    Common::get_instance().paramsMap["mfrs"] = JTools::get_manufacturer();
    Common::get_instance().paramsMap["mobile_brand"] = JTools::get_mobile_brand();

    initState = true;
    return initState;
}

string ParamsMap::get(const string &key) {
    return Common::get_instance().paramsMap[key];
}

void ParamsMap::put(const string &key, const string &value) {
    Common::get_instance().paramsMap[key] = value;
}

Json::Value ParamsMap::getCommon(JNIEnv *env, jobject context) {

    if (!initState) {
        init(env, context);
    }

    Json::Value root, common, biz, vers, device;

    biz["game_code"] = Common::get_instance().paramsMap["game_code"];
    biz["package_name"] = Common::get_instance().paramsMap["package_name"];
    common["biz"] = biz;

    vers["server_version"] = Common::get_instance().paramsMap["server_version"];
    vers["client_version"] = Common::get_instance().paramsMap["client_version"];
    vers["game_version_code"] = Common::get_instance().paramsMap["game_version_code"];
    vers["game_version_name"] = Common::get_instance().paramsMap["game_version_name"];
    common["vers"] = vers;

    device["local_language"] = Common::get_instance().paramsMap["local_language"];
    device["screen"] = Common::get_instance().paramsMap["screen"];
    device["simulator"] = Common::get_instance().paramsMap["simulator"];
    device["imei"] = Common::get_instance().paramsMap["imei"];
    device["device_id"] = Common::get_instance().paramsMap["device_id"];
    device["mac"] = Common::get_instance().paramsMap["mac"];
    device["adid"] = Common::get_instance().paramsMap["adid"];
    device["android_id"] = Common::get_instance().paramsMap["android_id"];
    device["idfa"] = "";
    device["idfv"] = "";
    device["network"] = Common::get_instance().paramsMap["network"];
    device["os"] = "android";
    device["os_version"] = Common::get_instance().paramsMap["os_version"];
    device["model"] = Common::get_instance().paramsMap["model"];
    device["mfrs"] = Common::get_instance().paramsMap["mfrs"];
    device["mobile_brand"] = Common::get_instance().paramsMap["mobile_brand"];
    common["device"] = device;

    common["ext"] = "";

    return common;
}

string ParamsMap::addCommon(JNIEnv *env, jobject context, jstring param) {
    if (param == nullptr) {
        Json::Value _root;
        Json::StreamWriterBuilder _builder;
        string _data;
        ostringstream _oss;
        //无格式输出
        _builder.settings_["indentation"] = "";

        _root["common"] = getCommon(env, context);
        unique_ptr<Json::StreamWriter> json_writer(_builder.newStreamWriter());
        json_writer->write(_root, &_oss);
        _data = _oss.str();
        return _data;
    }

    string json_param = JTools::jstring2str(env, param);
    Json::StreamWriterBuilder writer_builder;
    Json::CharReaderBuilder reader_builder;
    Json::CharReader *reader_ptr(reader_builder.newCharReader());
    JSONCPP_STRING _errs;
    Json::Value _root;
    string _data;
    ostringstream _oss;

    if (reader_ptr->parse(json_param.c_str(), json_param.c_str() + json_param.length(), &_root,
                          &_errs)) {
        _root["common"] = getCommon(env, context);
    }

    writer_builder.settings_["indentation"] = "";
    unique_ptr<Json::StreamWriter> json_writer(writer_builder.newStreamWriter());
    json_writer->write(_root, &_oss);
    _data = _oss.str();
    return _data;
}









