package cn.flyfun.gamesdk.core.impl

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import cn.flyfun.gamesdk.core.entity.*
import cn.flyfun.gamesdk.core.internal.IRequestCallback
import cn.flyfun.gamesdk.core.internal.ImplCallback
import cn.flyfun.gamesdk.core.network.SdkRequest
import cn.flyfun.gamesdk.core.ui.DialogUtils
import cn.flyfun.gamesdk.core.ui.EventEditText
import cn.flyfun.gamesdk.core.utils.AndroidBug5497Workaround
import cn.flyfun.gamesdk.core.utils.SessionUtils
import cn.flyfun.support.ResUtils
import cn.flyfun.support.jarvis.Toast
import cn.flyfun.support.ui.circleprogress.CircleProgressLoadingDialog
import org.json.JSONException
import org.json.JSONObject


/**
 * @author #Suyghur.
 * Created on 2020/12/3
 */
class BindActivity : Activity(), View.OnClickListener, EventEditText.EventEditTextListener {
    private lateinit var ivClose: ImageView
    private lateinit var etAccount: EventEditText
    private lateinit var etPassword: EventEditText
    private lateinit var btnBind: Button
    private var imageShow = 0
    private var imageHide: Int = 0
    private var isShowText = false
    private var loadingDialog: CircleProgressLoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        if (SdkBridgeImpl.isLandscape) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
        setContentView(ResUtils.getResId(this, "ffg_bind", "layout"))
        if (!SdkBridgeImpl.isLandscape) {
            AndroidBug5497Workaround.assistActivity(this)
        }

        imageShow = ResUtils.getResId(this, "ffg_show_img", "drawable")
        imageHide = ResUtils.getResId(this, "ffg_hide_img", "drawable")

        ivClose = findViewById(ResUtils.getResId(this, "ffg_iv_close", "id"))
        val tvTitle: TextView = findViewById(ResUtils.getResId(this, "ffg_tv_title", "id"))

        when (SdkBackLoginInfo.instance.loginType) {
            LoginType.TYPE_FACEBOOK_LOGIN ->
                tvTitle.text = ResUtils.getResString(this, "ffg_bind_tv_facebook")
            LoginType.TYPE_GUEST_LOGIN ->
                tvTitle.text = ResUtils.getResString(this, "ffg_bind_tv_guest")
            LoginType.TYPE_GOOGLE_LOGIN ->
                tvTitle.text = ResUtils.getResString(this, "ffg_bind_tv_google")
        }

        etAccount = findViewById(ResUtils.getResId(this, "ffg_et_account", "id"))
        etAccount.apply {
            leftImageView.setBackgroundResource(
                ResUtils.getResId(
                    this@BindActivity,
                    "ffg_account_img",
                    "drawable"
                )
            )
            editText.setHint(
                ResUtils.getResId(
                    this@BindActivity,
                    "ffg_login_tv_email_hint",
                    "string"
                )
            )
        }

        etPassword = findViewById(ResUtils.getResId(this, "ffg_et_pwd", "id"))
        etPassword.apply {
            leftImageView.setBackgroundResource(
                ResUtils.getResId(
                    this@BindActivity,
                    "ffg_password_img",
                    "drawable"
                )
            )
            rightImageView.setBackgroundResource(imageShow)
            editText.setHint(
                ResUtils.getResId(
                    this@BindActivity,
                    "ffg_login_password_hint",
                    "string"
                )
            )
            editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        }

        btnBind = findViewById(ResUtils.getResId(this, "ffg_btn_bind", "id"))

        initViewAction()

        initViewListener()
    }

    private fun initViewAction() {
        ivClose.tag = ClickType.ACTION_CLOSE
        btnBind.tag = ClickType.ACTION_BIND_ACCOUNT
    }

    private fun initViewListener() {
        ivClose.setOnClickListener(this)
        btnBind.setOnClickListener(this)
        etPassword.eventEditTextListener = this
    }

    private fun doBind() {
        try {
            showLoadingDialog()
            val jsonObject = JSONObject()
            jsonObject.put("bind_type", SdkBackLoginInfo.instance.loginType)
            jsonObject.put("user_name", etAccount.editText.text.toString())
            jsonObject.put("pwd", etPassword.editText.text.toString())
            jsonObject.put("user_id", SdkBackLoginInfo.instance.userId)
            doBindAccount(jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun doBindAccount(jsonObject: JSONObject) {
        SdkRequest.getInstance().userBind(this, jsonObject, object : IRequestCallback {
            override fun onResponse(resultInfo: ResultInfo) {
                if (resultInfo.code == 0) {
                    try {
                        val session = Session()
                        session.userId = SdkBackLoginInfo.instance.userId
                        session.userName = jsonObject.getString("user_name")
                        session.pwd = jsonObject.getString("pwd")
                        session.loginType = 0
                        SessionUtils.getInstance().saveSession(this@BindActivity, session)
                        val tips = this@BindActivity.resources.getString(
                            ResUtils.getResId(
                                this@BindActivity,
                                "ffg_bind_tv_success",
                                "string"
                            )
                        )
                        Toast.toastInfo(this@BindActivity, tips)
                        callback?.onSuccess("绑定成功")
                        hideLoadingDialog()
                        finish()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        callback?.onFailed("绑定失败")
                        hideLoadingDialog()
                    }
                } else {
                    if (TextUtils.isEmpty(resultInfo.msg)) {
                        val tips = this@BindActivity.resources.getString(
                            ResUtils.getResId(
                                this@BindActivity,
                                "ffg_bind_tv_fail",
                                "string"
                            )
                        )
                        Toast.toastInfo(this@BindActivity, tips)
                    } else {
                        Toast.toastInfo(this@BindActivity, resultInfo.msg)
                    }
                    callback?.onFailed("绑定失败")
                    hideLoadingDialog()
                }
            }

        })
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            val view = this@BindActivity.currentFocus
            view?.apply {
                if (isShouldHideInput(this, it)) {
                    (this@BindActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        this.windowToken,
                        0
                    )
                }
                return super.dispatchTouchEvent(it)
            }
        }
        //必不可少，否则所有的组件都不会有TouchEvent了
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)
    }

    private fun isShouldHideInput(view: View, event: MotionEvent): Boolean {
        if (view is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            view.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + view.getHeight()
            val right = left + view.getWidth()
            // 点击的是输入框区域，保留点击EditText的事件
            return event.x <= left || event.x >= right || event.y <= top || event.y >= bottom
        }
        return false
    }


    override fun beforeTextChanged(v: View, s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(v: View, s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(v: View, s: Editable?) {
    }

    override fun onViewClick(v: View?) {
        v?.apply {
            if (this == etPassword?.rightImageView) {
                if (!isShowText) {
                    isShowText = true
                    etPassword!!.rightImageView.setBackgroundResource(imageHide)
                    etPassword!!.editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                } else {
                    isShowText = false
                    etPassword!!.rightImageView.setBackgroundResource(imageShow)
                    etPassword!!.editText.inputType =
                        InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                }
                etPassword?.editText?.setSelection(etPassword!!.editText.length())
            }
        }
    }

    private fun showLoadingDialog() {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
            loadingDialog = null
        }
        loadingDialog = DialogUtils.showCircleProgressLoadingDialog(this@BindActivity, "")
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
            loadingDialog = null
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {

    }


    override fun onClick(v: View?) {
        v?.apply {
            when (tag as Int) {
                ClickType.ACTION_CLOSE -> finish()
                ClickType.ACTION_BIND_ACCOUNT -> doBind()
            }
        }
    }

    companion object {
        fun bind(activity: Activity, callback: ImplCallback) {
            val intent = Intent(activity, BindActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
            Companion.callback = callback
        }

        private var callback: ImplCallback? = null
    }

}