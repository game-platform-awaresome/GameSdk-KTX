package cn.flyfun.gamesdk.base.utils

import android.os.Handler
import cn.flyfun.support.jarvis.LogRvds
import cn.flyfun.zap.Zap

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
object Logger {

    private const val TAG: String = "flyfun_game"
    var debug: Boolean = true
    var handler: Handler? = null
    var zapInitSuccess = false

    fun i(any: Any) {
//        LogRvds.i(TAG, any)
        if (zapInitSuccess) {
            Zap.i(TAG, any)
        } else {
            LogRvds.i(TAG, any)
        }
    }

    fun e(msg: String) {
        if (zapInitSuccess) {
            Zap.e(TAG, msg)
        } else {
            LogRvds.e(TAG, msg)
        }
    }

    @JvmStatic
    fun d(any: Any) {
        d(TAG, any)
    }

    @JvmStatic
    fun d(tag: String, any: Any) {
        if (debug) {
            if (zapInitSuccess) {
                Zap.d(tag, any)
            } else {
                LogRvds.d(tag, any)
            }
        }
    }

    @JvmStatic
    fun logHandler(msg: String) {
        handler?.apply {
            LogRvds.logHandler(this, msg)
        }
    }
}