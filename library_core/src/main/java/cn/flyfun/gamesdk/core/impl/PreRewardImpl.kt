package cn.flyfun.gamesdk.core.impl

import android.app.Activity
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.entity.GameRewardInfo
import cn.flyfun.gamesdk.core.entity.ResultInfo
import cn.flyfun.gamesdk.core.internal.IRequestCallback
import cn.flyfun.gamesdk.core.network.SdkRequest
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

/**
 * @author #Suyghur,
 * Created on 2021/3/18
 * 1、创角时拉起实现
 * 2、连接Google Play
 * 3、查询PlayStore是否有存在预注册奖励产品id的订单
 * 4、服务端校验购买结果并通知CP发货
 * 5、消耗订单
 */
class PreRewardImpl private constructor() {

    private var billingClient: BillingClient? = null
    private var rewardInfo: GameRewardInfo? = null

    fun checkPreReward(activity: Activity, rewardInfo: GameRewardInfo) {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity) != ConnectionResult.SUCCESS) {
            Logger.e("查询预注册奖励失败，谷歌Iab支付服务不可用")
            return
        }
        this.rewardInfo = rewardInfo
        initializeBillingClient(activity)
    }

    /**
     * 初始化IAB
     *
     * @param activity
     */
    private fun initializeBillingClient(activity: Activity) {
        billingClient = BillingClient.newBuilder(activity).setListener { billingResult, list ->
            //谷歌支付结果在这里回调
            logBillingResult("onPurchasesUpdated", billingResult)
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (list != null) {
                    disConnection()
                }
                list?.apply {
                    if (size > 0) {
                        for (reward in this) {
                            if (reward.sku == rewardInfo!!.rewardId)
                                notifyReward2Backend(activity)
                        }
                    }
                }
            } else {
                //没有预注册奖励
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
                Logger.d("start connection Google Play")
                startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        logBillingResult("onBullingSetupFinished", billingResult)
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            Logger.d("connection Google Play success")
                            queryReward(activity)
                        } else {
                            Logger.e("连接Google Play异常")
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

    private fun queryReward(activity: Activity) {
        billingClient?.apply {
            val list = queryPurchases(BillingClient.SkuType.INAPP).purchasesList
            list?.apply {
                if (size > 0) {
                    Logger.d("查询预注册奖励状态")
                    for (reward in this) {
                        Logger.d("reward : $reward")
                        if (rewardInfo?.rewardId == reward.sku) {
                            //通知发货
                            rewardInfo?.purchaseToken = reward.purchaseToken
                            notifyReward2Backend(activity)
                        } else {
                            Logger.d("不是预注册奖励订单，断开连接")
                            disConnection()
                            return
                        }
                    }
                }
            }
        }
    }

    private fun notifyReward2Backend(activity: Activity) {
        SdkRequest.getInstance().notifyReward(activity, rewardInfo!!, object : IRequestCallback {
            override fun onResponse(resultInfo: ResultInfo) {
                rewardInfo!!.purchaseToken?.apply {
                    consumeAsync(this)
                }
            }
        })
    }

    private fun consumeAsync(purchaseToken: String) {
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build()
        billingClient?.apply {
            consumeAsync(consumeParams) { billingResult, _ ->
                logBillingResult("onConsumeResponse", billingResult)
                disConnection()
            }
        }
    }

    private fun disConnection() {
        billingClient?.apply {
            if (isReady) {
                Logger.d("断开谷歌收银台连接")
                rewardInfo = null
                endConnection()
            }
        }
    }

    private fun logBillingResult(callbackFunName: String, billingResult: BillingResult) {
        val code = billingResult.responseCode
        val msg = billingResult.debugMessage
        Logger.d("$callbackFunName code : $code , msg : $msg")
    }

    companion object {
        fun getInstance(): PreRewardImpl {
            return PreRegisterImplHolder.INSTANCE
        }

        private object PreRegisterImplHolder {
            val INSTANCE = PreRewardImpl()
        }
    }
}