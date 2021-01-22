package cn.flyfun.gamesdk.core.apm

import android.content.Context
import android.text.TextUtils
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.base.utils.ParamsUtils
import cn.flyfun.gamesdk.core.entity.SdkBackLoginInfo
import cn.flyfun.gamesdk.core.entity.bean.LogBean
import cn.flyfun.gamesdk.core.inter.IEventTrace
import cn.flyfun.gamesdk.core.utils.NTools
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.AdjustEvent
import com.adjust.sdk.LogLevel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * @author #Suyghur.
 * Created on 2020/12/1
 */
class EventTraceImpl private constructor() : IEventTrace {

    private var isInitSuccess = false
    private var logBean: LogBean? = null

    companion object {
        fun getInstance(): EventTraceImpl {
            return EventTraceImplHolder.INSTANCE
        }
    }

    private object EventTraceImplHolder {
        val INSTANCE: EventTraceImpl = EventTraceImpl()
    }

    override fun onInitialize(context: Context) {
        val appToken = ParamsUtils.getTraceId(context)
        logBean = LogBean.toBean(getLogJson(context))
        if (TextUtils.isEmpty(appToken)) {
            Logger.e("EventTrace初始化失败，trace_id为空")
            return
        }
        if (logBean == null) {
            Logger.e("EventTrace初始化失败，读取assets/log_event.json异常")
            return
        }
        val config = AdjustConfig(context, appToken, AdjustConfig.ENVIRONMENT_PRODUCTION)
        config.setLogLevel(LogLevel.VERBOSE)
        Adjust.onCreate(config)
        addCommonSessionParams(context)
        isInitSuccess = true
        Adjust.trackEvent(AdjustEvent(logBean!!.eventActivities))
    }

    override fun onLogin() {
        if (!isInitSuccess) {
            return
        }
        if (TextUtils.isEmpty(SdkBackLoginInfo.instance.userId)) {
            Logger.e("apm log EVENT_LOGIN_SUCCESS failed , user is null")
            return
        }
        resetSessionParams()
        val loginEvent = AdjustEvent(logBean!!.eventLoginSuccess)
        Adjust.addSessionCallbackParameter("user_id", SdkBackLoginInfo.instance.userId)
        Adjust.addSessionCallbackParameter("device_id", NTools.getParam("device_id"))
        Adjust.trackEvent(loginEvent)
    }

    override fun onRegister() {
        if (!isInitSuccess) {
            return
        }
        if (TextUtils.isEmpty(SdkBackLoginInfo.instance.userId)) {
            Logger.e("apm log EVENT_USER_REGISTER failed , user is null")
            return
        }
        resetSessionParams()
        val registerEvent = AdjustEvent(logBean!!.eventUserRegister)
        Adjust.addSessionCallbackParameter("user_id", SdkBackLoginInfo.instance.userId)
        Adjust.addSessionCallbackParameter("device_id", NTools.getParam("device_id"))
        Adjust.trackEvent(registerEvent)
    }

    override fun onCharge(eventMap: HashMap<String, Any>) {
        if (!isInitSuccess) {
            return
        }
        resetSessionParams()
        val chargeEvent = AdjustEvent(logBean!!.eventChargeSuccess)
        Adjust.addSessionCallbackParameter("user_id", SdkBackLoginInfo.instance.userId)
        Adjust.addSessionCallbackParameter("device_id", NTools.getParam("device_id"))
        if (eventMap.containsKey("role_id")) {
            Adjust.addSessionCallbackParameter("role_id", java.lang.String.valueOf(eventMap["role_id"]))
        } else {
            Adjust.addSessionCallbackParameter("role_id", "")
        }
        if (eventMap.containsKey("role_name")) {
            Adjust.addSessionCallbackParameter("role_name", java.lang.String.valueOf(eventMap["role_name"]))
        } else {
            Adjust.addSessionCallbackParameter("role_name", "")
        }
        if (eventMap.containsKey("server_code")) {
            Adjust.addSessionCallbackParameter("server_code", java.lang.String.valueOf(eventMap["server_code"]))
        } else {
            Adjust.addSessionCallbackParameter("server_code", "")
        }
        if (eventMap.containsKey("server_name")) {
            Adjust.addSessionCallbackParameter("server_name", java.lang.String.valueOf(eventMap["server_name"]))
        } else {
            Adjust.addSessionCallbackParameter("server_name", "")
        }
        if (eventMap.containsKey("price")) {
            val amount: Double = eventMap["price"].toString().toDouble()
            chargeEvent.setRevenue(amount, "USD")
        } else {
            chargeEvent.setRevenue(0.00, "USD")
        }
        if (eventMap.containsKey("order_id")) {
            chargeEvent.setOrderId(eventMap["order_id"].toString())
        } else {
            chargeEvent.setOrderId("")
        }
        Adjust.trackEvent(chargeEvent)
    }

    override fun onRoleCreate(eventMap: HashMap<String, Any>) {
        if (!isInitSuccess) {
            return
        }
        resetSessionParams()
        val roleCreateEvent = AdjustEvent(logBean!!.eventRoleCreate)
        Adjust.addSessionCallbackParameter("user_id", SdkBackLoginInfo.instance.userId)
        Adjust.addSessionCallbackParameter("device_id", NTools.getParam("device_id"))
        if (eventMap.containsKey("role_id")) {
            Adjust.addSessionCallbackParameter("role_id", java.lang.String.valueOf(eventMap["role_id"]))
        } else {
            Adjust.addSessionCallbackParameter("role_id", "")
        }
        if (eventMap.containsKey("role_name")) {
            Adjust.addSessionCallbackParameter("role_name", java.lang.String.valueOf(eventMap["role_name"]))
        } else {
            Adjust.addSessionCallbackParameter("role_name", "")
        }
        if (eventMap.containsKey("server_code")) {
            Adjust.addSessionCallbackParameter("server_code", java.lang.String.valueOf(eventMap["server_code"]))
        } else {
            Adjust.addSessionCallbackParameter("server_code", "")
        }
        if (eventMap.containsKey("server_name")) {
            Adjust.addSessionCallbackParameter("server_name", java.lang.String.valueOf(eventMap["server_name"]))
        } else {
            Adjust.addSessionCallbackParameter("server_name", "")
        }
        Adjust.trackEvent(roleCreateEvent)
    }

    override fun onRoleLauncher(eventMap: HashMap<String, Any>) {
        if (!isInitSuccess) {
            return
        }
        resetSessionParams()
        val roleLauncherEvent = AdjustEvent(logBean!!.eventRoleLauncher)
        Adjust.addSessionCallbackParameter("user_id", SdkBackLoginInfo.instance.userId)
        Adjust.addSessionCallbackParameter("device_id", NTools.getParam("device_id"))
        if (eventMap.containsKey("role_id")) {
            Adjust.addSessionCallbackParameter("role_id", java.lang.String.valueOf(eventMap["role_id"]))
        } else {
            Adjust.addSessionCallbackParameter("role_id", "")
        }
        if (eventMap.containsKey("role_name")) {
            Adjust.addSessionCallbackParameter("role_name", java.lang.String.valueOf(eventMap["role_name"]))
        } else {
            Adjust.addSessionCallbackParameter("role_name", "")
        }
        if (eventMap.containsKey("server_code")) {
            Adjust.addSessionCallbackParameter("server_code", java.lang.String.valueOf(eventMap["server_code"]))
        } else {
            Adjust.addSessionCallbackParameter("server_code", "")
        }
        if (eventMap.containsKey("server_name")) {
            Adjust.addSessionCallbackParameter("server_name", java.lang.String.valueOf(eventMap["server_name"]))
        } else {
            Adjust.addSessionCallbackParameter("server_name", "")
        }
        Adjust.trackEvent(roleLauncherEvent)
    }

    override fun onResume() {
        if (!isInitSuccess) {
            return
        }
        Adjust.onResume()
    }

    override fun onPause() {
        if (!isInitSuccess) {
            return
        }
        Adjust.onPause()
    }

    fun getAaid(): String {
        return Adjust.getAdid()
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