package cn.flyfun.ktx.gamesdk.base.utils

import android.os.Handler
import cn.flyfun.support.jarvis.LogRvds

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
object Logger {

    private const val TAG: String = "flyfun_game"

    @JvmField
    var debug: Boolean = true

    @JvmField
    var handler: Handler? = null

    @JvmStatic
    fun i(any: Any) {
        LogRvds.i(TAG, any)
    }

    @JvmStatic
    fun e(msg: String) {
        LogRvds.e(TAG, msg)
    }

    @JvmStatic
    fun d(any: Any) {
        if (debug) {
            LogRvds.d(TAG, any)
        }
    }

    @JvmStatic
    fun logHandler(msg: String) {
        handler?.apply {
            LogRvds.logHandler(this, msg)
        }
    }
}