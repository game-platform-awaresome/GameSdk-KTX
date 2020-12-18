package cn.flyfun.gamesdk.core.entity.bean

import android.text.TextUtils
import cn.flyfun.support.JsonUtils
import org.json.JSONException
import org.json.JSONObject

/**
 * @author #Suyghur.
 * Created on 2020/12/10
 */
class InitBean {

    var initNotice: InitNotice? = null
    var initPrivacy: InitPrivacy? = null
    var initGm: InitGm? = null
    override fun toString(): String {
        return "InitBean{" +
                "initNotice=" + initNotice +
                ", initPrivacy=" + initPrivacy +
                ", initGm=" + initGm +
                '}'
    }


    class InitNotice {
        var noticeSwitch = 0
        var url = ""
        var showCount = 1
        override fun toString(): String {
            return "InitNotice{" +
                    "noticeSwitch=" + noticeSwitch +
                    ", url='" + url + '\'' +
                    ", showCount=" + showCount +
                    '}'
        }
    }

    class InitPrivacy {
        var privacySwitch = 0
        var url = ""
        override fun toString(): String {
            return "InitPrivacy{" +
                    "privacySwitch=" + privacySwitch +
                    ", url='" + url + '\'' +
                    '}'
        }
    }

    class InitGm {
        var gmSwitch = 0
        var url = ""
        var logoUrl = ""
        override fun toString(): String {
            return "InitGm{" +
                    "gmSwitch=" + gmSwitch +
                    ", url='" + url + '\'' +
                    ", logoUrl='" + logoUrl + '\'' +
                    '}'
        }
    }

    companion object {
        fun toBean(json: String): InitBean? {
            if (TextUtils.isEmpty(json)) {
                return null
            }
            try {
                val bean = InitBean()
                bean.initNotice = InitNotice()
                bean.initPrivacy = InitPrivacy()
                bean.initGm = InitGm()
                val jsonObject = JSONObject(json)
                if (JsonUtils.hasJsonKey(jsonObject, "notice_switch")) {
                    bean.initNotice!!.noticeSwitch = jsonObject.getInt("notice_switch")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "notice_url")) {
                    bean.initNotice!!.url = jsonObject.getString("notice_url")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "notice_show_count")) {
                    bean.initNotice!!.showCount = jsonObject.getInt("notice_show_count")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "privacy_switch")) {
                    bean.initPrivacy!!.privacySwitch = jsonObject.getInt("privacy_switch")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "privacy_url")) {
                    bean.initPrivacy!!.url = jsonObject.getString("privacy_url")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "gm_switch")) {
                    bean.initGm!!.gmSwitch = jsonObject.getInt("gm_switch")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "gm_url")) {
                    bean.initGm!!.url = jsonObject.getString("gm_url")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "logo_pic")) {
                    bean.initGm!!.logoUrl = jsonObject.getString("logo_pic")
                }
                return bean
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }
    }
}