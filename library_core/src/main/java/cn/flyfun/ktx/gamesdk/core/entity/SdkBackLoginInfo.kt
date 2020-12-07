package cn.flyfun.ktx.gamesdk.core.entity

import org.json.JSONException

import org.json.JSONObject


/**
 * @author #Suyghur.
 * Created on 2020/12/2
 */
class SdkBackLoginInfo private constructor() {

    companion object {
        @JvmStatic
        val instance: SdkBackLoginInfo by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SdkBackLoginInfo()
        }
    }

    @JvmField
    var userId = ""

    @JvmField
    var timestamp = ""

    @JvmField
    var cpSign = ""

    @JvmField
    var isRegUser = 0

    @JvmField
    var isBindPlatform = 0

    @JvmField
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

}