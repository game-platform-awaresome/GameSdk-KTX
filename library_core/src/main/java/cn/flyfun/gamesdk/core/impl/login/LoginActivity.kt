package cn.flyfun.gamesdk.core.impl.login

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.*
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.entity.LoginType
import cn.flyfun.gamesdk.core.entity.ResultInfo
import cn.flyfun.gamesdk.core.entity.SdkBackLoginInfo
import cn.flyfun.gamesdk.core.entity.Session
import cn.flyfun.gamesdk.core.impl.SdkBridgeImpl
import cn.flyfun.gamesdk.core.impl.login.fragment.ChooseFragment
import cn.flyfun.gamesdk.core.internal.IRequestCallback
import cn.flyfun.gamesdk.core.internal.ImplCallback
import cn.flyfun.gamesdk.core.network.SdkRequest
import cn.flyfun.gamesdk.core.ui.DialogUtils
import cn.flyfun.gamesdk.core.ui.EventEditText
import cn.flyfun.gamesdk.core.ui.VerifyCodeEditText
import cn.flyfun.gamesdk.core.ui.dialog.ScaleLoadingDialog
import cn.flyfun.gamesdk.core.utils.AndroidBug5497Workaround
import cn.flyfun.gamesdk.core.utils.SessionUtils
import cn.flyfun.gamesdk.core.utils.TimeDownUtils
import cn.flyfun.support.EditTextUtils
import cn.flyfun.support.ResUtils
import cn.flyfun.support.encryption.Md5Utils
import cn.flyfun.support.jarvis.Toast
import cn.flyfun.support.ui.NoScrollViewPager
import cn.flyfun.support.ui.circleprogress.CircleProgressLoadingDialog
import com.google.android.material.tabs.TabLayout
import org.json.JSONException
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.Exception


/**
 * @author #Suyghur.
 * Created on 2020/12/8
 */
class LoginActivity : FragmentActivity() {

    var signInImpl: SignInImpl? = null
        private set

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: NoScrollViewPager
    private lateinit var rootContainer: ConstraintLayout
    private lateinit var autoLoginContainer: ConstraintLayout
    private lateinit var loginContainer: ConstraintLayout
    private lateinit var forgetContainer: ConstraintLayout
    private lateinit var tvTips: TextView
    private lateinit var ivReturn: ImageView
    private lateinit var vcSend: VerifyCodeEditText
    private lateinit var etAccount: EventEditText
    private lateinit var etPassword: EventEditText
    private lateinit var etPassword2: EventEditText
    private var leftSelect = 0
    private var leftSelected = 0
    private var rightSelect = 0
    private var rightSelected = 0
    private var loginLoadingDialog: ScaleLoadingDialog? = null

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                101 -> changeTimeView(msg.obj as Int)
                200 -> {
                    //自动登录
                    if (isCancelLogin) {
                        return
                    }
                    session?.apply {
                        showLoadingDialog()
                        userAutoLogin()
                    }
                }
            }
        }
    }

    private fun userAutoLogin() {
        session?.apply {
            when (loginType) {
                LoginType.TYPE_ACCOUNT_LOGIN -> signInImpl?.accountLogin(userName, pwd)
                LoginType.TYPE_FACEBOOK_LOGIN -> signInImpl?.facebookLogin(this@LoginActivity)
                LoginType.TYPE_GUEST_LOGIN -> signInImpl?.guestLogin()
                LoginType.TYPE_GOOGLE_LOGIN -> signInImpl?.googleLogin(this@LoginActivity)
            }
        }
    }


    fun userLoginVerify(loginParams: JSONObject) {
        SdkRequest.getInstance().userLoginVerify(this, loginParams, object : IRequestCallback {
            override fun onResponse(resultInfo: ResultInfo) {
                if (resultInfo.code == 0 && !TextUtils.isEmpty(resultInfo.data)) {
                    try {
                        val jsonObject = JSONObject(resultInfo.data)
                        SdkBackLoginInfo.instance.userId = jsonObject.getString("user_id")
                        SdkBackLoginInfo.instance.timestamp = jsonObject.getString("timestamp")
                        SdkBackLoginInfo.instance.isRegUser = jsonObject.getInt("is_reg_user")
                        SdkBackLoginInfo.instance.isBindPlatform =
                            jsonObject.getInt("is_bind_platform")
                        SdkBackLoginInfo.instance.cpSign = jsonObject.getString("cp_sign")
                        SdkBackLoginInfo.instance.loginType = loginParams.getInt("login_type")

                        if (session == null) {
                            session = Session()
                        }
                        session?.apply {
                            userId = SdkBackLoginInfo.instance.userId
                            userName = loginParams.getString("user_name")
                            pwd = loginParams.getString("pwd")
                            loginType = loginParams.getInt("login_type")
                            SessionUtils.getInstance().saveSession(this@LoginActivity, this)
                        }

                        implCallback?.onSuccess(SdkBackLoginInfo.instance.toJsonString())
                        hideLoadingDialog()
                        this@LoginActivity.finish()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        implCallback?.onFailed("登录校验异常")
                        hideLoadingDialog()
                    }
                } else {
                    if (!TextUtils.isEmpty(resultInfo.msg)) {
                        Toast.toastInfo(this@LoginActivity, resultInfo.msg)
                    }
                    implCallback?.onFailed("登录校验异常")
                    hideLoadingDialog()
                }
            }
        })
    }

    fun userRegister(userName: String, pwd: String) {
        SdkRequest.getInstance().userRegister(this, userName, pwd, object : IRequestCallback {
            override fun onResponse(resultInfo: ResultInfo) {
                if (resultInfo.code == 0 && !TextUtils.isEmpty(resultInfo.data)) {
                    try {
                        val jsonObject = JSONObject(resultInfo.data)
                        SdkBackLoginInfo.instance.userId = jsonObject.getString("user_id")
                        SdkBackLoginInfo.instance.timestamp = jsonObject.getString("timestamp")
                        SdkBackLoginInfo.instance.isRegUser = jsonObject.getInt("is_reg_user")
                        SdkBackLoginInfo.instance.isBindPlatform =
                            jsonObject.getInt("is_bind_platform")
                        SdkBackLoginInfo.instance.cpSign = jsonObject.getString("cp_sign")
                        SdkBackLoginInfo.instance.loginType = 0

                        if (session == null) {
                            session = Session()
                        }

                        session?.apply {
                            reset()
                            userId = SdkBackLoginInfo.instance.userId
                            this.userName = userName
                            this.pwd = pwd
                            loginType = 0
                            SessionUtils.getInstance().saveSession(this@LoginActivity, this)
                        }

                        implCallback?.onSuccess(SdkBackLoginInfo.instance.toJsonString())
                        hideLoadingDialog()
                        this@LoginActivity.finish()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.toastInfo(
                            this@LoginActivity,
                            ResUtils.getResString(this@LoginActivity, "ffg_login_register_error")
                        )
                        hideLoadingDialog()
                        implCallback?.onFailed("账号注册异常")
                    }
                } else {
                    if (!TextUtils.isEmpty(resultInfo.msg)) {
                        Toast.toastInfo(this@LoginActivity, resultInfo.msg)
                    } else {
                        Toast.toastInfo(
                            this@LoginActivity,
                            ResUtils.getResString(this@LoginActivity, "ffg_login_register_error")
                        )
                    }
                    implCallback?.onFailed("账号注册异常")
                    hideLoadingDialog()
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInImpl = SignInImpl(this, object : SignInImpl.ISignInCallback {
            override fun onSuccess(result: String) {
                try {
                    showLoadingDialog()
                    userLoginVerify(JSONObject(result))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailed(result: String) {
                if (loginLoadingDialog != null && loginLoadingDialog!!.isShowing) {
                    loginLoadingDialog!!.dismiss()
                    loginLoadingDialog = null
                }
                implCallback?.onFailed(result)
            }
        })

        initView()
        showAutoLoginView()
    }

    private fun initView() {
        if (SdkBridgeImpl.isLandscape) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
        setContentView(ResUtils.getResId(this, "ffg_login", "layout"))
        if (!SdkBridgeImpl.isLandscape) {
            AndroidBug5497Workaround.assistActivity(this)
        }
        rootContainer = findViewById(ResUtils.getResId(this, "ffg_login_container", "id"))
        autoLoginContainer = findViewById(ResUtils.getResId(this, "ffg_cl_auto", "id"))
        tvTips = findViewById(ResUtils.getResId(this, "ffg_tv_tips", "id"))
        val btnChange = findViewById<Button>(ResUtils.getResId(this, "ffg_btn_change", "id"))
        btnChange.setOnClickListener {
            if (autoLoginContainer.visibility == View.VISIBLE) {
                autoLoginContainer.visibility = View.GONE
                loginContainer.visibility = View.VISIBLE
                isCancelLogin = true
            }
        }

        autoLoginContainer.visibility = View.GONE

        forgetContainer = findViewById(ResUtils.getResId(this, "ffg_cl_forget", "id"))

        loginContainer = findViewById(ResUtils.getResId(this, "ffg_cl_login", "id"))
        loginContainer.visibility = View.VISIBLE

        ivReturn = findViewById(ResUtils.getResId(this, "ffg_iv_return", "id"))
        ivReturn.setOnClickListener {
            hideForgetView()
        }

        val ivLogo = findViewById<ImageView>(ResUtils.getResId(this, "ffg_iv_login_logo", "id"))
        val logoName = Md5Utils.encodeByMD5(SdkBridgeImpl.initBean.initGm.iconUrl) + ".png"
        ivLogo.setImageBitmap(getLocalBitmap("${this@LoginActivity.getExternalFilesDir(".cache")!!.absolutePath}/$logoName"))

        etAccount = findViewById(ResUtils.getResId(this, "ffg_et_forget_account", "id"))
        etAccount.apply {
            leftImageView.setBackgroundResource(
                ResUtils.getResId(
                    this@LoginActivity,
                    "ffg_account_img",
                    "drawable"
                )
            )
            editText.hint = ResUtils.getResString(this@LoginActivity, "ffg_login_account_hint")
        }

        etPassword = findViewById(ResUtils.getResId(this, "ffg_et_forget_pwd1", "id"))
        etPassword.apply {
            leftImageView.setBackgroundResource(
                ResUtils.getResId(
                    this@LoginActivity,
                    "ffg_password_img",
                    "drawable"
                )
            )
            editText.hint = ResUtils.getResString(this@LoginActivity, "ffg_login_password1_hint")
        }

        etPassword2 = findViewById(ResUtils.getResId(this, "ffg_et_forget_pwd2", "id"))
        etPassword2.apply {
            leftImageView.setBackgroundResource(
                ResUtils.getResId(
                    this@LoginActivity,
                    "ffg_password_img",
                    "drawable"
                )
            )
            editText.hint = ResUtils.getResString(this@LoginActivity, "ffg_login_password2_hint")
        }

        vcSend = findViewById(ResUtils.getResId(this, "ffg_et_forget_code", "id"))
        vcSend.apply {
            leftImageView.setBackgroundResource(
                ResUtils.getResId(
                    this@LoginActivity,
                    "ffg_email_img",
                    "drawable"
                )
            )
            editText.hint = ResUtils.getResString(this@LoginActivity, "ffg_login_input_code_hint")
            textView.setOnClickListener {
                if (!TextUtils.isEmpty(etAccount.editText.text)) {
                    captchaCode
                } else {
                    Toast.toastInfo(
                        this@LoginActivity,
                        ResUtils.getResString(this@LoginActivity, "ffg_tips_empty_account")
                    )
                }
            }
        }

        val btnConfirm = findViewById<Button>(ResUtils.getResId(this, "ffg_btn_confirm", "id"))
        btnConfirm.setOnClickListener {
            doForgetPassword()
        }

        tabLayout = findViewById(ResUtils.getResId(this, "ffg_tl", "id"))
        viewPager = findViewById(ResUtils.getResId(this, "ffg_vp", "id"))
        val fragmentManager = supportFragmentManager
        //使用适配器将ViewPager与Fragment绑定在一起
        val tabs = this.resources.getStringArray(ResUtils.getResId(this, "ffg_login_tab", "array"))
        val pagerAdapter = LoginFragmentPagerAdapter(tabs, fragmentManager)
        viewPager.adapter = pagerAdapter
        //将TabLayout与ViewPager绑定在一起
        tabLayout.setupWithViewPager(viewPager)
        leftSelect = ResUtils.getResId(this, "ffg_tab_left_select", "drawable")
        leftSelected = ResUtils.getResId(this, "ffg_tab_left_selected", "drawable")
        rightSelect = ResUtils.getResId(this, "ffg_tab_right_select", "drawable")
        rightSelected = ResUtils.getResId(this, "ffg_tab_right_selected", "drawable")
        setTabBackground(leftSelected, rightSelect)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tabLayout.selectedTabPosition) {
                    0 -> setTabBackground(leftSelected, rightSelect)
                    1 -> setTabBackground(leftSelect, rightSelected)
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (pagerAdapter.currentFragment is ChooseFragment) {
                    (pagerAdapter.currentFragment as ChooseFragment).hideAccountList()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun getLocalBitmap(path: String): Bitmap? {
        try {
            val fis = FileInputStream(path)
            return BitmapFactory.decodeStream(fis)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    private fun showAutoLoginView() {
        session = SessionUtils.getInstance().getLocalLastSession(this)

        if (session == null || !isAutoLogin) {
            loginContainer.visibility = View.VISIBLE
            autoLoginContainer.visibility = View.GONE
            return
        }

        session?.apply {
            if (isAutoLogin) {
                loginContainer.visibility = View.GONE
                autoLoginContainer.visibility = View.VISIBLE
                var tips = ""
                when (loginType) {
                    LoginType.TYPE_ACCOUNT_LOGIN -> {
                        var userName = session!!.userName
                        if (userName.length > 15) {
                            userName = userName.substring(0, 15) + "..."
                        }
                        tips = userName + ResUtils.getResString(
                            this@LoginActivity,
                            "ffg_login_tv_login_account"
                        )
                    }
                    LoginType.TYPE_FACEBOOK_LOGIN -> tips =
                        ResUtils.getResString(this@LoginActivity, "ffg_login_tv_login_facebook")
                    LoginType.TYPE_GUEST_LOGIN -> tips =
                        ResUtils.getResString(this@LoginActivity, "ffg_login_tv_login_guest")
                    LoginType.TYPE_GOOGLE_LOGIN -> tips =
                        ResUtils.getResString(this@LoginActivity, "ffg_login_tv_login_google")
                }
                tvTips.text = tips

                //延迟n秒发起登录请求
                Logger.d("延迟1.5秒发起登录请求")
                mHandler.sendEmptyMessageDelayed(200, 1500)
            }
        }
    }

    private fun changeTimeView(time: Int) {
        vcSend.apply {
            if (time <= 0) {
                textView.text =
                    ResUtils.getResString(this@LoginActivity, "ffg_login_btn_get_captcha")
                textView.isEnabled = true
            } else {
                textView.text = time.toString() + "s"
                textView.isEnabled = false
            }
        }
    }

    private fun setTabBackground(left: Int, right: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val tabStrip = tabLayout.getChildAt(0) as ViewGroup
            val tabView1 = tabStrip.getChildAt(0)
            val tabView2 = tabStrip.getChildAt(1)
            if (tabView1 != null) {
                val paddingStart = tabView1.paddingStart
                val paddingTop = tabView1.paddingTop
                val paddingEnd = tabView1.paddingEnd
                val paddingBottom = tabView1.paddingBottom
                ViewCompat.setBackground(
                    tabView1,
                    ContextCompat.getDrawable(tabView1.context, left)
                )
                ViewCompat.setPaddingRelative(
                    tabView1,
                    paddingStart,
                    paddingTop,
                    paddingEnd,
                    paddingBottom
                )
            }
            if (tabView2 != null) {
                val paddingStart = tabView2.paddingStart
                val paddingTop = tabView2.paddingTop
                val paddingEnd = tabView2.paddingEnd
                val paddingBottom = tabView2.paddingBottom
                ViewCompat.setBackground(
                    tabView2,
                    ContextCompat.getDrawable(tabView2.context, right)
                )
                ViewCompat.setPaddingRelative(
                    tabView2,
                    paddingStart,
                    paddingTop,
                    paddingEnd,
                    paddingBottom
                )
            }
        }
    }

    private val captchaCode: Unit
        get() {
            SdkRequest.getInstance()
                .getCaptcha(this, etAccount.editText.text.toString(), object : IRequestCallback {
                    override fun onResponse(resultInfo: ResultInfo) {
                        if (resultInfo.code == 0) {
                            if (TextUtils.isEmpty(resultInfo.msg)) {
                                Toast.toastInfo(this@LoginActivity, "驗證碼已發送，請注意查收")
                            } else {
                                Toast.toastInfo(this@LoginActivity, resultInfo.msg)
                            }
                            changeTimeNum()
                        } else {
                            if (TextUtils.isEmpty(resultInfo.msg)) {
                                Toast.toastInfo(this@LoginActivity, "驗證碼發送异常，請稍後再試")
                            } else {
                                Toast.toastInfo(this@LoginActivity, resultInfo.msg)
                            }
                        }
                    }
                })
        }

    private fun changeTimeNum() {
        if (!TimeDownUtils.isRunning()) {
            TimeDownUtils.start(object : TimeDownUtils.TimeCallback {
                override fun onTime(time: Int) {
                    val msg = Message()
                    msg.what = 101
                    msg.obj = time
                    mHandler.sendMessage(msg)
                }
            })
        } else {
            TimeDownUtils.resetCallback(object : TimeDownUtils.TimeCallback {
                override fun onTime(time: Int) {
                    val msg = Message()
                    msg.what = 101
                    msg.obj = time
                    mHandler.sendMessage(msg)
                }
            })
        }
    }

    private fun doForgetPassword() {
        val userName = etAccount.editText.text.toString()
        val pwd = etPassword.editText.text.toString()
        val pwd2 = etPassword2.editText.text.toString()
        val code = vcSend.editText.text.toString()
        if (TextUtils.isEmpty(userName)) {
            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_empty_account"))
            return
        }
        if (TextUtils.isEmpty(pwd)) {
            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_empty_password"))
            return
        }
        if (TextUtils.isEmpty(pwd2)) {
            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_empty_password"))
            return
        }
        if (TextUtils.isEmpty(code)) {
            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_empty_code"))
            return
        }
        if (!EditTextUtils.filterEmail(userName)) {
            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_email_format_error"))
            return
        }
        SdkRequest.getInstance()
            .forgetPassword(this, userName, pwd, code, object : IRequestCallback {
                override fun onResponse(resultInfo: ResultInfo) {
                    if (resultInfo.code == 0) {
                        hideForgetView()
                        if (TextUtils.isEmpty(resultInfo.msg)) {
                            return
                        } else {
                            Toast.toastInfo(this@LoginActivity, resultInfo.msg)
                        }
                        signInImpl?.accountLogin(userName, pwd)
                    } else {
                        if (TextUtils.isEmpty(resultInfo.msg)) {
                            return
                        } else {
                            Toast.toastInfo(this@LoginActivity, resultInfo.msg)
                        }
                    }
                }

            })
    }


    fun showForgetView() {
        tabLayout.visibility = View.GONE
        viewPager.visibility = View.GONE
        forgetContainer.visibility = View.VISIBLE
        ivReturn.visibility = View.VISIBLE
    }

    fun hideForgetView() {
        forgetContainer.visibility = View.GONE
        ivReturn.visibility = View.GONE
        tabLayout.visibility = View.VISIBLE
        viewPager.visibility = View.VISIBLE
        etAccount.editText.setText("")
        etPassword.editText.setText("")
        etPassword2.editText.setText("")
        vcSend.editText.setText("")
        if (TimeDownUtils.isRunning()) {
            TimeDownUtils.cancel()
            vcSend.textView.text = ResUtils.getResString(this, "ffg_login_btn_get_captcha")
            vcSend.textView.isEnabled = true
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                val v = currentFocus
                if (isShouldHideInput(v, it)) {
                    val imm =
                        this@LoginActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v!!.windowToken, 0)
                }
                return super.dispatchTouchEvent(it)
            }
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)
    }

    private fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            // 点击的是输入框区域，保留点击EditText的事件
            return event.x <= left || event.x >= right || event.y <= top || event.y >= bottom
        }
        return false
    }

    private fun hideBar() {
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        val uiOptions = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions
        val isImmersiveModeEnabled = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions
        if (!isImmersiveModeEnabled) {
            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
            if (Build.VERSION.SDK_INT >= 16) {
                newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN
            }
            if (Build.VERSION.SDK_INT >= 18) {
                newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
            window.decorView.systemUiVisibility = newUiOptions
        }
    }

    fun showLoadingDialog() {
        if (loginLoadingDialog != null && loginLoadingDialog!!.isShowing) {
            loginLoadingDialog!!.dismiss()
            loginLoadingDialog = null
        }
        loginLoadingDialog = DialogUtils.showScaleLoadingDialog(this@LoginActivity, "")
        loginLoadingDialog?.show()
    }

    fun hideLoadingDialog() {
        if (loginLoadingDialog != null && loginLoadingDialog!!.isShowing) {
            loginLoadingDialog!!.dismiss()
            loginLoadingDialog = null
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (!SdkBridgeImpl.isLandscape) {
            rootContainer.setBackgroundColor(Color.BLACK)
        }
    }

    override fun onResume() {
        super.onResume()
        hideBar()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.apply {
            signInImpl?.onActivityResult(requestCode, resultCode, this)
        }
    }

    companion object {
        var implCallback: ImplCallback? = null
        private var session: Session? = null
        private var isAutoLogin = false
        private var isCancelLogin = false
        fun login(activity: Activity, isAuto: Boolean, callback: ImplCallback?) {
            isAutoLogin = isAuto
            isCancelLogin = false
            implCallback = callback
            activity.startActivity(
                Intent(
                    activity,
                    LoginActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}