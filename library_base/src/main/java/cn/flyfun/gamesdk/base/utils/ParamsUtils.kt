package cn.flyfun.gamesdk.base.utils

import android.content.Context
import android.text.TextUtils
import cn.flyfun.support.PropertiesUtils
import kotlin.Exception

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
object ParamsUtils {

    private const val CONFIG_FILE: String = "flyfun_cfg.properties"
    private const val FLYFUN_GAME_CODE: String = "FLYFUN_GAME_CODE"
    private const val FLYFUN_ADJUST_APP_ID: String = "FLYFUN_ADJUST_APP_ID"
    private const val FLYFUN_ADJUST_ENABLE: String = "FLYFUN_ADJUST_ENABLE"
    private const val FLYFUN_GOOGLE_APP_ID: String = "FLYFUN_GOOGLE_APP_ID"
    private const val FLYFUN_GOOGLE_CLIENT_ID: String = "FLYFUN_GOOGLE_CLIENT_ID"

    @JvmStatic
    fun getGameCode(context: Context): String {
        try {
            val code = PropertiesUtils.getValue4Properties(context, CONFIG_FILE, "flyfun", FLYFUN_GAME_CODE)
            if (!TextUtils.isEmpty(code)) {
                return code
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun isAdjustEnable(context: Context): Boolean {
        try {
            val enable = PropertiesUtils.getValue4Properties(context, CONFIG_FILE, "flyfun", FLYFUN_ADJUST_ENABLE)
            if (!TextUtils.isEmpty(enable)) {
                return enable.toBoolean()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //默认开启
        return true
    }

    fun getAdjustAppId(context: Context): String {
        try {
            val traceId = PropertiesUtils.getValue4Properties(context, CONFIG_FILE, "flyfun", FLYFUN_ADJUST_APP_ID)
            if (!TextUtils.isEmpty(traceId)) {
                return traceId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getGoogleAppId(context: Context): String {
        try {
            val appId = PropertiesUtils.getValue4Properties(context, CONFIG_FILE, "flyfun", FLYFUN_GOOGLE_APP_ID)
            if (!TextUtils.isEmpty(appId)) {
                return appId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getGoogleClientId(context: Context): String {
        try {
            val clientId = PropertiesUtils.getValue4Properties(context, CONFIG_FILE, "flyfun", FLYFUN_GOOGLE_CLIENT_ID)
            if (!TextUtils.isEmpty(clientId)) {
                return clientId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

}