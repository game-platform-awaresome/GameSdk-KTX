package cn.flyfun.gamesdk.core.utils

import cn.flyfun.gamesdk.base.utils.Logger

/**
 * @author #Suyghur.
 * Created on 2020/12/9
 */
object TimeDownUtils {

    private var isRunning = false
    private var isCancel = false

    @Volatile
    private var times: Times? = null

    fun start(callback: TimeCallback) {
        if (isRunning) {
            Logger.e("倒计时还在执行中...")
        } else {
            isRunning = true
            isCancel = false
            times = Times(callback)
            times!!.start()
        }
    }

    fun cancel() {
        isCancel = true
        if (isRunning) {
            times?.apply {
                interrupt()
                times = null
            }
        }
        isRunning = false
        Logger.d("TimeDownUtils.cancel")
    }

    fun resetCallback(callback: TimeCallback) {
        times?.apply {
            this.callback = callback
        }
    }

    fun isRunning(): Boolean {
        return isRunning
    }


    interface TimeCallback {
        fun onTime(time: Int)
    }

    private class Times(var callback: TimeCallback) : Thread() {

        override fun run() {
            callback.onTime(59)
            Logger.d("timeDownUtils 倒计时开始...")
            for (i in 59 downTo 0) {
                try {
                    sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                if (isCancel) {
                    Logger.d("TimeDownUtils 线程已退出")
                    return
                }
                callback.onTime(i)
            }
            cancel()
        }
    }

}