package cn.flyfun.ktx.gamesdk.core.entity.bean;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.flyfun.support.JsonUtils;

/**
 * Author #Suyghur.
 * Created on 11/16/20
 */
public class LogBean {

    public String eventActivities;
    public String eventLoginSuccess;
    public String eventUserRegister;
    public String eventChargeSuccess;
    public String eventRoleCreate;
    public String eventRoleLauncher;

    public static LogBean toBean(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        LogBean bean = null;
        JSONObject jsonObject;
        try {
            bean = new LogBean();
            jsonObject = new JSONObject(json);
            if (JsonUtils.hasJsonKey(jsonObject, "event_activities")) {
                bean.eventActivities = jsonObject.getString("event_activities");
            }
            if (JsonUtils.hasJsonKey(jsonObject, "event_login_success")) {
                bean.eventLoginSuccess = jsonObject.getString("event_login_success");
            }
            if (JsonUtils.hasJsonKey(jsonObject, "event_user_register")) {
                bean.eventUserRegister = jsonObject.getString("event_user_register");
            }
            if (JsonUtils.hasJsonKey(jsonObject, "event_charge_success")) {
                bean.eventChargeSuccess = jsonObject.getString("event_charge_success");
            }
            if (JsonUtils.hasJsonKey(jsonObject, "event_role_create")) {
                bean.eventRoleCreate = jsonObject.getString("event_role_create");
            }
            if (JsonUtils.hasJsonKey(jsonObject, "event_role_launcher")) {
                bean.eventRoleLauncher = jsonObject.getString("event_role_launcher");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    public String toString() {
        return "LogBean{" +
                "eventActivities='" + eventActivities + '\'' +
                ", eventLoginSuccess='" + eventLoginSuccess + '\'' +
                ", eventUserRegister='" + eventUserRegister + '\'' +
                ", eventChargeSuccess='" + eventChargeSuccess + '\'' +
                ", eventRoleCreate='" + eventRoleCreate + '\'' +
                ", eventRoleLauncher='" + eventRoleLauncher + '\'' +
                '}';
    }
}
