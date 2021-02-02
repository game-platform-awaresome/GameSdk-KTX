package cn.flyfun.gamesdk.core.fama

import android.content.Context
import android.text.TextUtils
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.base.utils.ParamsUtils
import cn.flyfun.gamesdk.core.entity.SdkBackLoginInfo
import cn.flyfun.gamesdk.core.entity.bean.LogBean
import cn.flyfun.gamesdk.core.fama.channel.adjust.AdjustImpl
import cn.flyfun.gamesdk.core.fama.channel.firebase.FirebaseImpl
import cn.flyfun.gamesdk.core.internal.IEventTrace
import cn.flyfun.gamesdk.core.utils.NTools
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.AdjustEvent
import com.adjust.sdk.LogLevel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.HashMap


/**
 * @author #Suyghur.
 * Created on 2020/12/1
 */
class EventTraceImpl private constructor() {
    private val adjust: AdjustImpl = AdjustImpl()
    private val firebase: FirebaseImpl = FirebaseImpl()

    fun onInitialize(context: Context) {
        adjust.onInitialize(context)
        firebase.onInitialize(context)
    }

    fun onLogin(context: Context) {
        adjust.onLogin(context)
        firebase.onLogin(context)
    }

    fun onRegister(context: Context) {
        adjust.onRegister(context)
        firebase.onRegister(context)
    }

    fun onCharge(context: Context, eventMap: HashMap<String, Any>) {
        adjust.onCharge(context, eventMap)
        firebase.onCharge(context, eventMap)
    }

    fun onRoleCreate(context: Context, eventMap: HashMap<String, Any>) {
        adjust.onRoleCreate(context, eventMap)
        firebase.onRoleCreate(context, eventMap)
    }

    fun onRoleLauncher(context: Context, eventMap: HashMap<String, Any>) {
        adjust.onRoleLauncher(context, eventMap)
        firebase.onRoleLauncher(context, eventMap)
    }

    fun onResume(context: Context) {
        adjust.onResume(context)
    }

    fun onPause(context: Context) {
        adjust.onPause(context)
    }


    companion object {
        fun getInstance(): EventTraceImpl {
            return EventTraceImplHolder.INSTANCE
        }
    }

    private object EventTraceImplHolder {
        val INSTANCE: EventTraceImpl = EventTraceImpl()
    }
}