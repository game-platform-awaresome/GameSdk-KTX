package cn.flyfun.gamesdk.core.utils

import android.content.Context

/**
 * @author #Suyghur.
 * Created on 2020/12/24
 */
object NTools {

    init {
        System.loadLibrary("flyfun_boost")
    }

    @JvmStatic
    external fun init(context: Context)

    @JvmStatic
    external fun putParam(key: String, value: String)

    @JvmStatic
    external fun getParam(key: String): String

    @JvmStatic
    external fun invokeFuseJob(context: Context, url: String, data: String): String

    @JvmStatic
    external fun parseFuseJob(context: Context, data: String): String
}