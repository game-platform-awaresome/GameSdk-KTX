package com.flyfun.demo

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import cn.flyfun.gamesdk.base.FlyFunGame
import cn.flyfun.gamesdk.base.entity.GameChargeInfo
import cn.flyfun.gamesdk.base.entity.GameRoleInfo
import cn.flyfun.gamesdk.base.internal.ICallback
import cn.flyfun.support.encryption.Md5Utils
import cn.flyfun.support.jarvis.Toast
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import kotlin.system.exitProcess

/**
 * @author #Suyghur
 * Created on 2020/12/7
 */
class DemoActivity : Activity(), View.OnClickListener {

    companion object {
        private const val APP_SECRET = "xxxx"
    }

    private var mTextView: TextView? = null
    private var cacheRoleInfo: CacheRoleInfo.Companion.RoleInfo? = null
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                111 -> {
                    mTextView?.apply {
                        text = text.toString() + msg.obj.toString()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FlyFunGame.getInstance().initialize(this, false, object : ICallback {
            override fun onResult(code: Int, result: String) {
                if (code == 0) {
                    FlyFunGame.getInstance().login(this@DemoActivity, true, object : ICallback {
                        override fun onResult(code: Int, result: String) {
                            verifyUserLogin(code, result)
                        }
                    })
                }
            }
        })
        initView()
        FlyFunGame.getInstance().logHandler(handler)

    }

    private fun initView() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        DemoButtons.addViews(this, layout)

        mTextView = TextView(this)
        mTextView?.apply {
            text = ""
            layout.addView(this)
        }

        val scrollView = ScrollView(this)
        scrollView.addView(layout)
        setContentView(scrollView)
    }

    override fun onClick(v: View?) {
        mTextView?.text = ""
        v?.apply {
            when (tag as Int) {
                0 -> EnvActivity.start(this@DemoActivity)
                1 -> FlyFunGame.getInstance().login(this@DemoActivity, true, object : ICallback {
                    override fun onResult(code: Int, result: String) {
                        verifyUserLogin(code, result)
                    }
                })
                2 -> FlyFunGame.getInstance().logout(this@DemoActivity, object : ICallback {
                    override fun onResult(code: Int, result: String) {
                        if (code == 0) {
                            DemoButtons.hideBindButton()
                            DemoButtons.hideGMButton()
                            FlyFunGame.getInstance()
                                .login(this@DemoActivity, false, object : ICallback {
                                    override fun onResult(code: Int, result: String) {
                                        verifyUserLogin(code, result)
                                    }
                                })
                        }
                    }
                })
                3 -> {
                    //???????????????
                    cacheRoleInfo = null
                    cacheRoleInfo = CacheRoleInfo.setDemoRoleInfo(
                        this@DemoActivity,
                        FlyFunGame.getInstance().getCurrentUserId()
                    )
                    FlyFunGame.getInstance().roleCreate(this@DemoActivity, getGameRoleInfo())
                }
                4 -> FlyFunGame.getInstance().roleLauncher(this@DemoActivity, getGameRoleInfo())
                5 -> FlyFunGame.getInstance().roleUpgrade(this@DemoActivity, getGameRoleInfo())
                6 -> FlyFunGame.getInstance()
                    .charge(this@DemoActivity, getGameChargeInfo(), object : ICallback {
                        override fun onResult(code: Int, result: String) {
                            if (code == 0) {
                                Toast.toastInfo(this@DemoActivity, "(demo??????)??????????????????")
                            } else {
                                Toast.toastInfo(this@DemoActivity, "(demo??????)???????????????$result")
                            }
                        }

                    })
                7 -> FlyFunGame.getInstance()
                    .openBindAccount(this@DemoActivity, object : ICallback {
                        override fun onResult(code: Int, result: String) {
                            if (code == 0) {
                                Toast.toastInfo(this@DemoActivity, "(demo??????)??????????????????")
                            } else {
                                Toast.toastInfo(this@DemoActivity, "(demo??????)??????????????????")
                            }
                        }
                    })
                8 -> FlyFunGame.getInstance().openGmCenter(this@DemoActivity, object : ICallback {
                    override fun onResult(code: Int, result: String) {
                        if (code != 0) {
                            Toast.toastInfo(this@DemoActivity, "(demo??????)$result")
                        }
                    }
                })
                9 -> createCrash()
            }
        }
    }

    private fun createCrash() {
        throw RuntimeException("test crash")
    }

    private fun getGameRoleInfo(): GameRoleInfo {
        val gameRoleInfo = GameRoleInfo()
        //??????ID
        gameRoleInfo.userId = FlyFunGame.getInstance().getCurrentUserId()
        //??????ID
        gameRoleInfo.roleId = cacheRoleInfo?.roleId
        //????????????
        gameRoleInfo.roleName = cacheRoleInfo?.roleName
        //????????????
        gameRoleInfo.roleLevel = cacheRoleInfo?.roleLevel
        //?????????ID
        gameRoleInfo.serverCode = cacheRoleInfo?.serverCode
        //????????????
        gameRoleInfo.serverName = cacheRoleInfo?.serverName
        //??????VIP?????????????????????????????????""
        gameRoleInfo.vipLevel = cacheRoleInfo?.vipLevel
        //???????????????????????????
        gameRoleInfo.balance = cacheRoleInfo?.balance
        return gameRoleInfo
    }

    private fun getGameChargeInfo(): GameChargeInfo {
        val gameChargeInfo = GameChargeInfo()
        val orderId = "order_" + System.currentTimeMillis()
        //??????ID
        gameChargeInfo.userId = FlyFunGame.getInstance().getCurrentUserId()
        //??????ID
        gameChargeInfo.roleId = cacheRoleInfo?.roleId
        //????????????
        gameChargeInfo.roleName = cacheRoleInfo?.roleName
        //????????????
        gameChargeInfo.roleLevel = cacheRoleInfo?.roleLevel
        //?????????ID
        gameChargeInfo.serverCode = cacheRoleInfo?.serverCode
        //????????????
        gameChargeInfo.serverName = cacheRoleInfo?.serverName
        //??????????????????
        gameChargeInfo.cpOrderId = orderId
        //?????????????????????????????????????????????
        gameChargeInfo.cpNotifyUrl = "http://www.flyfungame.com/?ac=order&ct=notify"
        //????????????????????????????????????????????????
        gameChargeInfo.cpCallbackInfo = "cp_callback_info||$orderId"
        //?????????????????????????????????
        gameChargeInfo.price = 0.99f
        //??????ID????????????
        gameChargeInfo.productId = "com.flyfun.ylj.60"
        //????????????
        gameChargeInfo.productName = "70??????"
        //????????????
        gameChargeInfo.productDesc = "70??????"
        return gameChargeInfo
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FlyFunGame.getInstance().openExitView(this, object : ICallback {
                override fun onResult(code: Int, result: String) {
                    if (code == 0) {
                        finish()
                    }
                }
            })
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        FlyFunGame.getInstance().onStart(this)
    }

    override fun onResume() {
        super.onResume()
        FlyFunGame.getInstance().onResume(this)
    }

    override fun onRestart() {
        super.onRestart()
        FlyFunGame.getInstance().onRestart(this)
    }

    override fun onPause() {
        super.onPause()
        FlyFunGame.getInstance().onPause(this)
    }

    override fun onStop() {
        super.onStop()
        FlyFunGame.getInstance().onStop(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        FlyFunGame.getInstance().onDestroy(this)
        exitProcess(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FlyFunGame.getInstance().onActivityResult(this, requestCode, resultCode, data)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        FlyFunGame.getInstance().onNewIntent(this, intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        FlyFunGame.getInstance().onConfigurationChanged(this, newConfig)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        FlyFunGame.getInstance()
            .onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    fun getAllLogFiles(path: String): ArrayList<String> {
        val fileTree = File(path).walk()
        val logs = arrayListOf<String>()
        fileTree.maxDepth(1)
            .filter { it.isFile }
            .filter { it.extension == "log" }
            .forEach {
                logs.add(it.name)
            }
        return logs
    }


    private fun verifyUserLogin(code: Int, result: String) {
        //???????????????????????????????????????????????????
        if (code == 0 && !TextUtils.isEmpty(result)) {
            try {
                val jsonObject = JSONObject(result)
                val userId = jsonObject.getString("user_id")
                val timestamp = jsonObject.getString("timestamp")
                val cpSign = jsonObject.getString("cp_sign")
                //app_secret+user_id+timestamp('+'??????????????????????????????)
                val sign = Md5Utils.encodeByMD5(APP_SECRET + userId + timestamp)
                if (sign == cpSign) {
                    Toast.toastInfo(this, "(demo??????)???????????????user_id : $userId")
                    cacheRoleInfo = CacheRoleInfo.getDemoRoleInfo(this, userId)
                    //??????????????????????????????????????????????????????????????????????????????????????????????????????
                    if (FlyFunGame.getInstance().hasBindAccount()) {
                        //?????????????????????????????????
                        DemoButtons.hideBindButton()
                    } else {
                        //?????????????????????????????????
                        DemoButtons.showBindButton()
                    }
                    //???????????????????????????SDK??????????????????????????????????????????????????????????????????
                    if (FlyFunGame.getInstance().isGmCenterEnable()) {
                        DemoButtons.showGMButton()
                    } else {
                        DemoButtons.hideGMButton()
                    }
                } else {
                    Toast.toastInfo(this, "(demo??????)???????????????????????????????????????")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            Toast.toastInfo(this, "(demo??????)$result")
        }
    }
}