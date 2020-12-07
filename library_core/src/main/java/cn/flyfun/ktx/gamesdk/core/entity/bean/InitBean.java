package cn.flyfun.ktx.gamesdk.core.entity.bean;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.flyfun.support.JsonUtils;

/**
 * @author #Suyghur.
 * Created on 10/26/20
 */
public class InitBean {
    public InitNotice initNotice = null;
    public InitPrivacy initPrivacy = null;
    public InitGm initGm = null;

    public static InitBean toBean(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        InitBean bean = null;
        JSONObject jsonObject;
        try {
            bean = new InitBean();
            bean.initNotice = new InitNotice();
            bean.initPrivacy = new InitPrivacy();
            bean.initGm = new InitGm();
            jsonObject = new JSONObject(json);
            if (JsonUtils.hasJsonKey(jsonObject, "notice_switch")) {
                bean.initNotice.noticeSwitch = jsonObject.getInt("notice_switch");
            }
            if (JsonUtils.hasJsonKey(jsonObject, "notice_url")) {
                bean.initNotice.url = jsonObject.getString("notice_url");
            }
            if (JsonUtils.hasJsonKey(jsonObject, "notice_show_count")) {
                bean.initNotice.showCount = jsonObject.getInt("notice_show_count");
            }
            if (JsonUtils.hasJsonKey(jsonObject, "privacy_switch")) {
                bean.initPrivacy.privacySwitch = jsonObject.getInt("privacy_switch");
            }
            if (JsonUtils.hasJsonKey(jsonObject, "privacy_url")) {
                bean.initPrivacy.url = jsonObject.getString("privacy_url");
            }
            if (JsonUtils.hasJsonKey(jsonObject, "gm_switch")) {
                bean.initGm.gmSwitch = jsonObject.getInt("gm_switch");
            }
            if (JsonUtils.hasJsonKey(jsonObject, "gm_url")) {
                bean.initGm.url = jsonObject.getString("gm_url");
            }
            if (JsonUtils.hasJsonKey(jsonObject, "logo_pic")) {
                bean.initGm.logoUrl = jsonObject.getString("logo_pic");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }

    @Override
    public String toString() {
        return "InitBean{" +
                "initNotice=" + initNotice +
                ", initPrivacy=" + initPrivacy +
                ", initGm=" + initGm +
                '}';
    }

    public static class InitNotice {

        public int noticeSwitch = 0;
        public String url = "";
        public int showCount = 1;

        @Override
        public String toString() {
            return "InitNotice{" +
                    "noticeSwitch=" + noticeSwitch +
                    ", url='" + url + '\'' +
                    ", showCount=" + showCount +
                    '}';
        }
    }

    public static class InitPrivacy {
        public int privacySwitch = 0;
        public String url = "";

        @Override
        public String toString() {
            return "InitPrivacy{" +
                    "privacySwitch=" + privacySwitch +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    public static class InitGm {
        public int gmSwitch = 0;
        public String url = "";
        public String logoUrl = "";

        @Override
        public String toString() {
            return "InitGm{" +
                    "gmSwitch=" + gmSwitch +
                    ", url='" + url + '\'' +
                    ", logoUrl='" + logoUrl + '\'' +
                    '}';
        }
    }
}
