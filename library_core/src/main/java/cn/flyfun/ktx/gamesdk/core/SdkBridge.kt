package cn.flyfun.ktx.gamesdk.core

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import cn.flyfun.ktx.gamesdk.base.entity.GameChargeInfo
import cn.flyfun.ktx.gamesdk.base.entity.GameRoleInfo
import cn.flyfun.ktx.gamesdk.base.inter.ICallback
import cn.flyfun.ktx.gamesdk.base.utils.Logger
import cn.flyfun.ktx.gamesdk.core.impl.SdkBridgeImpl
import cn.flyfun.ktx.gamesdk.core.network.Host
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
        if (mImpl == null) {
            mImpl = SdkBridgeImpl()
        }
    }

    fun attachBaseContext(application: Application, context: Context) {
        mImpl?.apply {
            attachBaseContext(application, context)
        }
    }

    fun initApplication(application: Application) {
        mImpl?.apply {
            initApplication(application)
        }
    }


    fun initialize(activity: Activity, isLandScape: Boolean, callback: ICallback) {
        this.mActivity = activity
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        val networkChangeReceiver = NetworkChangeReceiver()
        activity.registerReceiver(networkChangeReceiver, intentFilter)
        mImpl?.apply {
            initialize(activity, isLandScape, callback)
        }
    }

    fun login(activity: Activity, isAutoLogin: Boolean, callback: ICallback) {
        this.mActivity = activity
        mImpl?.apply {
            login(activity, isAutoLogin, callback)
        }
    }

    fun logout(activity: Activity, callback: ICallback) {
        this.mActivity = activity
        mImpl?.apply {
            logout(activity, callback)
        }
    }

    fun charge(activity: Activity, chargeInfo: GameChargeInfo, callback: ICallback) {
        this.mActivity = activity
        mImpl?.apply {
            charge(activity, chargeInfo, callback)
        }
    }

    fun roleCreate(activity: Activity, roleInfo: GameRoleInfo) {
        this.mActivity = activity
        mImpl?.apply {
            roleCreate(activity, roleInfo)
        }
    }

    fun roleLauncher(activity: Activity, roleInfo: GameRoleInfo) {
        this.mActivity = activity
        mImpl?.apply {
            roleLauncher(activity, roleInfo)
        }
    }

    fun roleUpgrade(activity: Activity, roleInfo: GameRoleInfo) {
        this.mActivity = activity
        mImpl?.apply {
            roleUpgrade(activity, roleInfo)
        }
    }

    fun showExitView(activity: Activity, callback: ICallback) {
        this.mActivity = activity
        mImpl?.apply {
            showExitView(activity, callback)
        }
    }

    fun bindPlatformAccount(activity: Activity, callback: ICallback) {
        this.mActivity = activity
        mImpl?.apply {
            bindPlatformAccount(activity, callback)
        }
    }

    fun openGmCenter(activity: Activity, callback: ICallback) {
        this.mActivity = activity
        mImpl?.apply {
            openGmCenter(activity, callback)
        }
    }

    fun onStart(activity: Activity) {
        this.mActivity = activity
        mImpl?.apply {
            onStart(activity)
        }
    }

    fun onResume(activity: Activity) {
        this.mActivity = activity
        mImpl?.apply {
            onResume(activity)
        }
    }

    fun onRestart(activity: Activity) {
        this.mActivity = activity
        mImpl?.apply {
            onRestart(activity)
        }
    }

    fun onPause(activity: Activity) {
        this.mActivity = activity
        mImpl?.apply {
            onPause(activity)
        }
    }

    fun onStop(activity: Activity) {
        this.mActivity = activity
        mImpl?.apply {
            onStop(activity)
        }
    }

    fun onDestroy(activity: Activity) {
        this.mActivity = activity
        mImpl?.apply {
            onDestroy(activity)
        }
    }

    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        this.mActivity = activity
        mImpl?.apply {
            onActivityResult(activity, requestCode, resultCode, data)
        }
    }

    fun onNewIntent(activity: Activity, intent: Intent) {
        this.mActivity = activity
        mImpl?.apply {
            onNewIntent(activity, intent)
        }
    }

    fun onConfigurationChanged(activity: Activity, newConfig: Configuration) {
        this.mActivity = activity
        mImpl?.apply {
            onConfigurationChanged(activity, newConfig)
        }
    }

    fun onRequestPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        this.mActivity = activity
        mImpl?.apply {
            onRequestPermissionsResult(activity, requestCode, permissions, grantResults)
        }
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

    fun isBindPlatformAccount(): Boolean {
        mImpl?.let {
            return it.isBindPlatformAccount()
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