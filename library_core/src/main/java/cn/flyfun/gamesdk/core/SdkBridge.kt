package cn.flyfun.gamesdk.core

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import cn.flyfun.gamesdk.Version
import cn.flyfun.gamesdk.base.entity.GameChargeInfo
import cn.flyfun.gamesdk.base.entity.GameRoleInfo
import cn.flyfun.gamesdk.base.internal.ICallback
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.impl.SdkBridgeImpl
import cn.flyfun.gamesdk.core.network.Host
import cn.flyfun.gamesdk.core.utils.NTools
import cn.flyfun.support.ResUtils
import cn.flyfun.support.device.DeviceInfoUtils
import cn.flyfun.support.jarvis.OwnDebugUtils
import cn.flyfun.support.jarvis.Toast


/**
 * @author #Suyghur.
 * Created on 2020/12/1
 */
class SdkBridge constructor(context: Context) {

    private var mActivity: Activity? = null
    private var mImpl: SdkBridgeImpl? = null

    init {
        Host.initHostModel(context)
        Logger.debug = OwnDebugUtils.isOwnDebug(context)
        NTools.init(context)
        if (mImpl == null) {
            mImpl = SdkBridgeImpl()
        }
    }

    fun attachBaseContext(application: Application, context: Context) {
        mImpl?.attachBaseContext(application, context)
    }

    fun initApplication(application: Application) {
        mImpl?.initApplication(application)
    }


    fun initialize(activity: Activity, isLandScape: Boolean, callback: ICallback) {
        this.mActivity = activity
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        val networkChangeReceiver = NetworkChangeReceiver()
        activity.registerReceiver(networkChangeReceiver, intentFilter)
        mImpl?.initialize(activity, isLandScape, callback)
    }

    fun login(activity: Activity, isAutoLogin: Boolean, callback: ICallback) {
        this.mActivity = activity
        mImpl?.login(activity, isAutoLogin, callback)
    }

    fun logout(activity: Activity, callback: ICallback) {
        this.mActivity = activity
        mImpl?.logout(activity, callback)
    }

    fun charge(activity: Activity, chargeInfo: GameChargeInfo, callback: ICallback) {
        this.mActivity = activity
        mImpl?.charge(activity, chargeInfo, callback)
    }

    fun roleCreate(activity: Activity, roleInfo: GameRoleInfo) {
        this.mActivity = activity
        mImpl?.roleCreate(activity, roleInfo)
    }

    fun roleLauncher(activity: Activity, roleInfo: GameRoleInfo) {
        this.mActivity = activity
        mImpl?.roleLauncher(activity, roleInfo)
    }

    fun roleUpgrade(activity: Activity, roleInfo: GameRoleInfo) {
        this.mActivity = activity
        mImpl?.roleUpgrade(activity, roleInfo)
    }

    fun openExitView(activity: Activity, callback: ICallback) {
        this.mActivity = activity
        mImpl?.openExitView(activity, callback)

    }

    fun openBindAccount(activity: Activity, callback: ICallback) {
        this.mActivity = activity
        mImpl?.openBindAccount(activity, callback)

    }

    fun openGmCenter(activity: Activity, callback: ICallback) {
        this.mActivity = activity
        mImpl?.openGmCenter(activity, callback)
    }

    fun onStart(activity: Activity) {
        this.mActivity = activity
        mImpl?.onStart(activity)

    }

    fun onResume(activity: Activity) {
        this.mActivity = activity
        mImpl?.onResume(activity)
    }


    fun onRestart(activity: Activity) {
        this.mActivity = activity
        mImpl?.onRestart(activity)
    }

    fun onPause(activity: Activity) {
        this.mActivity = activity
        mImpl?.onPause(activity)
    }

    fun onStop(activity: Activity) {
        this.mActivity = activity
        mImpl?.onStop(activity)
    }

    fun onDestroy(activity: Activity) {
        this.mActivity = activity
        mImpl?.onDestroy(activity)
    }

    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        this.mActivity = activity
        mImpl?.onActivityResult(activity, requestCode, resultCode, data)
    }

    fun onNewIntent(activity: Activity, intent: Intent) {
        this.mActivity = activity
        mImpl?.onNewIntent(activity, intent)
    }

    fun onConfigurationChanged(activity: Activity, newConfig: Configuration) {
        this.mActivity = activity
        mImpl?.onConfigurationChanged(activity, newConfig)
    }

    fun onRequestPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        this.mActivity = activity
        mImpl?.onRequestPermissionsResult(activity, requestCode, permissions, grantResults)
    }

    fun getCurrentSdkVersion(): String {
        return Version.CORE_VERSION_NAME
    }

    fun getCurrentUserId(): String {
        mImpl?.let {
            return it.getCurrentUserId()
        }
        return ""
    }

    fun hasBindAccount(): Boolean {
        mImpl?.let {
            return it.hasBindAccount()
        }
        return false
    }

    fun isGmCenterEnable(): Boolean {
        mImpl?.let {
            return it.isGmCenterEnable()
        }
        return false
    }

    private inner class NetworkChangeReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            context?.apply {
                if (!DeviceInfoUtils.isNetworkConnected(this)) {
                    Toast.toastInfo(context, ResUtils.getResString(this, "ffg_network_unavailable"))
                }
            }
        }

    }

}