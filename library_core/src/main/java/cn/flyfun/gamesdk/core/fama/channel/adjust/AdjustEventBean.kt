package cn.flyfun.gamesdk.core.fama.channel.adjust

import android.text.TextUtils
import cn.flyfun.gamesdk.core.entity.bean.LogBean
import cn.flyfun.support.JsonUtils
import org.json.JSONException
import org.json.JSONObject

/**
 * @author #Suyghur,
 * Created on 2021/2/2
 */
class AdjustEventBean {

    var eventActivities: String = ""
    var eventLoginSuccess: String = ""
    var eventUserRegister: String = ""
    var eventChargeSuccess: String = ""
    var eventRoleCreate: String = ""
    var eventRoleLauncher: String = ""

    override fun toString(): String {
        return "AdjustEventBean{" +
                "eventActivities='" + eventActivities + '\'' +
                ", eventLoginSuccess='" + eventLoginSuccess + '\'' +
                ", eventUserRegister='" + eventUserRegister + '\'' +
                ", eventChargeSuccess='" + eventChargeSuccess + '\'' +
                ", eventRoleCreate='" + eventRoleCreate + '\'' +
                ", eventRoleLauncher='" + eventRoleLauncher + '\'' +
                '}'
    }

    companion object {
        fun toBean(json: String): AdjustEventBean? {
            if (TextUtils.isEmpty(json)) {
                return null
            }
            try {
                val bean = AdjustEventBean()
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