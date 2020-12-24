package cn.flyfun.gamesdk.core.utils

import android.content.Context
import cn.flyfun.gamesdk.Version
import cn.flyfun.gamesdk.core.entity.N2JType

/**
 * @author #Suyghur.
 * Created on 2020/12/24
 */
object NTools {

    init {
        System.loadLibrary("flyfun_boost")
    }

    external fun init(context: Context)

    external fun putParam(key: String, value: String)

    external fun invokeFuseJob(context: Context, url: String, data: String): String

    external fun parseFuseJob(context: Context, data: String): String

    @JvmStatic
    fun n2j(context: Context, methodId: Int): String {
        var result = ""
        when (methodId) {
            N2JType.METHOD_GET_SERVER_VERSION -> result = Version.SERVER_VERSION_NAME
            N2JType.METHOD_GET_CLIENT_VERSION -> result = Version.CORE_VERSION_NAME
        }
        return result
    }
}