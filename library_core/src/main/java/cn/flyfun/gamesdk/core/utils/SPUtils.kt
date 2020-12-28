package cn.flyfun.gamesdk.core.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import cn.flyfun.support.DateUtils
import java.util.*

/**
 * @author #Suyghur.
 * Created on 2020/12/3
 */
object SPUtils {

    private fun setDialogShowTimeByTypeId(activity: Activity, typeId: String) {
        val time = DateUtils.getDateTimeByStr(Date(), null)
        val sp = activity.getSharedPreferences("app_dialog_show_time_type_$typeId", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("time", time)
        editor.apply()
    }

    fun getDialogShowTimeByTypeId(activity: Activity, typeId: String): Boolean {
        val sp = activity.getSharedPreferences("app_dialog_show_time_type_$typeId", Context.MODE_PRIVATE)
        val time = sp.getString("time", "")
        if (TextUtils.isEmpty(time)) {
            //写入时间
            setDialogShowTimeByTypeId(activity, typeId)
            return true
        }

        val lastTime = DateUtils.getStrByDataTime(time, "")
        val nowTime = Date()
        if (lastTime.day != nowTime.day) {
            //写入时间
            setDialogShowTimeByTypeId(activity, typeId)
            return true
        }
        return false
    }

    fun saveCacheOrder(context: Context, orderId: String) {
        val sp = context.getSharedPreferences("app_cache_order", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("order_info", orderId)
        editor.apply()
    }

    fun getCacheOrder(context: Context): String {
        val sp = context.getSharedPreferences("app_cache_order", Context.MODE_PRIVATE)
        return sp.getString("order_info", "") + ""
    }
}