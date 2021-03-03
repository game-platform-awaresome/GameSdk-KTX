package cn.flyfun.gamesdk.core.impl

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import cn.flyfun.gamesdk.base.entity.GameChargeInfo
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.entity.ResultInfo
import cn.flyfun.gamesdk.core.internal.IRequestCallback
import cn.flyfun.gamesdk.core.internal.ImplCallback
import cn.flyfun.gamesdk.core.network.SdkRequest
import cn.flyfun.gamesdk.core.ui.DialogUtils
import cn.flyfun.gamesdk.core.ui.dialog.ScaleLoadingDialog
import cn.flyfun.gamesdk.core.utils.SPUtils
import cn.flyfun.support.JsonUtils
import cn.flyfun.support.ResUtils
import cn.flyfun.support.jarvis.Toast
import cn.flyfun.support.ui.circleprogress.CircleProgressLoadingDialog
import com.android.billingclient.api.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import org.json.JSONException
import org.json.JSONObject


/**
 * @author #Suyghur.
 * Created on 2020/12/3
 * 1、检查是否存在未发货的订单
 * 2、生成我们的订单号
 * 3、链接Google Play
 * 4、检查商品
 * 5、购买
 * 6、服务端校验购买结果并通知CP发货
 * 7、消耗该笔订单
 */
class ChargeImpl private constructor() {

    companion object {
        val instance: ChargeImpl by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ChargeImpl()
        }
    }

    private var payLoadingDialog: ScaleLoadingDialog? = null
    private var callback: ImplCallback? = null

    private var chargeInfo: GameChargeInfo? = null
    private var billingClient: BillingClient? = null

    fun invokeCharge(activity: Activity, chargeInfo: GameChargeInfo, callback: ImplCallback) {
        this.chargeInfo = chargeInfo
        this.callback = callback
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity) != ConnectionResult.SUCCESS) {
            Toast.toastInfo(activity, "Your phone or Google account does not support In-app Billing")
            callback.onFailed("谷歌Iab支付服务不可用")
            return
        }
        dismissDialog()
        payLoadingDialog = DialogUtils.showScaleLoadingDialog(activity, ResUtils.getResString(activity, "ffg_charge_loading_tips"))
        payLoadingDialog?.show()
        getOrderId(activity)
    }

    /**
     * 获取平台订单号
     *
     * @param activity   Activity上下文对象
     */
    private fun getOrderId(activity: Activity) {
        SdkRequest.getInstance().getOrderId(activity, chargeInfo!!, object : IRequestCallback {
            override fun onResponse(resultInfo: ResultInfo) {
                if (resultInfo.code == 0 && !TextUtils.isEmpty(resultInfo.data)) {
                    try {
                        val jsonObject = JSONObject(resultInfo.data)
                        chargeInfo!!.orderId = jsonObject.getString("order_id")
                        Logger.d("order_id ---> ${chargeInfo?.orderId}")
                        initializeBillingClient(activity)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        callback?.onFailed("获取订单异常")
                    }
                } else {
                    dismissDialog()
                    if (!TextUtils.isEmpty(resultInfo.msg)) {
                        Toast.toastInfo(activity, resultInfo.msg)
                    }
                    callback?.onFailed("获取订单失败")
                }
            }

        })

    }


    /**
     * 初始化IAB
     *
     * @param activity Activity上下文对象
     */
    private fun initializeBillingClient(activity: Activity) {
        billingClient = BillingClient.newBuilder(activity).setListener { billingResult, list ->
            //谷歌支付结果在这里回调
            logBillingResult("onPurchasesUpdated", billingResult)
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (list == null) {
                    callback?.onFailed("支付失败")
                    disConnection()
                }
                list?.apply {
                    if (size > 0) {
                        notifyOrder2Backend(activity, chargeInfo!!.orderId + "", this[0].originalJson, this[0].signature, false)
                    }
                }
            } else {
                callback?.onFailed("支付异常")
                disConnection()
            }
        }.enablePendingPurchases().build()
        connectGooglePlay(activity)
    }


    /**
     * 连接谷歌商店
     *
     * @param activity
     */
    private fun connectGooglePlay(activity: Activity) {
        billingClient?.apply {
            if (!isReady) {
                Logger.d("start connection Google Play ...")
                startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        logBillingResult("onBillingSetupFinished", billingResult)
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            queryPurchases(activity)
                        } else {
                            callback?.onFailed("连接Google Play服务异常")
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        Logger.e("onBillingServiceDisconnected")
                        connectGooglePlay(activity)
                    }

                })
            }
        }
    }

    /**
     * 查询未消耗订单
     *
     * @param activity
     */
    private fun queryPurchases(activity: Activity) {
        billingClient?.apply {
            val list = queryPurchases(BillingClient.SkuType.INAPP).purchasesList
            if (list == null) {
                //发起正常支付流程
                querySkuDetails(activity)
            }
            list?.apply {
                if (size > 0) {
                    Logger.d("存在未消耗的订单，发起补单流程")
                    //消耗完了再发起支付
                    try {
                        val cache = SPUtils.getCacheOrder(activity)
                        if (!TextUtils.isEmpty(cache)) {
                            var cacheOrderId = ""
                            var cacheOriginalJson = ""
                            val cacheJson = JSONObject(cache)
                            if (JsonUtils.hasJsonKey(cacheJson, "order_id")) {
                                cacheOrderId = cacheJson.getString("order_id")
                            }
                            if (JsonUtils.hasJsonKey(cacheJson, "original_json")) {
                                cacheOriginalJson = cacheJson.getString("original_json")
                            }
                            if (cacheOriginalJson == this[0].originalJson) {
                                notifyOrder2Backend(activity, cacheOrderId, cacheOriginalJson, this[0].signature, true)
                            } else {
                                notifyOrder2Backend(activity, cacheOrderId, this[0].originalJson, this[0].signature, true)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    //发起正常支付流程
                    querySkuDetails(activity)
                }
            }
        }

    }

    /**
     * 查询商品信息
     *
     * @param activity
     */
    private fun querySkuDetails(activity: Activity) {
        val skus = ArrayList<String>()
        skus.add(chargeInfo!!.productId + "")
        val params = SkuDetailsParams.newBuilder().setType(BillingClient.SkuType.INAPP).setSkusList(skus).build()
        billingClient?.apply {
            querySkuDetailsAsync(params, object : SkuDetailsResponseListener {
                override fun onSkuDetailsResponse(billingResult: BillingResult, list: MutableList<SkuDetails>?) {
                    logBillingResult("onSkuDetailsResponse", billingResult)
                    dismissDialog()
                    list?.apply {
                        if (size == 0) {
                            callback?.onFailed("查询计费点失败")
                            return
                        }
                        if (size == 1) {
                            val skuDetails = this[0]
                            Logger.d("product_id : " + skuDetails.sku)
                            Logger.d("price : " + skuDetails.price)
                            launchBillingFlow(activity, this[0])
                        }
                    }
                    if (list == null) {
                        callback?.onFailed("查询计费点失败")
                        return
                    }
                }
            })
        }
    }

    /**
     * 启动谷歌收银台
     *
     * @param activity
     * @param skuDetails
     */
    private fun launchBillingFlow(activity: Activity, skuDetails: SkuDetails) {
        billingClient?.apply {
            val billingResult = launchBillingFlow(activity, BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build())
            logBillingResult("launchBillingFlow", billingResult)
        }
    }

    /**
     * 消耗订单
     *
     * @param purchaseToken
     */
    private fun consumeAsync(activity: Activity, purchaseToken: String, isCache: Boolean) {
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build()
        billingClient?.apply {
            consumeAsync(consumeParams) { billingResult, _ ->
                logBillingResult("onConsumeResponse", billingResult)
                dismissDialog()
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    //消耗成功
                    if (isCache) {
                        querySkuDetails(activity)
                    } else {
                        callback?.onSuccess("支付成功")
                        disConnection()
                    }
                    saveOrder2Local(activity, "", "")
                } else {
                    Toast.toastInfo(activity, ResUtils.getResString(activity, "ffg_charge_tv_error"))
                    callback?.onFailed("消耗订单异常")
                    disConnection()
                }
            }
        }
    }

    private fun notifyOrder2Backend(activity: Activity, orderId: String, originalJson: String, signature: String, isCache: Boolean) {
        SdkRequest.getInstance().notifyOrder(activity, orderId, originalJson, signature, object : IRequestCallback {
            override fun onResponse(resultInfo: ResultInfo) {
                try {
                    saveOrder2Local(activity, orderId, originalJson)
                    val jsonObject = JSONObject(originalJson)
                    if (JsonUtils.hasJsonKey(jsonObject, "purchaseToken")) {
                        consumeAsync(activity, jsonObject.getString("purchaseToken"), isCache)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun disConnection() {
        billingClient?.apply {
            if (isReady) {
                Logger.d("断开谷歌收银台连接，以清空被消耗或者失败的缓存订单")
                endConnection()
            }
        }
    }

    private fun logBillingResult(callbackFuncName: String, billingResult: BillingResult) {
        val code = billingResult.responseCode
        val msg = billingResult.debugMessage
        Logger.d(callbackFuncName + "code : $code , msg : $msg")
    }

    private fun saveOrder2Local(context: Context, orderId: String, originalJson: String) {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("order_id", orderId)
            jsonObject.put("original_json", originalJson)
            Logger.d("saveOrder2Local : $jsonObject")
            SPUtils.saveCacheOrder(context, jsonObject.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun dismissDialog() {
        payLoadingDialog?.apply {
            if (isShowing) {
                dismiss()
                payLoadingDialog = null
            }
        }
    }
}