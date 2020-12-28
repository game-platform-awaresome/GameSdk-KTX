package cn.flyfun.gamesdk.core.impl

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.text.TextUtils
import cn.flyfun.gamesdk.base.entity.GameChargeInfo
import cn.flyfun.gamesdk.base.entity.GameRoleInfo
import cn.flyfun.gamesdk.base.inter.ICallback
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.base.utils.ParamsUtils
import cn.flyfun.gamesdk.core.apm.EventTraceImpl
import cn.flyfun.gamesdk.core.entity.ResultInfo
import cn.flyfun.gamesdk.core.entity.SdkBackLoginInfo
import cn.flyfun.gamesdk.core.entity.bean.InitBean
import cn.flyfun.gamesdk.core.impl.login.LoginActivity
import cn.flyfun.gamesdk.core.inter.IRequestCallback
import cn.flyfun.gamesdk.core.inter.ImplCallback
import cn.flyfun.gamesdk.core.network.SdkRequest
import cn.flyfun.gamesdk.core.ui.DialogUtils
import cn.flyfun.gamesdk.core.ui.dialog.InitDialog
import cn.flyfun.gamesdk.core.ui.dialog.TipsDialog
import cn.flyfun.gamesdk.core.utils.NTools
import cn.flyfun.gamesdk.core.utils.SPUtils
import cn.flyfun.support.BeanUtils
import cn.flyfun.support.DensityUtils
import cn.flyfun.support.device.DeviceInfoUtils
import cn.flyfun.support.encryption.Md5Utils
import cn.flyfun.support.gaid.GAIDUtils
import java.net.URLEncoder


/**
 * @author #Suyghur.
 * Created on 2020/12/2
 */
class SdkBridgeImpl {

    private var mActivity: Activity? = null
    private var roleInfo: GameRoleInfo? = null
    private var initNoticeDialog: InitDialog? = null
    private var exitTipsDialog: TipsDialog? = null

    private var initState = false

    // 是否有强更，默认有，为了拦截初始化后马上登录
    private var isShowInitDialog = true

    // 登录接口是否在休眠中，公告接口请求完成后，会自动发起登录
    private var isSleepLogin = false

    // 当前是否存在登录操作
    private var isExistLoginHandle = false

    private var isSubmitRoleData = false
    private var gameCode = ""

    fun attachBaseContext(application: Application, context: Context) {
        GAIDUtils.initGoogleAdid(application) { code: Int, _ ->
            if (code == 0) {
                Logger.d("谷歌框架可以访问，请求adid")
                NTools.putParam("device_id", GAIDUtils.getGoogleAdid())
                NTools.putParam("adid", GAIDUtils.getGoogleAdid())
            } else {
                Logger.e("谷歌框架不可以访问，使用android_id替代")
                Logger.e("谷歌框架不可以访问，Adjust上报使用SDK的aaid : " + EventTraceImpl.getInstance().getAaid())
                NTools.putParam("device_id", DeviceInfoUtils.getAndroidDeviceId(application))
                NTools.putParam("adid", DeviceInfoUtils.getAndroidDeviceId(application))
            }
        }
    }

    fun initApplication(application: Application) {
        EventTraceImpl.getInstance().onInitialize(application)
    }

    fun initialize(activity: Activity, isLand: Boolean, callback: ICallback) {
        this.mActivity = activity
        isLandscape = isLand
        this.gameCode = ParamsUtils.getGameCode(activity)
        if (TextUtils.isEmpty(gameCode)) {
            Logger.e("初始化失败，参数异常，请检查flyfun_cfg.properties中FLYFUN_GAME_CODE的值")
            callback.onResult(-1, "初始化失败，参数异常，请检查flyfun_cfg.properties中FLYFUN_GAME_CODE的值")
            return
        }

        if (TextUtils.isEmpty(ParamsUtils.getGoogleClientId(activity))) {
            Logger.e("初始化失败，参数异常，请检查flyfun_cfg.properties中FLYFUN_SERVER_CLIENT_ID的值")
            callback.onResult(-1, "初始化失败，参数异常，请检查flyfun_cfg.properties中FLYFUN_GOOGLE_CLIENT_ID的值")
            return
        }

        //获取当前屏幕尺寸
        NTools.putParam("screen", DensityUtils.getResolutionByFullScreen(activity))
        startSdkInit(activity, callback)
    }

    private fun startSdkInit(activity: Activity, callback: ICallback) {
        SdkRequest.getInstance().initSdk(activity, object : IRequestCallback {
            override fun onResponse(resultInfo: ResultInfo) {
                if (resultInfo.code == 0 && !TextUtils.isEmpty(resultInfo.data)) {
                    initBean = InitBean.toBean(resultInfo.data)
                    //检查公告配置
                    showInitNotice(activity, callback)
                } else {
                    callback.onResult(-1, "SDK初始化失败")
                    initState = false
                }
            }
        })
    }

    private fun showInitNotice(activity: Activity, callback: ICallback) {
        if (initBean?.initNotice != null && initBean!!.initNotice!!.noticeSwitch == 1) {
            try {
                if (mActivity?.isFinishing == false) {
                    val url = initBean!!.initNotice!!.url
                    val showCount = initBean!!.initNotice!!.showCount

                    //是否弹出公告
                    var isShowAppDialog = true
                    if (showCount == 0) {
                        isShowAppDialog = SPUtils.getDialogShowTimeByTypeId(activity, "#")
                    }
                    if (!isShowAppDialog) {
                        return
                    }

                    initNoticeDialog = DialogUtils.newInitNoticeDialog(activity, url) {
                        initState = true
                        isShowInitDialog = false
                        initNoticeDialog?.dismiss()
                        callback.onResult(0, "SDK初始化成功")
                    }

                    initNoticeDialog?.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Logger.e("公告解析出现异常")
                initState = true
                isShowInitDialog = false
                callback.onResult(0, "SDK初始化成功")
            }
        } else {
            //没有公告
            initState = true
            isShowInitDialog = false
            callback.onResult(0, "SDK初始化成功")
        }
    }

    fun login(activity: Activity, isAutoLogin: Boolean, callback: ICallback) {
        this.mActivity = activity
        if (!initState) {
            Logger.e("登录失败，SDK未初始化或初始化失败")
            callback.onResult(-1, "登录失败，SDK未初始化或初始化失败")
            return
        }
        if (isSleepLogin) {
            Logger.e("登录接口正在执行中，请稍等...")
            return
        }
        if (isExistLoginHandle) {
            Logger.e("登录接口已经存在队列中操作，请等待...")
            return
        }
        isExistLoginHandle = true
        if (isShowInitDialog) {
            Logger.e("登录调用过快或Dialog还未关闭，延迟登录，Dialog : $isShowInitDialog")
            callback.onResult(-1, "登录调用过快或Dialog还未关闭，延迟登录")
            if (!isSleepLogin) {
                isSleepLogin = true
                Thread {
                    while (isShowInitDialog) {
                        try {
                            Logger.e("公告还在执行，延迟2秒后自动登录，Dialog : $isShowInitDialog")
                            Thread.sleep(2000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                    isSleepLogin = false
                    isExistLoginHandle = false
                    activity.runOnUiThread {
                        Logger.e("公告接口执行完成，发起登录...")
                        login(activity, isAutoLogin, callback)
                    }
                }.start()
            }
            return
        }
        isExistLoginHandle = false
        SdkBackLoginInfo.instance.reset()
        LoginActivity.login(activity, isAutoLogin, object : ImplCallback {
            override fun onSuccess(result: String) {
                callback.onResult(0, result)
                if (SdkBackLoginInfo.instance.isRegUser == 1) {
                    EventTraceImpl.getInstance().onRegister()
                }
                EventTraceImpl.getInstance().onLogin()
            }

            override fun onFailed(result: String) {
                callback.onResult(-1, result)
            }

        })
    }

    fun logout(activity: Activity, callback: ICallback) {
        this.mActivity = activity
        this.roleInfo = null
        SdkBackLoginInfo.instance.reset()
        callback.onResult(0, "用户登出成功")
    }

    fun charge(activity: Activity, chargeInfo: GameChargeInfo, callback: ICallback) {
        this.mActivity = activity
        if (!initState) {
            Logger.e("支付失败，SDK未初始化或初始化失败")
            callback.onResult(-1, "支付失败，SDK未初始化或初始化失败")
            return
        }
        if (TextUtils.isEmpty(SdkBackLoginInfo.instance.userId)) {
            Logger.e("支付失败，用户未登录或登录失败")
            callback.onResult(-1, "支付失败，用户未登录或登录失败")
            return
        }
        if (!isSubmitRoleData) {
            Logger.e("支付失败，角色未登录或登录失败")
            callback.onResult(-1, "支付失败，角色未登录或登录失败")
            return
        }
        val innerChargeInfo = BeanUtils.deepClone(chargeInfo)
        if (innerChargeInfo == null) {
            Logger.e("支付失败，支付信息对象拷贝过程异常")
            callback.onResult(-1, "支付失败，支付信息对象拷贝过程异常")
            return
        }
        ChargeImpl.instance.invokeCharge(activity, innerChargeInfo, object : ImplCallback {
            override fun onSuccess(result: String) {
                val params = HashMap<String, Any>()
                params["order_id"] = innerChargeInfo.orderId.toString()
                params["price"] = innerChargeInfo.price
                params["role_id"] = innerChargeInfo.roleId.toString()
                params["role_name"] = innerChargeInfo.roleName.toString()
                params["server_code"] = innerChargeInfo.serverCode.toString()
                params["server_name"] = innerChargeInfo.serverName.toString()
                EventTraceImpl.getInstance().onCharge(params)
                callback.onResult(0, result)
            }

            override fun onFailed(result: String) {
                callback.onResult(-1, result)
            }
        })
    }

    fun roleCreate(activity: Activity, roleInfo: GameRoleInfo) {
        this.mActivity = activity
        this.roleInfo = roleInfo
        submitRoleData(activity, roleInfo)
        val params = HashMap<String, Any>()
        params["role_id"] = roleInfo.roleId.toString()
        params["role_name"] = roleInfo.roleName.toString()
        params["server_code"] = roleInfo.serverCode.toString()
        params["server_name"] = roleInfo.serverName.toString()
        EventTraceImpl.getInstance().onRoleCreate(params)
    }

    fun roleLauncher(activity: Activity, roleInfo: GameRoleInfo) {
        this.mActivity = activity
        this.roleInfo = roleInfo
        submitRoleData(activity, roleInfo)
        val params = HashMap<String, Any>()
        params["role_id"] = roleInfo.roleId.toString()
        params["role_name"] = roleInfo.roleName.toString()
        params["server_code"] = roleInfo.serverCode.toString()
        params["server_name"] = roleInfo.serverName.toString()
        EventTraceImpl.getInstance().onRoleCreate(params)
    }

    fun roleUpgrade(activity: Activity, roleInfo: GameRoleInfo) {
        this.mActivity = activity
        this.roleInfo = roleInfo
        submitRoleData(activity, roleInfo)
    }

    private fun submitRoleData(activity: Activity, roleInfo: GameRoleInfo) {
        isSubmitRoleData = true
        SdkRequest.getInstance().submitRoleData(activity, roleInfo, object : IRequestCallback {
            override fun onResponse(resultInfo: ResultInfo) {
                Logger.d("角色上报成功")
            }
        })
    }

    fun openExitView(activity: Activity, callback: ICallback) {
        this.mActivity = activity
        exitTipsDialog?.apply {
            if (isShowing) {
                dismiss()
                exitTipsDialog = null
            }
        }
        exitTipsDialog = DialogUtils.newExitDialog(activity, {
            exitTipsDialog?.apply {
                if (isShowing) {
                    dismiss()
                    exitTipsDialog = null
                }
                callback.onResult(0, "退出游戏")
            }
        }) {
            exitTipsDialog?.apply {
                if (isShowing) {
                    dismiss()
                    exitTipsDialog = null
                }
                callback.onResult(-1, "继续游戏")
            }
        }
        exitTipsDialog?.show()
    }

    fun openBindAccount(activity: Activity, callback: ICallback) {
        this.mActivity = activity
        BindActivity.bind(activity, object : ImplCallback {
            override fun onSuccess(result: String) {
                callback.onResult(0, result)
            }

            override fun onFailed(result: String) {
                callback.onResult(-1, result)
            }

        })
    }

    fun openGmCenter(activity: Activity, callback: ICallback) {
        this.mActivity = activity
        if (initBean == null) {
            Logger.e("跳转客服中心失败，SDK未初始化或初始化失败")
            callback.onResult(-1, "SDK初始化异常或未初始化")
            return
        }
        if (TextUtils.isEmpty(SdkBackLoginInfo.instance.userId)) {
            Logger.e("跳转客服中心失败，用户未登录或登录失败")
            callback.onResult(-1, "跳转客服中心失败，用户未登录或登录失败")
            return
        }
        if (roleInfo == null) {
            Logger.e("跳转客服中心失败，角色未登录或登录失败")
            callback.onResult(-1, "跳转客服中心失败，用户未登录或登录失败")
            return
        }
        roleInfo?.apply {
            val sign = Md5Utils.encodeByMD5(SdkBackLoginInfo.instance.userId + roleId + serverCode + gameCode + "flyfun")
            val url = StringBuffer()
            url.append(initBean!!.initGm!!.url).append("?")
                    .append("sign=").append(URLEncoder.encode(sign, "UTF-8"))
                    .append("&game_code=").append(URLEncoder.encode(gameCode, "UTF-8"))
                    .append("&user_id=").append(URLEncoder.encode(SdkBackLoginInfo.instance.userId, "UTF-8"))
                    .append("&role_id=").append(URLEncoder.encode(roleId, "UTF-8"))
                    .append("&role_name=").append(URLEncoder.encode(roleName, "UTF-8"))
                    .append("&server_code=").append(URLEncoder.encode(serverCode, "UTF-8"))
                    .append("&server_name=").append(URLEncoder.encode(serverName, "UTF-8"))
                    .append("&game_code=").append(URLEncoder.encode(gameCode, "UTF-8"))
                    .append("&user_id=").append(URLEncoder.encode(SdkBackLoginInfo.instance.userId, "UTF-8"))
                    .append("&pic=").append(URLEncoder.encode(initBean!!.initGm!!.logoUrl, "UTF-8"))
            HybridActivity.start(activity, url.toString())
        }
    }

    fun onStart(activity: Activity) {
        this.mActivity = activity
    }

    fun onResume(activity: Activity) {
        this.mActivity = activity
        EventTraceImpl.getInstance().onResume()
    }

    fun onRestart(activity: Activity) {
        this.mActivity = activity
    }

    fun onPause(activity: Activity) {
        this.mActivity = activity
        EventTraceImpl.getInstance().onPause()
    }

    fun onStop(activity: Activity) {
        this.mActivity = activity
    }

    fun onDestroy(activity: Activity?) {
        this.mActivity = activity
    }

    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        this.mActivity = activity
    }

    fun onNewIntent(activity: Activity, intent: Intent) {
        this.mActivity = activity
    }

    fun onConfigurationChanged(activity: Activity, newConfig: Configuration) {
        this.mActivity = activity
    }

    fun onRequestPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        this.mActivity = activity
    }

    fun getCurrentUserId(): String {
        return SdkBackLoginInfo.instance.userId
    }

    fun hasBindAccount(): Boolean {
        return SdkBackLoginInfo.instance.isBindPlatform == 1
    }

    fun isGmCenterEnable(): Boolean {
        return if (initBean == null || initBean!!.initGm == null) {
            false
        } else initBean!!.initGm!!.gmSwitch == 1
    }

    companion object {
        var isLandscape = false

        var initBean: InitBean? = null
    }
}