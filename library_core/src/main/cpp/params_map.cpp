//
// Created by #Suyghur, on 2020/12/23.
//

#include "include/common.h"
#include "include/params_map.h"
#include "include/constant.h"
#include "include/jtools.h"

bool initState = false;

bool ParamsMap::init(JNIEnv *env, jobject context) {
    if (initState) {
        return initState;
    }
    Common::getInstance().paramsMap["game_code"] = JTools::getGameCode(env, context);
    Common::getInstance().paramsMap["package_name"] = JTools::getPackageName(env, context);
    Common::getInstance().paramsMap["server_version"] = JTools::getServerVersion(env, context);
    Common::getInstance().paramsMap["client_version"] = JTools::getClientVersion(env, context);
    Common::getInstance().paramsMap["game_version_code"] = JTools::getVersionCode(env, context);
    Common::getInstance().paramsMap["game_version_name"] = JTools::getVersionName(env, context);
    Common::getInstance().paramsMap["local_language"] = JTools::getLocalLanguage(env, context);
    Common::getInstance().paramsMap["android_id"] = JTools::getAndroidId(env, context);
    Common::getInstance().paramsMap["simulator"] = JTools::isSimulator(env, context);
    Common::getInstance().paramsMap["imei"] = JTools::getImei(env, context);
    Common::getInstance().paramsMap["mac"] = JTools::getMac(env, context);
    Common::getInstance().paramsMap["network"] = JTools::getNetwork(env, context);
    Common::getInstance().paramsMap["os_version"] = JTools::getOsVersion();
    Common::getInstance().paramsMap["model"] = JTools::getModel();
    Common::getInstance().paramsMap["mfrs"] = JTools::getManufacturer();
    Common::getInstance().paramsMap["mobile_brand"] = JTools::getMobileBrand();

    initState = true;
    return initState;
}

std::string ParamsMap::get(const std::string &key) {
    return Common::getInstance().paramsMap[key];
}

void ParamsMap::put(const std::string &key, const std::string &value) {
    Common::getInstance().paramsMap[key] = value;
}

Json::Value ParamsMap::getCommon(JNIEnv *env, jobject context) {
    if (!initState) {
        init(env, context);
    }

    Json::Value root, common, biz, vers, device;

    biz["game_code"] = Common::getInstance().paramsMap["game_code"];
    biz["package_name"] = Common::getInstance().paramsMap["package_name"];
    common["biz"] = biz;

    vers["server_version"] = Common::getInstance().paramsMap["server_version"];
    vers["client_version"] = Common::getInstance().paramsMap["client_version"];
    vers["game_version_code"] = Common::getInstance().paramsMap["game_version_code"];
    vers["game_version_name"] = Common::getInstance().paramsMap["game_version_name"];
    common["vers"] = vers;

    device["local_language"] = Common::getInstance().paramsMap["local_language"];
    device["screen"] = Common::getInstance().paramsMap["screen"];
    device["simulator"] = Common::getInstance().paramsMap["simulator"];
    device["imei"] = Common::getInstance().paramsMap["imei"];
    device["device_id"] = Common::getInstance().paramsMap["device_id"];
    device["mac"] = Common::getInstance().paramsMap["mac"];
    device["adid"] = Common::getInstance().paramsMap["adid"];
    device["android_id"] = Common::getInstance().paramsMap["android_id"];
    device["idfa"] = "";
    device["idfv"] = "";
    device["network"] = Common::getInstance().paramsMap["network"];
    device["os"] = "android";
    device["os_version"] = Common::getInstance().paramsMap["os_version"];
    device["model"] = Common::getInstance().paramsMap["model"];
    device["mfrs"] = Common::getInstance().paramsMap["mfrs"];
    device["mobile_brand"] = Common::getInstance().paramsMap["mobile_brand"];
    common["device"] = device;

    common["ext"] = "";

    return common;
}

std::string ParamsMap::addCommon(JNIEnv *env, jobject context, jstring param) {
    if (param == nullptr) {
        Json::Value _root;
        Json::StreamWriterBuilder _builder;
        std::string _data;
        std::ostringstream _oss;
        //无格式输出
        _builder.settings_["indentation"] = "";

        _root["common"] = getCommon(env, context);
        std::unique_ptr<Json::StreamWriter> json_writer(_builder.newStreamWriter());
        json_writer->write(_root, &_oss);
        _data = _oss.str();
        return _data;
    }

    std::string json_param = JTools::jstring2str(env, param);
    Json::StreamWriterBuilder writer_builder;
    Json::CharReaderBuilder reader_builder;
    Json::CharReader *reader_ptr(reader_builder.newCharReader());
    JSONCPP_STRING _errs;
    Json::Value _root;
    std::string _data;
    std::ostringstream _oss;

    if (reader_ptr->parse(json_param.c_str(), json_param.c_str() + json_param.length(), &_root,
                          &_errs)) {
        _root["common"] = getCommon(env, context);
    }

    writer_builder.settings_["indentation"] = "";
    std::unique_ptr<Json::StreamWriter> json_writer(writer_builder.newStreamWriter());
    json_writer->write(_root, &_oss);
    _data = _oss.str();
    return _data;
}








