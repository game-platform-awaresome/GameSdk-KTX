package cn.flyfun.ktx.gamesdk.core.network

import android.content.Context
import cn.flyfun.ktx.gamesdk.base.entity.GameChargeInfo
import cn.flyfun.ktx.gamesdk.base.entity.GameRoleInfo
import cn.flyfun.ktx.gamesdk.core.inter.IRequestCallback
import org.json.JSONException
import org.json.JSONObject


/**
 * @author #Suyghur.
 * Created on 2020/12/2
 */
class SdkRequest {

    companion object {
        @JvmStatic
        fun getInstance(): SdkRequest {
            return RequestHolder.INSTANCE
        }
    }

    private object RequestHolder {
        val INSTANCE = SdkRequest()
    }

    fun initSdk(context: Context, callback: IRequestCallback) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("common", getCommon(context))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_INIT_SDK, jsonObject, callback)
    }

    fun userLoginVerify(context: Context, loginParams: JSONObject, callback: IRequestCallback) {
        try {
            loginParams.put("common", getCommon(context))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_USER_VERIFY, loginParams, callback)
    }

    fun userRegister(context: Context, userName: String, pwd: String, callback: IRequestCallback) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("common", getCommon(context))
            jsonObject.put("user_name", userName)
            jsonObject.put("pwd", pwd)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_USER_REGISTER, jsonObject, callback)
    }

    fun userBind(context: Context, bindParams: JSONObject, callback: IRequestCallback) {
        try {
            bindParams.put("common", getCommon(context))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_USER_BIND, bindParams, callback)
    }

    fun getCaptcha(context: Context, userName: String, callback: IRequestCallback) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("common", getCommon(context))
            jsonObject.put("user_name", userName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_GET_CAPTCHA, jsonObject, callback)
    }

    fun forgetPassword(context: Context, userName: String, pwd: String, code: String, callback: IRequestCallback) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("common", getCommon(context))
            jsonObject.put("user_name", userName)
            jsonObject.put("pwd", pwd)
            jsonObject.put("sms_code", code)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_FORGET_PASSWORD, jsonObject, callback)
    }

    fun getOrderId(context: Context, chargeInfo: GameChargeInfo, callback: IRequestCallback) {
        val jsonObject = assembleChargeParams(chargeInfo)
        try {
            jsonObject.put("common", getCommon(context))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_GET_ORDER_ID, jsonObject, callback)
    }

    fun notifyOrder(context: Context, orderId: String, originalJson: String, signature: String, callback: IRequestCallback) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("common", getCommon(context))
            jsonObject.put("order_id", orderId)
            jsonObject.put("third_plat_content", originalJson)
            jsonObject.put("third_plat_sign", signature)
            //iOS交易编号，置空
            jsonObject.put("transaction_id", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_NOTIFY_ORDER, jsonObject, callback)
    }

    fun submitRoleData(context: Context, roleInfo: GameRoleInfo, callback: IRequestCallback) {
        val jsonObject = assembleRoleParams(roleInfo)
        try {
            jsonObject.put("common", getCommon(context))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        VolleyRequest.post(context, Host.BASIC_URL_SUBMIT_ROLE, jsonObject, callback)
    }

    private fun getCommon(context: Context): JSONObject {
        ParamsMap.init(context)
        val jsonCommon = JSONObject()
        try {
            val jsonBiz = JSONObject()
            jsonBiz.put("game_code", ParamsMap.get("game_code"))
            jsonBiz.put("package_name", context.packageName)
            jsonCommon.put("biz", jsonBiz)

            val jsonVers = JSONObject()
            jsonVers.put("server_version", ParamsMap.get("server_version"))
            jsonVers.put("client_version", ParamsMap.get("client_version"))
            jsonVers.put("game_version_code", ParamsMap.get("game_version_code"))
            jsonVers.put("game_version_name", ParamsMap.get("game_version_name"))
            jsonCommon.put("vers", jsonVers)

            val jsonDevice = JSONObject()
            jsonDevice.put("local_language", ParamsMap.get("local_language"))
            jsonDevice.put("screen", ParamsMap.get("screen"))
            jsonDevice.put("simulator", ParamsMap.get("simulator"))
            jsonDevice.put("imei", ParamsMap.get("imei"))
            jsonDevice.put("device_id", ParamsMap.get("device_id"))
            jsonDevice.put("mac", ParamsMap.get("mac"))
            jsonDevice.put("adid", ParamsMap.get("adid"))
            jsonDevice.put("android_id", ParamsMap.get("android_id"))
            jsonDevice.put("idfa", "")
            jsonDevice.put("idfv", "")
            jsonDevice.put("network", ParamsMap.get("network"))
            jsonDevice.put("os", ParamsMap.get("os"))
            jsonDevice.put("os_version", ParamsMap.get("os_version"))
            jsonDevice.put("model", ParamsMap.get("model"))
            jsonDevice.put("mfrs", ParamsMap.get("mfrs"))
            jsonDevice.put("mobile_brand", ParamsMap.get("mobile_brand"))
            jsonCommon.put("device", jsonDevice)
            jsonCommon.put("ext", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonCommon
    }

    private fun assembleChargeParams(chargeInfo: GameChargeInfo): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", chargeInfo.userId)
            jsonObject.put("server_code", chargeInfo.serverCode)
            jsonObject.put("server_name", chargeInfo.serverName)
            jsonObject.put("role_name", chargeInfo.roleName)
            jsonObject.put("role_id", chargeInfo.roleId)
            jsonObject.put("role_level", chargeInfo.roleLevel)
            jsonObject.put("price", chargeInfo.price.toString())
            jsonObject.put("product_id", chargeInfo.productId)
            jsonObject.put("product_name", chargeInfo.productName)
            jsonObject.put("product_desc", chargeInfo.productDesc)
            jsonObject.put("cp_order_id", chargeInfo.cpOrderId)
            jsonObject.put("cp_notify_url", chargeInfo.cpNotifyUrl)
            jsonObject.put("cp_callback_info", chargeInfo.cpCallbackInfo)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    private fun assembleRoleParams(roleInfo: GameRoleInfo): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("user_id", roleInfo.userId)
            jsonObject.put("server_code", roleInfo.serverCode)
            jsonObject.put("server_name", roleInfo.serverName)
            jsonObject.put("role_name", roleInfo.roleName)
            jsonObject.put("role_id", roleInfo.roleId)
            jsonObject.put("role_level", roleInfo.roleLevel)
            jsonObject.put("vip_level", roleInfo.vipLevel)
            jsonObject.put("balance", roleInfo.balance)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

}