package cn.flyfun.ktx.gamesdk.base.utils

import android.os.Handler
import cn.flyfun.support.jarvis.LogRvds

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
object Logger {

    private const val TAG: String = "flyfun_game"
    var debug: Boolean = true
    var handler: Handler? = null

    fun i(any: Any) {
        LogRvds.i(TAG, any)
    }

    fun e(msg: String) {
        LogRvds.e(TAG, msg)
    }

    fun d(any: Any) {
        if (debug) {
            LogRvds.d(TAG, any)
        }
    }

    fun logHandler(msg: String) {
        handler?.apply {
            LogRvds.logHandler(this, msg)
        }
    }
}