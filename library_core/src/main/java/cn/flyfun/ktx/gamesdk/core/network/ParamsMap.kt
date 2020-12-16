package cn.flyfun.ktx.gamesdk.core.network

import android.content.Context
import android.os.Build
import android.text.TextUtils
import cn.flyfun.ktx.gamesdk.base.utils.Logger
import cn.flyfun.ktx.gamesdk.base.utils.ParamsUtils
import cn.flyfun.ktx.gamesdk.core.Version
import cn.flyfun.support.AppUtils
import cn.flyfun.support.LocaleUtils
import cn.flyfun.support.device.DeviceInfoUtils


/**
 * @author #Suyghur.
 * Created on 2020/12/2
 */
object ParamsMap {

    private var paramsMap: HashMap<String, Any>? = null
    private var isDefaultInit = false

    @Synchronized
    fun init(context: Context) {
        if (isDefaultInit) {
            return
        }
        Logger.d("FuseParamMap init ...")
        isDefaultInit = true
        if (paramsMap == null) {
            paramsMap = HashMap()
        }
        paramsMap?.apply {
            //biz
            this["game_code"] = ParamsUtils.getGameCode(context)
            this["package_name"] = context.packageName

            //vers
            this["server_version"] = Version.SERVER_VERSION_NAME
            this["client_version"] = Version.CORE_VERSION_NAME
            this["game_version_code"] = AppUtils.getVersionCode(context)
            this["game_version_name"] = AppUtils.getVersionName(context)

            //device
            this["local_language"] = LocaleUtils.getLocaleCountry(context)
            this["simulator"] = DeviceInfoUtils.isEmulator(context)
            this["imei"] = DeviceInfoUtils.getImei(context)
            this["android_id"] = DeviceInfoUtils.getAndroidDeviceId(context)
            this["network"] = DeviceInfoUtils.getNet(context)
            this["os"] = "android"
            this["os_version"] = Build.VERSION.RELEASE
            this["model"] = encode(DeviceInfoUtils.getModel())
            this["mfrs"] = DeviceInfoUtils.getManufacturer()
            this["mobile_brand"] = DeviceInfoUtils.getDeviceBrand()


        }
    }

    fun get(key: String): String {
        paramsMap?.let {
            return if (it.containsKey(key)) {
                it[key].toString()
            } else {
                ""
            }
        }
        return ""
    }

    fun put(key: String, any: Any) {
        if (paramsMap == null) {
            paramsMap = HashMap()
        }
        paramsMap?.apply {
            this[key] = any
        }
    }

    fun containsKey(key: String): Boolean {
        paramsMap?.let {
            return it.containsKey(key)
        }
        return false
    }

    private fun encode(realString: String): String {
        return if (TextUtils.isEmpty(realString)) {
            "none"
        } else {
            realString
        }
    }

}