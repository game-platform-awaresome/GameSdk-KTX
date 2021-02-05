package cn.flyfun.gamesdk.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.webkit.WebView
import androidx.annotation.Keep
import cn.flyfun.gamesdk.base.entity.FunctionName
import cn.flyfun.gamesdk.base.entity.GameChargeInfo
import cn.flyfun.gamesdk.base.entity.GameRoleInfo
import cn.flyfun.gamesdk.base.internal.ICallback
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.support.AppUtils


/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
class FlyFunGame private constructor() {

    private var mSdkBridge: Any? = null

    private var processName: String = ""

    private var clickLoginTime: Long = 0
    private var clickLogoutTime: Long = 0
    private var clickChargeTime: Long = 0

    private var isSdkInit: Boolean = false


    /**
     * 同步Application中的attachBaseContext
     *
     * @param application Application上下文对象
     * @param context     Context上下文对象
     */
    fun attachBaseContext(application: Application, context: Context) {
        if (TextUtils.isEmpty(processName)) {
            this.processName = AppUtils.getProcessName(context)
        }
        if (TextUtils.isEmpty(processName) || processName != context.packageName) {
            return
        }
        if (mSdkBridge == null) {
            mSdkBridge = SdkBridgeManager.getSdkBridgeManager(context)
        }
        SdkBridgeManager.call(FunctionName.ATTACH_BASE_CONTEXT, arrayOf(Application::class.java, Context::class.java), arrayOf(application, context))
    }

    /**
     * 同步Application中的onCreate
     *
     * @param application Application上下文对象
     */
    fun initApplication(application: Application) {
        if (TextUtils.isEmpty(processName)) {
            this.processName = AppUtils.getProcessName(application)
        }
        //处理Android P webView的坑
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                if (application.packageName != processName) {
                    Logger.d("Android P设置webView不同的目录")
                    WebView.setDataDirectorySuffix(processName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (TextUtils.isEmpty(processName) || processName != application.packageName) {
            return
        }
        if (mSdkBridge == null) {
            mSdkBridge = SdkBridgeManager.getSdkBridgeManager(application)
        }
        SdkBridgeManager.call(FunctionName.INIT_APPLICATION, arrayOf(Application::class.java), arrayOf(application))
    }

    /**
     * SDK初始化
     *
     * @param activity    Activity上下文
     * @param isLandscape 是否横屏
     * @param callback    SDK初始化回调
     */
    fun initialize(activity: Activity, isLandscape: Boolean, callback: ICallback) {
        if (mSdkBridge == null) {
            Logger.e("initialize error ... SdkBridgeManager is null")
            callback.onResult(-1, "初始化失败，同步Application的生命周期异常")
            return
        }
        //有些CP自己请求了权限，导致游戏回调2次，执行了两次initialize，这里拦截一下
        if (isSdkInit) {
            Logger.e("FlyFunGameSdk initFuseSdk 已经执行，拦截此次调用")
            return
        }
        isSdkInit = true
        SdkBridgeManager.call(FunctionName.INITIALIZE, arrayOf(Activity::class.java, Boolean::class.java, ICallback::class.java), arrayOf(activity, isLandscape, callback))
    }

    /**
     * SDK用户登录
     *
     * @param activity Activity上下文
     * @param isAuto   是否自动登录
     * @param callback 登录回调对象
     */
    fun login(activity: Activity, isAuto: Boolean, callback: ICallback) {
        if (mSdkBridge == null) {
            Logger.e("login error ... SdkBridgeManager is null")
            return
        }
        if (System.currentTimeMillis() - clickLoginTime < CLICK_INTERVAL) {
            Logger.e("调用登录接口太频繁")
            return
        }
        clickLoginTime = System.currentTimeMillis()
        SdkBridgeManager.call(FunctionName.LOGIN, arrayOf(Activity::class.java, Boolean::class.java, ICallback::class.java), arrayOf(activity, isAuto, callback))
    }

    /**
     * SDK用户登出账号
     *
     * @param activity Activity上下文
     * @param callback 登出回调对象
     */
    fun logout(activity: Activity, callback: ICallback) {
        if (mSdkBridge == null) {
            Logger.e("logout error ... SdkBridgeManager is null")
            return
        }

        if (System.currentTimeMillis() - clickLoginTime < CLICK_INTERVAL) {
            Logger.e("调用登录接口后，调用登出账号接口间隔太短")
            return
        }
        if (System.currentTimeMillis() - clickLogoutTime < CLICK_INTERVAL) {
            Logger.e("调用登出账号接口太频繁")
            return
        }
        clickLogoutTime = System.currentTimeMillis()
        SdkBridgeManager.call(FunctionName.LOGOUT, arrayOf(Activity::class.java, ICallback::class.java), arrayOf(activity, callback))
    }

    /**
     * SDK用户支付
     *
     * @param activity   Activity上下文
     * @param chargeInfo 支付信息实体对象
     * @param callback   支付回调对象
     */
    fun charge(activity: Activity, chargeInfo: GameChargeInfo, callback: ICallback) {
        if (mSdkBridge == null) {
            Logger.e("charge error ... SdkBridgeManager is null")
            return
        }

        if (System.currentTimeMillis() - clickChargeTime < CLICK_INTERVAL) {
            Logger.e("调用充值接口太频繁")
            return
        }
        clickChargeTime = System.currentTimeMillis()
        SdkBridgeManager.call(FunctionName.CHARGE, arrayOf(Activity::class.java, GameChargeInfo::class.java, ICallback::class.java), arrayOf(activity, chargeInfo, callback))
    }

    /**
     * SDK角色创建信息上报
     *
     * @param activity Activity上下文
     * @param roleInfo 角色信息实体
     */
    fun roleCreate(activity: Activity, roleInfo: GameRoleInfo) {
        if (mSdkBridge == null) {
            Logger.e("roleCreate error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.ROLE_CREATE, arrayOf(Activity::class.java, GameRoleInfo::class.java), arrayOf(activity, roleInfo))
    }

    /**
     * SDK角色登录信息上报
     *
     * @param activity Activity上下文
     * @param roleInfo 角色信息实体
     */
    fun roleLauncher(activity: Activity, roleInfo: GameRoleInfo) {
        if (mSdkBridge == null) {
            Logger.e("roleLauncher error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.ROLE_LAUNCHER, arrayOf(Activity::class.java, GameRoleInfo::class.java), arrayOf(activity, roleInfo))
    }

    /**
     * SDK角色升级信息上报
     *
     * @param activity Activity上下文
     * @param roleInfo 角色信息实体
     */
    fun roleUpgrade(activity: Activity, roleInfo: GameRoleInfo) {
        if (mSdkBridge == null) {
            Logger.e("roleUpgrade error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.ROLE_UPGRADE, arrayOf(Activity::class.java, GameRoleInfo::class.java), arrayOf(activity, roleInfo))
    }

    /**
     * 显示退出框
     *
     * @param activity
     */
    fun openExitView(activity: Activity, callback: ICallback) {
        if (mSdkBridge == null) {
            Logger.e("openExitView error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.OPEN_EXIT_VIEW, arrayOf(Activity::class.java, ICallback::class.java), arrayOf(activity, callback))
    }

    /**
     * 显示绑定平台账号页面
     *
     * @param activity Activity上下文
     * @param callback 绑定回调对象
     */
    fun openBindAccount(activity: Activity, callback: ICallback) {
        if (mSdkBridge == null) {
            Logger.e("openBindAccount error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.OPEN_BIND_ACCOUNT, arrayOf(Activity::class.java, ICallback::class.java), arrayOf(activity, callback))
    }

    /**
     * 跳转到客服中心
     *
     * @param activity Activity上下文
     * @param callback 客服回调对象（预留）
     */
    fun openGmCenter(activity: Activity, callback: ICallback) {
        if (mSdkBridge == null) {
            Logger.e("openGmCenter error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.OPEN_GM_CENTER, arrayOf(Activity::class.java, ICallback::class.java), arrayOf(activity, callback))
    }

    /**
     * 同步游戏Activity的onStart
     *
     * @param activity
     */
    fun onStart(activity: Activity) {
        if (mSdkBridge == null) {
            Logger.e("onStart error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.ON_START, arrayOf(Activity::class.java), arrayOf(activity))
    }

    /**
     * 同步游戏Activity的onResume
     *
     * @param activity
     */
    fun onResume(activity: Activity) {
        if (mSdkBridge == null) {
            Logger.e("onResume error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.ON_RESUME, arrayOf(Activity::class.java), arrayOf(activity))
    }

    /**
     * 同步游戏Activity的onRestart
     *
     * @param activity
     */
    fun onRestart(activity: Activity) {
        if (mSdkBridge == null) {
            Logger.e("onRestart error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.ON_RESTART, arrayOf(Activity::class.java), arrayOf(activity))
    }

    /**
     * 同步游戏Activity的onPause
     *
     * @param activity
     */
    fun onPause(activity: Activity) {
        if (mSdkBridge == null) {
            Logger.e("onPause error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.ON_PAUSE, arrayOf(Activity::class.java), arrayOf(activity))
    }

    /**
     * 同步游戏Activity的onStop
     *
     * @param activity
     */
    fun onStop(activity: Activity) {
        if (mSdkBridge == null) {
            Logger.e("onStop error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.ON_STOP, arrayOf(Activity::class.java), arrayOf(activity))
    }

    /**
     * 同步游戏Activity的onDestroy
     *
     * @param activity
     */
    fun onDestroy(activity: Activity) {
        if (mSdkBridge == null) {
            Logger.e("onDestroy error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.ON_DESTROY, arrayOf(Activity::class.java), arrayOf(activity))
    }

    /**
     * 同步游戏Activity的onActivityResult
     *
     * @param activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        if (mSdkBridge == null) {
            Logger.e("onDestroy error ... SdkBridgeManager is null")
            return
        }
        data?.apply {
            SdkBridgeManager.call(FunctionName.ON_ACTIVITY_RESULT, arrayOf(Activity::class.java, Int::class.java, Int::class.java, Intent::class.java), arrayOf(activity, requestCode, resultCode, this))
        }
    }

    /**
     * 同步游戏Activity的onNewIntent
     *
     * @param activity
     * @param intent
     */
    fun onNewIntent(activity: Activity, intent: Intent?) {
        if (mSdkBridge == null) {
            Logger.e("onNewIntent error ... SdkBridgeManager is null")
            return
        }
        intent?.apply {
            SdkBridgeManager.call(FunctionName.ON_NEW_INTENT, arrayOf(Activity::class.java, Intent::class.java), arrayOf(activity, this))
        }
    }

    /**
     * 同步游戏Activity的onConfigurationChanged
     *
     * @param activity
     * @param newConfig
     */
    fun onConfigurationChanged(activity: Activity, newConfig: Configuration) {
        if (mSdkBridge == null) {
            Logger.e("onConfigurationChanged error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.ON_CONFIGURATION_CHANGED, arrayOf(Activity::class.java, Configuration::class.java), arrayOf(activity, newConfig))
    }

    /**
     * 同步游戏Activity的onRequestPermissionsResult
     *
     * @param activity
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    fun onRequestPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (mSdkBridge == null) {
            Logger.e("onRequestPermissionsResult error ... SdkBridgeManager is null")
            return
        }
        SdkBridgeManager.call(FunctionName.ON_REQUEST_PERMISSIONS_RESULT, arrayOf(Activity::class.java, Int::class.java, Array<out String>::class.java, Array<Int>::class.java), arrayOf(activity, requestCode, permissions, grantResults))
    }

    /**
     * 获取当前用户ID
     *
     * @return
     */
    fun getCurrentUserId(): String {
        if (mSdkBridge == null) {
            Logger.e("getCurrentUserId error ... SdkBridgeManager is null")
            return ""
        }
        return SdkBridgeManager.callback(FunctionName.GET_CURRENT_USER_ID, emptyArray(), emptyArray()) as String
    }


    /**
     * 当前用户是否已绑定平台账号
     *
     * @return
     */
    fun hasBindAccount(): Boolean {
        if (mSdkBridge == null) {
            Logger.e("hasBindAccount error ... SdkBridgeManager is null")
            return false
        }
        return SdkBridgeManager.callback(FunctionName.HAS_BIND_ACCOUNT, emptyArray(), emptyArray()) as Boolean
    }

    /**
     * 客服中心是否可用
     *
     * @return
     */
    fun isGmCenterEnable(): Boolean {
        if (mSdkBridge == null) {
            Logger.e("isGmCenterEnable error ... SdkBridgeManager is null")
            return false
        }
        return SdkBridgeManager.callback(FunctionName.IS_GM_CENTER_ENABLE, emptyArray(), emptyArray()) as Boolean
    }

    /**
     * 获取当前SDK版本
     *
     * @return
     */
    fun getCurrentSdkVersion(): String {
        if (mSdkBridge == null) {
            Logger.e("getCurrentSdkVersion error ... SdkBridgeManager is null")
            return ""
        }
        return SdkBridgeManager.callback(FunctionName.GET_CURRENT_SDK_VERSION, emptyArray(), emptyArray()) as String
    }

    fun logHandler(handler: Handler?) {
        Logger.handler = handler
    }

    companion object {
        @JvmStatic
        @Keep
        fun getInstance(): FlyFunGame {
            return FlyFunGameHolder.INSTANCE
        }

        private const val CLICK_INTERVAL: Int = 1500

        private object FlyFunGameHolder {
            val INSTANCE: FlyFunGame = FlyFunGame()
        }
    }
}