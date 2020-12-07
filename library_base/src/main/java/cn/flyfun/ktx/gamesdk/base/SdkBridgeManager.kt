package cn.flyfun.ktx.gamesdk.base

import android.content.Context
import java.lang.reflect.Method

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
object SdkBridgeManager {

    var sdkBridge: Any? = null

    var clzBridge: Class<*>? = null


    fun getSdkBridgeManager(context: Context?): Any? {
        if (null == sdkBridge) {
            sdkBridge = initSdkBridgeManager(context!!)
        }
        return sdkBridge
    }

    private fun initSdkBridgeManager(context: Context): Any? {
        if (sdkBridge != null) {
            return sdkBridge
        }
        try {
            clzBridge = Class.forName("cn.flyfun.ktx.gamesdk.core.SdkBridge")
            //获取构造函数的构造器
            val constructor = clzBridge!!.getDeclaredConstructor(Context::class.java)
            sdkBridge = constructor.newInstance(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sdkBridge
    }

    fun call(methodName: String, types: Array<Class<*>?>, values: Array<Any?>) {
        try {
            clzBridge?.apply {
                val method: Method = this.getMethod(methodName, *types)
                method.invoke(sdkBridge, *values)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun callback(methodName: String, types: Array<Class<*>?>, values: Array<Any?>): Any? {
        try {
            clzBridge?.let {
                val method: Method = it.getMethod(methodName, *types)
                return method.invoke(sdkBridge, *values)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }
}