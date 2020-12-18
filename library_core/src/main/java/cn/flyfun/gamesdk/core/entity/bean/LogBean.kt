package cn.flyfun.gamesdk.core.entity.bean

import android.text.TextUtils
import cn.flyfun.support.JsonUtils
import org.json.JSONException
import org.json.JSONObject

/**
 * @author #Suyghur.
 * Created on 2020/12/10
 */
class LogBean {
    var eventActivities: String? = null
    var eventLoginSuccess: String? = null
    var eventUserRegister: String? = null
    var eventChargeSuccess: String? = null
    var eventRoleCreate: String? = null
    var eventRoleLauncher: String? = null
    override fun toString(): String {
        return "LogBean{" +
                "eventActivities='" + eventActivities + '\'' +
                ", eventLoginSuccess='" + eventLoginSuccess + '\'' +
                ", eventUserRegister='" + eventUserRegister + '\'' +
                ", eventChargeSuccess='" + eventChargeSuccess + '\'' +
                ", eventRoleCreate='" + eventRoleCreate + '\'' +
                ", eventRoleLauncher='" + eventRoleLauncher + '\'' +
                '}'
    }

    companion object {
        fun toBean(json: String): LogBean? {
            if (TextUtils.isEmpty(json)) {
                return null
            }
            try {
                val bean = LogBean()
                val jsonObject = JSONObject(json)
                if (JsonUtils.hasJsonKey(jsonObject, "event_activities")) {
                    bean.eventActivities = jsonObject.getString("event_activities")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "event_login_success")) {
                    bean.eventLoginSuccess = jsonObject.getString("event_login_success")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "event_user_register")) {
                    bean.eventUserRegister = jsonObject.getString("event_user_register")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "event_charge_success")) {
                    bean.eventChargeSuccess = jsonObject.getString("event_charge_success")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "event_role_create")) {
                    bean.eventRoleCreate = jsonObject.getString("event_role_create")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "event_role_launcher")) {
                    bean.eventRoleLauncher = jsonObject.getString("event_role_launcher")
                }
                return bean
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }
    }
}