//package cn.flyfun.ktx.gamesdk.core.impl.login
//
//import android.app.Activity
//import android.content.Intent
//import android.graphics.Color
//import android.os.*
//import android.text.TextUtils
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewGroup
//import android.view.WindowManager
//import android.view.inputmethod.InputMethodManager
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.content.ContextCompat
//import androidx.core.view.ViewCompat
//import androidx.fragment.app.FragmentActivity
//import cn.flyfun.ktx.gamesdk.base.utils.Logger
//import cn.flyfun.ktx.gamesdk.core.entity.LoginType
//import cn.flyfun.ktx.gamesdk.core.entity.ResultInfo
//import cn.flyfun.ktx.gamesdk.core.entity.SdkBackLoginInfo
//import cn.flyfun.ktx.gamesdk.core.entity.Session
//import cn.flyfun.ktx.gamesdk.core.impl.SdkBridgeImpl
//import cn.flyfun.ktx.gamesdk.core.impl.login.fragment.ChooseFragment
//import cn.flyfun.ktx.gamesdk.core.inter.IRequestCallback
//import cn.flyfun.ktx.gamesdk.core.inter.ImplCallback
//import cn.flyfun.ktx.gamesdk.core.network.SdkRequest
//import cn.flyfun.ktx.gamesdk.core.ui.DialogUtils
//import cn.flyfun.ktx.gamesdk.core.ui.EventEditText
//import cn.flyfun.ktx.gamesdk.core.ui.VerifyCodeEditText
//import cn.flyfun.ktx.gamesdk.core.utils.AndroidBug5497Workaround
//import cn.flyfun.ktx.gamesdk.core.utils.SessionUtils
//import cn.flyfun.ktx.gamesdk.core.utils.TimeDownUtils
//import cn.flyfun.support.EditTextUtils
//import cn.flyfun.support.ResUtils
//import cn.flyfun.support.jarvis.Toast
//import cn.flyfun.support.ui.NoScrollViewPager
//import cn.flyfun.support.ui.circleprogress.CircleProgressLoadingDialog
//import com.google.android.material.tabs.TabLayout
//import org.json.JSONException
//import org.json.JSONObject
//
//
///**
// * @author #Suyghur.
// * Created on 2020/12/8
// */
//class LoginActivity : FragmentActivity() {
//
//    companion object {
//        var implcallback: ImplCallback? = null
//        private var session: Session? = null
//        private var isAutoLogin = false
//        private var isCancelLogin = false
//
//        fun login(activity: Activity, isAuto: Boolean, callback: ImplCallback) {
//            isAutoLogin = isAuto
//            isCancelLogin = false
//            implcallback = callback
//            activity.startActivity(Intent(activity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
//        }
//    }
//
//    var signInImpl: SignInImpl? = null
//
//    private var tabLayout: TabLayout? = null
//    private var viewPager: NoScrollViewPager? = null
//
//    private var rootContainer: ConstraintLayout? = null
//    private var autoLoginContainer: ConstraintLayout? = null
//    private var loginContainer: ConstraintLayout? = null
//    private var forgetContainer: ConstraintLayout? = null
//    private var tvTips: TextView? = null
//    private var ivReturn: ImageView? = null
//    private var vcSend: VerifyCodeEditText? = null
//    private var etAccount: EventEditText? = null
//    private var etPassword: EventEditText? = null
//    private var etPassword2: EventEditText? = null
//    private var leftSelect = 0
//    private var leftSelected = 0
//    private var rightSelect = 0
//    private var rightSelected = 0
//
//    private var loginLoadingDialog: CircleProgressLoadingDialog? = null
//
//    private val handler = object : Handler() {
//        override fun handleMessage(msg: Message) {
//            when (msg.what) {
//                101 -> changeTimeView(msg.obj as Int)
//                200 -> {
//                    //自动登录
//                    if (isCancelLogin) {
//                        return
//                    }
//                    session?.apply {
//                        loginLoadingDialog?.apply {
//                            if (isShowing) {
//                                dismiss()
//                                loginLoadingDialog = null
//                            }
//                        }
//                        loginLoadingDialog = DialogUtils.showCircleProgressLoadingDialog(this@LoginActivity, "")
//                        loginLoadingDialog!!.show()
//                        userAutoLogin()
//                    }
//                }
//            }
//        }
//
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        signInImpl = SignInImpl(this, object : SignInImpl.ISignInCallback {
//            override fun onSuccess(result: String) {
//                try {
//                    userLoginVerify(JSONObject(result))
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//            override fun onFailed(result: String) {
//                loginLoadingDialog?.apply {
//                    if (isShowing) {
//                        dismiss()
//                    }
//                    implcallback?.onFailed(result)
//                }
//            }
//        })
//
//        initView()
//        showAutoLoginView()
//    }
//
//    fun initView() {
//        if (SdkBridgeImpl.isLandscape) {
//            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
//        }
//        setContentView(ResUtils.getResId(this, "ffg_login", "layout"))
//
//        if (!SdkBridgeImpl.isLandscape) {
//            AndroidBug5497Workaround.assistActivity(this)
//        }
//
//        rootContainer = findViewById(ResUtils.getResId(this, "ffg_login_container", "id"))
//
//        autoLoginContainer = findViewById(ResUtils.getResId(this, "ffg_login_container", "id"))
//        tvTips = findViewById(ResUtils.getResId(this, "ffg_tv_tips", "id"))
//        val buttonChange = findViewById<Button>(ResUtils.getResId(this, "ffg_btn_change", "id"))
//        buttonChange.setOnClickListener {
//            if (autoLoginContainer?.visibility == View.VISIBLE) {
//                autoLoginContainer?.visibility = View.GONE
//                loginContainer?.visibility = View.VISIBLE
//                isCancelLogin = true
//            }
//        }
//
//        autoLoginContainer?.visibility = View.GONE
//        forgetContainer = findViewById(ResUtils.getResId(this, "ffg_cl_forget", "id"))
//        loginContainer = findViewById(ResUtils.getResId(this, "ffg_cl_login", "id"))
//        loginContainer?.visibility = View.VISIBLE
//
//        ivReturn = findViewById(ResUtils.getResId(this, "ffg_iv_return", "id"))
//        ivReturn?.setOnClickListener {
//            hideForgetView()
//        }
//
//        val ivLogo = findViewById<ImageView>(ResUtils.getResId(this, "ffg_iv_login_logo", "id"))
//
//        val logoId = ResUtils.getResId(this, "ffg_login_logo_img", "drawable")
//        if (logoId != 0) {
//            ivLogo.setBackgroundResource(logoId)
//        }
//
//        etAccount = findViewById(ResUtils.getResId(this, "ffg_et_forget_account", "id"))
//        etAccount?.apply {
//            leftImageView.setBackgroundResource(ResUtils.getResId(this@LoginActivity, "ffg_account_img", "drawable"))
//            editText.hint = ResUtils.getResString(this@LoginActivity, "ffg_login_account_hint")
//        }
//
//        etPassword = findViewById(ResUtils.getResId(this, "ffg_et_forget_pwd1", "id"))
//        etPassword?.apply {
//            leftImageView.setBackgroundResource(ResUtils.getResId(this@LoginActivity, "ffg_password_img", "drawable"))
//            editText.hint = ResUtils.getResString(this@LoginActivity, "ffg_login_password1_hint")
//        }
//
//        etPassword2 = findViewById(ResUtils.getResId(this, "ffg_et_forget_pwd2", "id"))
//        etPassword2?.apply {
//            leftImageView.setBackgroundResource(ResUtils.getResId(this@LoginActivity, "ffg_password_img", "drawable"))
//            editText.hint = ResUtils.getResString(this@LoginActivity, "ffg_login_password2_hint")
//        }
//
//        vcSend = findViewById(ResUtils.getResId(this, "ffg_et_forget_code", "id"))
//        vcSend?.apply {
//            leftImageView.setBackgroundResource(ResUtils.getResId(this@LoginActivity, "ffg_email_img", "drawable"))
//            editText.hint = ResUtils.getResString(this@LoginActivity, "ffg_login_input_code_hint")
//            setOnClickListener {
//                if (!TextUtils.isEmpty(etAccount?.editText?.text)) {
//                    getCaptchaCode()
//                } else {
//                    Toast.toastInfo(this@LoginActivity, ResUtils.getResString(this@LoginActivity, "ffg_tips_empty_account"))
//                }
//            }
//        }
//
//        val btnConfirm = findViewById<Button>(ResUtils.getResId(this, "ffg_btn_confirm", "id"))
//        btnConfirm?.setOnClickListener {
//            doForgetPassword()
//        }
//
//        tabLayout = findViewById(ResUtils.getResId(this, "ffg_tl", "id"))
//        viewPager = findViewById(ResUtils.getResId(this, "ffg_vp", "id"))
//
//        //使用适配器将ViewPager与Fragment绑定在一起
//        val tabs = resources.getStringArray(ResUtils.getResId(this, "ffg_login_tab", "array"))
//        val pagerAdapter = LoginFragmentPagerAdapter(tabs, supportFragmentManager)
//        viewPager?.adapter = pagerAdapter
//        //讲TabLayout与ViewPager绑定在一起
//        tabLayout?.setupWithViewPager(viewPager)
//
//        leftSelect = ResUtils.getResId(this, "ffg_tab_left_select", "drawable")
//        leftSelected = ResUtils.getResId(this, "ffg_tab_left_selected", "drawable")
//        rightSelect = ResUtils.getResId(this, "ffg_tab_right_select", "drawable")
//        rightSelected = ResUtils.getResId(this, "ffg_tab_right_selected", "drawable")
//        setTabBackground(leftSelected, rightSelect)
//        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                tab?.apply {
//                    when (tabLayout?.selectedTabPosition) {
//                        0 -> setTabBackground(leftSelected, rightSelect)
//                        1 -> setTabBackground(leftSelect, rightSelected)
//
//                    }
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                if (pagerAdapter.currentFragment is ChooseFragment) {
//                    (pagerAdapter.currentFragment as ChooseFragment).hideAccountList()
//                }
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//            }
//
//        })
//    }
//
//    private fun showAutoLoginView() {
//        session = SessionUtils.getInstance().getLocalLastSession(this)
//        session?.apply {
//            if (isAutoLogin) {
//                loginContainer?.visibility = View.GONE
//                autoLoginContainer?.visibility = View.VISIBLE
//                var tips = ""
//                when (loginType) {
//                    LoginType.TYPE_ACCOUNT_LOGIN -> {
//                        var name = userName
//                        if (name.length > 15) {
//                            name = name.substring(0, 15) + "..."
//                        }
//                        tips = name + ResUtils.getResString(this@LoginActivity, "ffg_login_tv_login_account")
//                    }
//                    LoginType.TYPE_FACEBOOK_LOGIN -> tips = ResUtils.getResString(this@LoginActivity, "ffg_login_tv_login_facebook")
//                    LoginType.TYPE_GUEST_LOGIN -> tips = ResUtils.getResString(this@LoginActivity, "ffg_login_tv_login_guest")
//                    LoginType.TYPE_GOOGLE_LOGIN -> tips = ResUtils.getResString(this@LoginActivity, "ffg_login_tv_login_google")
//
//                }
//                tvTips?.text = tips
//                //延迟n秒发起登录请求
//                Logger.d("延迟1.5秒发起登录请求")
//                handler.sendEmptyMessageDelayed(200, 1500)
//            } else {
//                loginContainer?.visibility = View.VISIBLE
//                autoLoginContainer?.visibility = View.GONE
//            }
//        }
//    }
//
//    private fun changeTimeView(time: Int) {
//        if (vcSend?.textView == null) {
//            return
//        }
//        vcSend?.apply {
//            if (time < 0) {
//                textView.text = ResUtils.getResString(this@LoginActivity, "ffg_login_btn_get_captcha")
//                textView.isEnabled = true
//            } else {
//                textView.text = time as String + "s"
//                textView.isEnabled = false
//            }
//        }
//    }
//
//    private fun setTabBackground(left: Int, right: Int) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            val tabStrip = tabLayout?.getChildAt(0) as ViewGroup
//            val tabView1 = tabStrip.getChildAt(0)
//            val tabView2 = tabStrip.getChildAt(1)
//            tabView1?.apply {
//                val paddingStart = paddingStart
//                val paddingTop = paddingTop
//                val paddingEnd = paddingEnd
//                val paddingBottom = paddingBottom
//                ViewCompat.setBackground(this, ContextCompat.getDrawable(this.context, left))
//                ViewCompat.setPaddingRelative(tabView1, paddingStart, paddingTop, paddingEnd, paddingBottom)
//            }
//
//            tabView2?.apply {
//                val paddingStart = tabView2.paddingStart
//                val paddingTop = tabView2.paddingTop
//                val paddingEnd = tabView2.paddingEnd
//                val paddingBottom = tabView2.paddingBottom
//                ViewCompat.setBackground(tabView2, ContextCompat.getDrawable(tabView2.context, right))
//                ViewCompat.setPaddingRelative(tabView2, paddingStart, paddingTop, paddingEnd, paddingBottom)
//            }
//        }
//    }
//
//    private fun getCaptchaCode() {
//        SdkRequest.getInstance().getCaptcha(this, etAccount?.editText?.text.toString(), object : IRequestCallback {
//            override fun onResponse(resultInfo: ResultInfo) {
//                if (resultInfo.code == 0) {
//                    if (TextUtils.isEmpty(resultInfo.msg)) {
//                        Toast.toastInfo(this@LoginActivity, "驗證碼已發送，請注意查收")
//                    } else {
//                        Toast.toastInfo(this@LoginActivity, resultInfo.msg)
//                    }
//                    changeTimeNum()
//                } else {
//                    if (TextUtils.isEmpty(resultInfo.msg)) {
//                        Toast.toastInfo(this@LoginActivity, "驗證碼發送异常，請稍後再試")
//                    } else {
//                        Toast.toastInfo(this@LoginActivity, resultInfo.msg)
//                    }
//                }
//            }
//        })
//    }
//
//    private fun changeTimeNum() {
//        if (!TimeDownUtils.isRunning()) {
//            TimeDownUtils.start {
//                val msg = Message()
//                msg.what = 101
//                msg.obj = it
//                handler.sendMessage(msg)
//            }
//        } else {
//            TimeDownUtils.resetCallback {
//                val msg = Message()
//                msg.what = 101
//                msg.obj = it
//                handler.sendMessage(msg)
//            }
//        }
//    }
//
//    private fun userAutoLogin() {
//        Logger.d("userAutoLogin")
//        session?.apply {
//            when (loginType) {
//                LoginType.TYPE_ACCOUNT_LOGIN -> signInImpl?.accountLogin(userName, pwd)
//                LoginType.TYPE_FACEBOOK_LOGIN -> signInImpl?.facebookLogin(this@LoginActivity)
//                LoginType.TYPE_GUEST_LOGIN -> signInImpl?.guestLogin()
//                LoginType.TYPE_GOOGLE_LOGIN -> signInImpl?.googleLogin(this@LoginActivity)
//            }
//        }
//    }
//
//    fun userLoginVerify(loginParams: JSONObject) {
//        SdkRequest.getInstance().userLoginVerify(this, loginParams, object : IRequestCallback {
//            override fun onResponse(resultInfo: ResultInfo) {
//                loginLoadingDialog?.apply {
//                    if (isShowing) {
//                        dismiss()
//                    }
//
//                    if (resultInfo.code == 0 && !TextUtils.isEmpty(resultInfo.data)) {
//                        try {
//                            val jsonObject = JSONObject(resultInfo.data)
//                            SdkBackLoginInfo.instance.userId = jsonObject.getString("user_id")
//                            SdkBackLoginInfo.instance.timestamp = jsonObject.getString("timestamp")
//                            SdkBackLoginInfo.instance.isRegUser = jsonObject.getInt("is_reg_user")
//                            SdkBackLoginInfo.instance.isBindPlatform = jsonObject.getInt("is_bind_platform")
//                            SdkBackLoginInfo.instance.cpSign = jsonObject.getString("cp_sign")
//                            SdkBackLoginInfo.instance.loginType = loginParams.getInt("login_type")
//
//                            if (session != null) {
//                                session!!.reset()
//                            }
//                            if (session == null) {
//                                session = Session()
//                            }
//                            session!!.userId = SdkBackLoginInfo.instance.userId
//                            session!!.userName = loginParams.getString("user_name")
//                            session!!.pwd = loginParams.getString("pwd")
//                            session!!.loginType = loginParams.getInt("login_type")
//                            SessionUtils.getInstance().saveSession(this@LoginActivity, session)
//                            implcallback?.onSuccess(SdkBackLoginInfo.instance.toJsonString())
//                            finish()
//                        } catch (e: JSONException) {
//                            e.printStackTrace()
//                            implcallback?.onFailed("登录校验异常")
//                        }
//                    } else {
//                        if (!TextUtils.isEmpty(resultInfo.msg)) {
//                            Toast.toastInfo(this@LoginActivity, resultInfo.msg)
//                        }
//                        implcallback?.onFailed("登录校验异常")
//                    }
//                }
//            }
//        })
//    }
//
//    fun userRegister(userName: String, pwd: String) {
//        SdkRequest.getInstance().userRegister(this, userName, pwd, object : IRequestCallback {
//            override fun onResponse(resultInfo: ResultInfo) {
//                if (resultInfo.code == 0 && !TextUtils.isEmpty(resultInfo.data)) {
//                    try {
//                        val jsonObject = JSONObject(resultInfo.data)
//                        SdkBackLoginInfo.instance.userId = jsonObject.getString("user_id")
//                        SdkBackLoginInfo.instance.timestamp = jsonObject.getString("timestamp")
//                        SdkBackLoginInfo.instance.isRegUser = jsonObject.getInt("is_reg_user")
//                        SdkBackLoginInfo.instance.isBindPlatform = jsonObject.getInt("is_bind_platform")
//                        SdkBackLoginInfo.instance.cpSign = jsonObject.getString("cp_sign")
//                        SdkBackLoginInfo.instance.loginType = 0
//
//                        if (session != null) {
//                            session!!.reset()
//                        }
//                        if (session == null) {
//                            session = Session()
//                        }
//                        session?.userId = SdkBackLoginInfo.instance.userId
//                        session?.userName = userName
//                        session?.pwd = pwd
//                        session?.loginType = 0
//                        SessionUtils.getInstance().saveSession(this@LoginActivity, session)
//                        implcallback?.onSuccess(SdkBackLoginInfo.instance.toJsonString())
//                        finish()
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                        Toast.toastInfo(this@LoginActivity, ResUtils.getResString(this@LoginActivity, "ffg_login_register_error"))
//                        implcallback?.onFailed("账号注册异常")
//                    }
//                } else {
//                    if (!TextUtils.isEmpty(resultInfo.msg)) {
//                        Toast.toastInfo(this@LoginActivity, resultInfo.msg)
//                    } else {
//                        Toast.toastInfo(this@LoginActivity, "ffg_login_register_error")
//                    }
//                    implcallback?.onFailed("账号注册异常")
//                }
//            }
//        })
//    }
//
//    private fun doForgetPassword() {
//        val name = etAccount?.editText?.text.toString()
//        val pwd = etPassword?.editText?.text.toString()
//        val pwd2 = etPassword2?.editText?.text.toString()
//        val code = vcSend?.editText?.text.toString()
//        if (TextUtils.isEmpty(name)) {
//            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_empty_account"))
//            return
//        }
//        if (TextUtils.isEmpty(pwd)) {
//            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_empty_password"))
//            return
//        }
//        if (TextUtils.isEmpty(pwd2)) {
//            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_empty_password"))
//            return
//        }
//        if (TextUtils.isEmpty(code)) {
//            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_empty_code"))
//            return
//        }
//
//        if (!EditTextUtils.filterEmail(name)) {
//            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_email_format_error"))
//            return
//        }
//
//        SdkRequest.getInstance().forgetPassword(this, name, pwd, code, object : IRequestCallback {
//            override fun onResponse(resultInfo: ResultInfo) {
//                if (resultInfo.code == 0) {
//                    hideForgetView()
//                    if (TextUtils.isEmpty(resultInfo.msg)) {
//                        return
//                    } else {
//                        Toast.toastInfo(this@LoginActivity, resultInfo.msg)
//                    }
//                    signInImpl?.accountLogin(name, pwd)
//                } else {
//                    if (TextUtils.isEmpty(resultInfo.msg)) {
//                        return
//                    } else {
//                        Toast.toastInfo(this@LoginActivity, resultInfo.msg)
//                    }
//                }
//            }
//        })
//    }
//
//    fun showForgetView() {
//        tabLayout?.visibility = View.GONE
//        viewPager?.visibility = View.GONE
//        forgetContainer?.visibility = View.VISIBLE
//        ivReturn?.visibility = View.VISIBLE
//    }
//
//    fun hideForgetView() {
//        forgetContainer?.visibility = View.GONE
//        ivReturn?.visibility = View.GONE
//        tabLayout?.visibility = View.VISIBLE
//        viewPager?.visibility = View.VISIBLE
//        etAccount?.editText?.setText("")
//        etPassword?.editText?.setText("")
//        etPassword2?.editText?.setText("")
//        if (TimeDownUtils.isRunning()) {
//            TimeDownUtils.cancel()
//            vcSend?.textView?.text = ResUtils.getResString(this, "ffg_login_btn_get_captcha")
//            vcSend?.textView?.isEnabled = true
//        }
//    }
//
//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        ev?.let { motionEvent ->
//            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
//                val v = currentFocus
//
//                v?.apply {
//                    if (isShouldHideInput(v, motionEvent)) {
//                        val imm = this@LoginActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//                        imm.hideSoftInputFromWindow(v.windowToken, 0)
//                    }
//                }
//                return super.dispatchTouchEvent(motionEvent)
//            }
//
//            // 必不可少，否则所有的组件都不会有TouchEvent了
//            if (window.superDispatchTouchEvent(motionEvent)) {
//                return true
//            }
//        }
//        return onTouchEvent(ev)
//    }
//
//    override fun onRestart() {
//        super.onRestart()
//        if (!SdkBridgeImpl.isLandscape && rootContainer != null) {
//            rootContainer?.setBackgroundColor(Color.WHITE)
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        hideBar()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        data?.apply {
//            signInImpl?.onActivityResult(requestCode, resultCode, this)
//        }
//    }
//
//    private fun isShouldHideInput(v: View, event: MotionEvent): Boolean {
//        if (v is EditText) {
//            val leftTop = intArrayOf(0, 0)
//            //获取输入框当前的location位置
//            v.getLocationInWindow(leftTop)
//            val left = leftTop[0]
//            val top = leftTop[1]
//            val bottom = top + v.getHeight()
//            val right = left + v.getWidth()
//            // 点击的是输入框区域，保留点击EditText的事件
//            return event.x <= left || event.x >= right || event.y <= top || event.y >= bottom
//        }
//        return false
//    }
//
//    private fun hideBar() {
//        // The UI options currently enabled are represented by a bitfield.
//        // getSystemUiVisibility() gives us that bitfield.
//        val uiOptions = window.decorView.systemUiVisibility
//        var newUiOptions = uiOptions
//        val isImmersiveModeEnabled = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions
//        if (!isImmersiveModeEnabled) {
////            Log.i(TAG, "Turning immersive mode mode on. ");
//            if (Build.VERSION.SDK_INT >= 14) {
//                newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//            }
//            if (Build.VERSION.SDK_INT >= 16) {
//                newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN
//            }
//            if (Build.VERSION.SDK_INT >= 18) {
//                newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//            }
//            window.decorView.systemUiVisibility = newUiOptions
//        }
//    }
//}