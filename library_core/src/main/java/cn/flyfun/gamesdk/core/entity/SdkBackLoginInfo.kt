package cn.flyfun.gamesdk.core.entity

import org.json.JSONException

import org.json.JSONObject


/**
 * @author #Suyghur.
 * Created on 2020/12/2
 */
class SdkBackLoginInfo private constructor() {

    var userId = ""
    var timestamp = ""
    var cpSign = ""
    var isRegUser = 0
    var isBindPlatform = 0
    var loginType = -1

    fun reset() {
        userId = ""
        timestamp = ""
        cpSign = ""
        isRegUser = 0
        isBindPlatform = 0
        loginType = -1
    }

    fun toJsonString(): String {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", userId)
            jsonObject.put("timestamp", timestamp)
            jsonObject.put("cp_sign", cpSign)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject.toString()
    }

    override fun toString(): String {
        return "FuseBackLoginInfo{" +
                "userId='" + userId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", cpSign='" + cpSign + '\'' +
                ", isRegUser=" + isRegUser +
                ", isBindPlatform=" + isBindPlatform +
                ", loginType=" + loginType +
                '}'
    }

    companion object {
        val instance: SdkBackLoginInfo by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SdkBackLoginInfo()
        }
    }

}