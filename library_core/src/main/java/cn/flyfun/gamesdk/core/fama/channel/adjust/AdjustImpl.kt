package cn.flyfun.gamesdk.core.fama.channel.adjust

import android.content.Context
import android.text.TextUtils
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.base.utils.ParamsUtils
import cn.flyfun.gamesdk.core.entity.SdkBackLoginInfo
import cn.flyfun.gamesdk.core.internal.IEventObserver
import cn.flyfun.gamesdk.core.utils.NTools
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.AdjustEvent
import com.adjust.sdk.LogLevel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * @author #Suyghur,
 * Created on 2021/2/2
 */
class AdjustImpl : IEventObserver {

    private var isInitSuccess = false
    private var bean: AdjustEventBean? = null

    override fun onInitialize(context: Context) {
        val appToken = ParamsUtils.getAdjustAppId(context)
        bean = AdjustEventBean.toBean(getLogJson(context))
        if (TextUtils.isEmpty(appToken)) {
            Logger.e("adjust log 初始化失败，adjust_app_id为空")
            return
        }

        if (bean == null) {
            Logger.e("adjust log 初始化失败，读取assets/log_event.json异常")
            return
        }

        val config = AdjustConfig(context, appToken, AdjustConfig.ENVIRONMENT_PRODUCTION)
        config.setLogLevel(LogLevel.VERBOSE)
        Adjust.onCreate(config)
        addCommonSessionParams(context)
        isInitSuccess = true
        Adjust.trackEvent(AdjustEvent(bean!!.eventActivities))
    }

    override fun onLogin(context: Context) {
        if (!isInitSuccess) {
            Logger.e("adjust log failed , component initialize failed")
            return
        }

        if (TextUtils.isEmpty(SdkBackLoginInfo.instance.userId)) {
            Logger.e("adjust log EVENT_LOGIN_SUCCESS failed , user is null")
            return
        }

        resetSessionParams()
        val loginEvent = AdjustEvent(bean!!.eventLoginSuccess)
        Adjust.addSessionCallbackParameter("user_id", SdkBackLoginInfo.instance.userId)
        Adjust.addSessionCallbackParameter("device_id", NTools.getParam("device_id"))
        Adjust.trackEvent(loginEvent)
    }

    override fun onRegister(context: Context) {
        if (!isInitSuccess) {
            Logger.e("adjust log failed , component initialize failed")
            return
        }

        if (TextUtils.isEmpty(SdkBackLoginInfo.instance.userId)) {
            Logger.e("adjust log EVENT_USER_REGISTER failed , user is null")
            return
        }

        resetSessionParams()
        val registerEvent = AdjustEvent(bean!!.eventUserRegister)
        Adjust.addSessionCallbackParameter("user_id", SdkBackLoginInfo.instance.userId)
        Adjust.addSessionCallbackParameter("device_id", NTools.getParam("device_id"))
        Adjust.trackEvent(registerEvent)
    }

    override fun onCharge(context: Context, eventMap: HashMap<String, Any>) {
        if (!isInitSuccess) {
            Logger.e("adjust log failed , component initialize failed")
            return
        }

        if (eventMap.isEmpty()) {
            Logger.e("adjust log EVENT_CHARGE_SUCCESS failed , event map is null")
            return
        }

        resetSessionParams()
        val chargeEvent = AdjustEvent(bean!!.eventChargeSuccess)
        Adjust.addSessionCallbackParameter("user_id", SdkBackLoginInfo.instance.userId)
        Adjust.addSessionCallbackParameter("device_id", NTools.getParam("device_id"))
        if (eventMap.containsKey("role_id")) {
            Adjust.addSessionCallbackParameter("role_id", eventMap["role_id"] as String)
        } else {
            Adjust.addSessionCallbackParameter("role_id", "none")
        }
        if (eventMap.containsKey("role_name")) {
            Adjust.addSessionCallbackParameter("role_name", eventMap["role_name"] as String)
        } else {
            Adjust.addSessionCallbackParameter("role_name", "none")
        }
        if (eventMap.containsKey("server_code")) {
            Adjust.addSessionCallbackParameter("server_code", eventMap["server_code"] as String)
        } else {
            Adjust.addSessionCallbackParameter("server_code", "none")
        }
        if (eventMap.containsKey("server_name")) {
            Adjust.addSessionCallbackParameter("server_name", eventMap["server_name"] as String)
        } else {
            Adjust.addSessionCallbackParameter("server_name", "none")
        }
        if (eventMap.containsKey("price")) {
            val amount: Double = (eventMap["price"] as Float).toDouble()
            chargeEvent.setRevenue(amount, "USD")
        } else {
            chargeEvent.setRevenue(0.00, "USD")
        }
        if (eventMap.containsKey("order_id")) {
            chargeEvent.setOrderId(eventMap["order_id"] as String)
        } else {
            chargeEvent.setOrderId("none")
        }
        Adjust.trackEvent(chargeEvent)
    }

    override fun onRoleCreate(context: Context, eventMap: HashMap<String, Any>) {
        if (!isInitSuccess) {
            Logger.e("adjust log failed , component initialize failed")
            return
        }

        if (eventMap.isEmpty()) {
            Logger.e("adjust log EVENT_ROLE_CREATE failed , event map is null")
            return
        }

        resetSessionParams()
        val roleCreateEvent = AdjustEvent(bean!!.eventRoleCreate)
        Adjust.addSessionCallbackParameter("user_id", SdkBackLoginInfo.instance.userId)
        Adjust.addSessionCallbackParameter("device_id", NTools.getParam("device_id"))
        if (eventMap.containsKey("role_id")) {
            Adjust.addSessionCallbackParameter("role_id", eventMap["role_id"] as String)
        } else {
            Adjust.addSessionCallbackParameter("role_id", "none")
        }
        if (eventMap.containsKey("role_name")) {
            Adjust.addSessionCallbackParameter("role_name", eventMap["role_name"] as String)
        } else {
            Adjust.addSessionCallbackParameter("role_name", "none")
        }
        if (eventMap.containsKey("server_code")) {
            Adjust.addSessionCallbackParameter("server_code", eventMap["server_code"] as String)
        } else {
            Adjust.addSessionCallbackParameter("server_code", "none")
        }
        if (eventMap.containsKey("server_name")) {
            Adjust.addSessionCallbackParameter("server_name", eventMap["server_name"] as String)
        } else {
            Adjust.addSessionCallbackParameter("server_name", "none")
        }
        Adjust.trackEvent(roleCreateEvent)
    }

    override fun onRoleLauncher(context: Context, eventMap: HashMap<String, Any>) {
        if (!isInitSuccess) {
            Logger.e("adjust log failed , component initialize failed")
            return
        }

        if (eventMap.isEmpty()) {
            Logger.e("adjust log EVENT_ROLE_LAUNCHER failed , event map is null")
            return
        }

        resetSessionParams()
        val roleLauncherEvent = AdjustEvent(bean!!.eventRoleLauncher)
        Adjust.addSessionCallbackParameter("user_id", SdkBackLoginInfo.instance.userId)
        Adjust.addSessionCallbackParameter("device_id", NTools.getParam("device_id"))
        if (eventMap.containsKey("role_id")) {
            Adjust.addSessionCallbackParameter("role_id", eventMap["role_id"] as String)
        } else {
            Adjust.addSessionCallbackParameter("role_id", "none")
        }
        if (eventMap.containsKey("role_name")) {
            Adjust.addSessionCallbackParameter("role_name", eventMap["role_name"] as String)
        } else {
            Adjust.addSessionCallbackParameter("role_name", "none")
        }
        if (eventMap.containsKey("server_code")) {
            Adjust.addSessionCallbackParameter("server_code", eventMap["server_code"] as String)
        } else {
            Adjust.addSessionCallbackParameter("server_code", "none")
        }
        if (eventMap.containsKey("server_name")) {
            Adjust.addSessionCallbackParameter("server_name", eventMap["server_name"] as String)
        } else {
            Adjust.addSessionCallbackParameter("server_name", "none")
        }
        Adjust.trackEvent(roleLauncherEvent)
    }

    override fun onResume(context: Context) {
        if (!isInitSuccess) {
            Logger.e("adjust log failed , component initialize failed")
            return
        }

        Adjust.onResume()
    }

    override fun onPause(context: Context) {
        if (!isInitSuccess) {
            Logger.e("adjust log failed , component initialize failed")
            return
        }

        Adjust.onPause()
    }

    private fun addCommonSessionParams(context: Context) {
        Adjust.addSessionCallbackParameter("game_code", ParamsUtils.getGameCode(context))
        Adjust.addSessionCallbackParameter("package_name", context.packageName)
        Adjust.addSessionCallbackParameter("platform", "android")
    }


    private fun resetSessionParams() {
        Adjust.removeSessionCallbackParameter("user_id")
        Adjust.removeSessionCallbackParameter("role_id")
        Adjust.removeSessionCallbackParameter("role_name")
        Adjust.removeSessionCallbackParameter("server_code")
        Adjust.removeSessionCallbackParameter("server_name")
        Adjust.removeSessionCallbackParameter("device_id")
    }

    private fun getLogJson(context: Context): String {
        val filePath = "flyfun/log_event.json"
        val sb = StringBuffer()
        try {
            val assetManager = context.assets
            val bf = BufferedReader(InputStreamReader(assetManager.open(filePath)))
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                sb.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }
}