package cn.flyfun.ktx.gamesdk.core.entity

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

/**
 * @author #Suyghur.
 * Created on 2020/8/2
 */
class Session : Serializable {
    var userId = ""
    var userName = ""
    var pwd = ""
    var loginType = -1

    fun reset() {
        userId = ""
        userName = ""
        pwd = ""
        loginType = -1
    }

    fun toJSONObject(): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", userId)
            jsonObject.put("user_name", userName)
            jsonObject.put("pwd", pwd)
            jsonObject.put("login_type", loginType)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    override fun toString(): String {
        return "Session{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", pwd='" + pwd + '\'' +
                ", loginType=" + loginType +
                '}'
    }
}