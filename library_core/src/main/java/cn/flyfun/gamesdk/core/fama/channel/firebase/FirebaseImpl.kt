package cn.flyfun.gamesdk.core.fama.channel.firebase

import android.content.Context
import android.os.Bundle
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.base.utils.ParamsUtils
import cn.flyfun.gamesdk.core.entity.SdkBackLoginInfo
import cn.flyfun.gamesdk.core.internal.IEventTrace
import cn.flyfun.gamesdk.core.utils.NTools
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

/**
 * @author #Suyghur,
 * Created on 2021/2/2
 */
class FirebaseImpl : IEventTrace {
    override fun onInitialize(context: Context) {
        val defaultBundle = Bundle()
        defaultBundle.putString("game_code", ParamsUtils.getGameCode(context))
        defaultBundle.putString("package_name", context.packageName)
        defaultBundle.putString("platform", "android")
        Firebase.analytics.setDefaultEventParameters(defaultBundle)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
    }

    override fun onLogin(context: Context) {
        val bundle = Bundle()
        bundle.putString("device_id", NTools.getParam("device_id"))
        bundle.putString("user_id", SdkBackLoginInfo.instance.userId)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    override fun onRegister(context: Context) {
        val bundle = Bundle()
        bundle.putString("device_id", NTools.getParam("device_id"))
        bundle.putString("user_id", SdkBackLoginInfo.instance.userId)
        Firebase.analytics.logEvent("event_user_register", bundle)
    }

    override fun onCharge(context: Context, eventMap: HashMap<String, Any>) {
        if (eventMap.isEmpty()) {
            Logger.e("firebase trace log PURCHASE failed , event map is null")
            return
        }

        val bundle = Bundle()
        bundle.putString("device_id", NTools.getParam("device_id"))
        bundle.putString("user_id", SdkBackLoginInfo.instance.userId)
        if (eventMap.containsKey("role_id")) {
            bundle.putString("role_id", eventMap["role_id"] as String)
        } else {
            bundle.putString("role_id", "none")
        }
        if (eventMap.containsKey("role_name")) {
            bundle.putString("role_name", eventMap["role_name"] as String)
        } else {
            bundle.putString("role_name", "none")
        }
        if (eventMap.containsKey("server_code")) {
            bundle.putString("server_code", eventMap["server_code"] as String)
        } else {
            bundle.putString("server_code", "none")
        }
        if (eventMap.containsKey("server_name")) {
            bundle.putString("server_name", eventMap["server_name"] as String)
        } else {
            bundle.putString("server_name", "none")
        }
        if (eventMap.containsKey("price")) {
            val amount: String = (eventMap["price"] as Float).toString()
            bundle.putString(FirebaseAnalytics.Param.PRICE, amount)
        } else {
            bundle.putString(FirebaseAnalytics.Param.PRICE, "0.00")
        }
        if (eventMap.containsKey("order_id")) {
            bundle.putString("order_id", eventMap["order_id"] as String)
        } else {
            bundle.putString("order_id", "none")
        }
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)
    }

    override fun onRoleCreate(context: Context, eventMap: HashMap<String, Any>) {
        if (eventMap.isEmpty()) {
            Logger.e("firebase trace log EVENT_ROLE_CREATE failed , event map is null")
            return
        }

        val bundle = Bundle()
        bundle.putString("device_id", NTools.getParam("device_id"))
        bundle.putString("user_id", SdkBackLoginInfo.instance.userId)
        if (eventMap.containsKey("role_id")) {
            bundle.putString("role_id", eventMap["role_id"] as String)
        } else {
            bundle.putString("role_id", "none")
        }
        if (eventMap.containsKey("role_name")) {
            bundle.putString("role_name", eventMap["role_name"] as String)
        } else {
            bundle.putString("role_name", "none")
        }
        if (eventMap.containsKey("server_code")) {
            bundle.putString("server_code", eventMap["server_code"] as String)
        } else {
            bundle.putString("server_code", "none")
        }
        if (eventMap.containsKey("server_name")) {
            bundle.putString("server_name", eventMap["server_name"] as String)
        } else {
            bundle.putString("server_name", "none")
        }

        Firebase.analytics.logEvent("event_role_create", bundle)
    }

    override fun onRoleLauncher(context: Context, eventMap: HashMap<String, Any>) {
        if (eventMap.isEmpty()) {
            Logger.e("firebase trace log EVENT_ROLE_LAUNCHER failed , event map is null")
            return
        }

        val bundle = Bundle()
        bundle.putString("device_id", NTools.getParam("device_id"))
        bundle.putString("user_id", SdkBackLoginInfo.instance.userId)
        if (eventMap.containsKey("role_id")) {
            bundle.putString("role_id", eventMap["role_id"] as String)
        } else {
            bundle.putString("role_id", "none")
        }
        if (eventMap.containsKey("role_name")) {
            bundle.putString("role_name", eventMap["role_name"] as String)
        } else {
            bundle.putString("role_name", "none")
        }
        if (eventMap.containsKey("server_code")) {
            bundle.putString("server_code", eventMap["server_code"] as String)
        } else {
            bundle.putString("server_code", "none")
        }
        if (eventMap.containsKey("server_name")) {
            bundle.putString("server_name", eventMap["server_name"] as String)
        } else {
            bundle.putString("server_name", "none")
        }

        Firebase.analytics.logEvent("event_role_launcher", bundle)
    }

    override fun onResume(context: Context) {
    }

    override fun onPause(context: Context) {
    }
}