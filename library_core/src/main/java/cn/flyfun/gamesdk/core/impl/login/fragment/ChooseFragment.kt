package cn.flyfun.gamesdk.core.impl.login.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.entity.ClickType
import cn.flyfun.gamesdk.core.entity.Session
import cn.flyfun.gamesdk.core.impl.SdkBridgeImpl
import cn.flyfun.gamesdk.core.impl.login.LoginActivity
import cn.flyfun.gamesdk.core.ui.EventEditText
import cn.flyfun.gamesdk.core.ui.dialog.PrivacyDialog
import cn.flyfun.gamesdk.core.utils.SessionUtils
import cn.flyfun.support.LocaleUtils
import cn.flyfun.support.ResUtils
import cn.flyfun.support.jarvis.Toast


/**
 * @author #Suyghur.
 * Created on 2020/12/7
 */
class ChooseFragment : Fragment(), View.OnClickListener {

    private lateinit var loginImpl: LoginActivity
    private lateinit var mInflater: LayoutInflater
    private lateinit var mView: View
    private lateinit var clAgreement: ConstraintLayout
    private lateinit var llAccountList: LinearLayout
    private lateinit var rlAccountList: RelativeLayout
    private lateinit var svAccountList: ScrollView
    private lateinit var ivCheck: ImageView
    private lateinit var ivGuest: ImageView
    private lateinit var ivGoogle: ImageView
    private lateinit var ivFacebook: ImageView
    private lateinit var etAccount: EventEditText
    private lateinit var etPassword: EventEditText
    private lateinit var tvForget: TextView
    private lateinit var btnLogin: Button
    private var imageCheck = 0
    private var imageUnCheck: Int = 0
    private var imageUp: Int = 0
    private var imageDown: Int = 0
    private var imageShow: Int = 0
    private var imageHide: Int = 0
    private var imageGuestEN: Int = 0
    private var imageGuestCN: Int = 0
    private var imageGuestHK: Int = 0
    private var userLists: ArrayList<Session>? = null
    private var check = true
    private var isShowText = false
    private var privacyDialog: PrivacyDialog? = null
    private val mEventEditTextListener = object : EventEditText.EventEditTextListener {
        override fun beforeTextChanged(
            v: View,
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(v: View, s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(v: View, s: Editable?) {
        }

        override fun onViewClick(v: View?) {
            v?.apply {
                if (v == etAccount.rightImageView) {
                    changeAccountList()
                } else if (v == etPassword.rightImageView) {
                    if (!isShowText) {
                        isShowText = true
                        etPassword.rightImageView.setBackgroundResource(imageHide)
                        etPassword.editText.inputType =
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        isShowText = false
                        etPassword.rightImageView.setBackgroundResource(imageShow)
                        etPassword.editText.inputType =
                            InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                    }
                    etPassword.editText.setSelection(etPassword.editText.length())
                }
            }
        }

        override fun onFocusChange(v: View?, hasFocus: Boolean) {
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        loginImpl = requireActivity() as LoginActivity
        mInflater = inflater
        mView = inflater.inflate(
            ResUtils.getResId(requireActivity(), "ffg_login_choose", "layout"),
            container,
            false
        )
        initView(mView)
        return mView
    }

    private fun initView(view: View) {
        ivCheck = view.findViewById(ResUtils.getResId(requireActivity(), "ffg_iv_check", "id"))
        ivGoogle = view.findViewById(ResUtils.getResId(requireActivity(), "ffg_iv_google", "id"))
        ivGuest = view.findViewById(ResUtils.getResId(requireActivity(), "ffg_iv_guest", "id"))
        ivFacebook =
            view.findViewById(ResUtils.getResId(requireActivity(), "ffg_iv_facebook", "id"))

        imageUp = ResUtils.getResId(requireActivity(), "ffg_pack_up_img", "drawable")
        imageDown = ResUtils.getResId(requireActivity(), "ffg_pack_down_img", "drawable")
        imageShow = ResUtils.getResId(requireActivity(), "ffg_show_img", "drawable")
        imageHide = ResUtils.getResId(requireActivity(), "ffg_hide_img", "drawable")
        imageCheck = ResUtils.getResId(requireActivity(), "ffg_checked_img", "drawable")
        imageUnCheck = ResUtils.getResId(requireActivity(), "ffg_check_img", "drawable")
        imageGuestEN = ResUtils.getResId(requireActivity(), "ffg_guest_en_img", "drawable")
        imageGuestCN = ResUtils.getResId(requireActivity(), "ffg_guest_cn_img", "drawable")
        imageGuestHK = ResUtils.getResId(requireActivity(), "ffg_guest_hk_img", "drawable")

        val language = LocaleUtils.getLocaleCountry(requireActivity())
        ivGuest.apply {
            when (language) {
                "EN" -> setBackgroundResource(imageGuestEN)
                "CN" -> setBackgroundResource(imageGuestCN)
                else -> setBackgroundResource(imageGuestHK)
            }
        }

        etAccount = view.findViewById(ResUtils.getResId(requireActivity(), "ffg_et_account", "id"))
        etAccount.apply {
            leftImageView.setBackgroundResource(
                ResUtils.getResId(
                    requireActivity(),
                    "ffg_account_img",
                    "drawable"
                )
            )
            rightImageView.setBackgroundResource(imageDown)
            editText.setHint(
                ResUtils.getResId(
                    requireActivity(),
                    "ffg_login_account_hint",
                    "string"
                )
            )
        }

        etPassword = view.findViewById(ResUtils.getResId(requireActivity(), "ffg_et_pwd", "id"))
        etPassword.apply {
            leftImageView.setBackgroundResource(
                ResUtils.getResId(
                    requireActivity(),
                    "ffg_password_img",
                    "drawable"
                )
            )
            rightImageView.setBackgroundResource(imageShow)
            editText.setHint(
                ResUtils.getResId(
                    requireActivity(),
                    "ffg_login_password1_hint",
                    "string"
                )
            )
            editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        }

        tvForget = view.findViewById(ResUtils.getResId(requireActivity(), "ffg_tv_forget", "id"))
        clAgreement =
            view.findViewById(ResUtils.getResId(requireActivity(), "ffg_cl_agreement", "id"))
        btnLogin = view.findViewById(ResUtils.getResId(requireActivity(), "ffg_btn_login", "id"))


        rlAccountList =
            view.findViewById(ResUtils.getResId(requireActivity(), "ffg_rl_account_list", "id"))
        svAccountList =
            view.findViewById(ResUtils.getResId(requireActivity(), "ffg_sv_account_list", "id"))
        llAccountList =
            view.findViewById(ResUtils.getResId(requireActivity(), "ffg_ll_account_list", "id"))
        rlAccountList.visibility = View.GONE

        if (SdkBridgeImpl.initBean.initPrivacy.privacySwitch == 0) {
            ivCheck.visibility = View.GONE
            clAgreement.visibility = View.GONE
        }

        initViewAction()
        initViewListener()
        autoFillUserInfo()
    }

    private fun initViewAction() {
        ivCheck.tag = ClickType.ACTION_CLICK_CHECK
        btnLogin.tag = ClickType.ACTION_LOGIN
        tvForget.tag = ClickType.ACTION_FORGET
        clAgreement.tag = ClickType.ACTION_CLICK_AGREEMENT
        ivGoogle.tag = ClickType.ACTION_GOOGLE_MODE
        ivGuest.tag = ClickType.ACTION_GUEST_MODE
        ivFacebook.tag = ClickType.ACTION_FACEBOOK_MODE
    }

    private fun initViewListener() {
        btnLogin.setOnClickListener(this)
        ivCheck.setOnClickListener(this)
        ivGoogle.setOnClickListener(this)
        ivGuest.setOnClickListener(this)
        ivFacebook.setOnClickListener(this)
        tvForget.setOnClickListener(this)
        etAccount.eventEditTextListener = mEventEditTextListener
        etPassword.eventEditTextListener = mEventEditTextListener
        clAgreement.setOnClickListener(this)
    }

    private fun autoFillUserInfo() {
        val temp: MutableList<Session> =
            SessionUtils.getInstance().getLocalSessionLimit5(requireActivity())
        if (temp.size == 0) {
            etAccount.rightImageView.visibility = View.GONE
            return
        } else {
            temp.forEach { session ->
                if (session.loginType != 0) {
                    return@forEach
                }
                if (userLists == null) {
                    userLists = ArrayList()
                }
                Logger.d(session.toString())
                userLists!!.add(session)
            }

            userLists?.apply {
                if (size > 0) {
                    val session = this[0]
                    etAccount.editText.setText(session.userName)
                    etPassword.editText.setText(session.pwd)
                }
                if (size > 1) {
                    initAccountList()
                    etAccount.rightImageView.visibility = View.VISIBLE
                } else {
                    etAccount.rightImageView.visibility = View.GONE
                    rlAccountList.visibility = View.GONE
                }
            }

            if (userLists == null || userLists?.size!! <= 0) {
                etAccount.rightImageView.visibility = View.GONE
            }
        }
    }

    private fun changeAccountList() {
        if (rlAccountList.visibility == View.GONE) {
            etAccount.rightImageView.setBackgroundResource(imageUp)
            rlAccountList.visibility = View.VISIBLE
        } else {
            etAccount.rightImageView.setBackgroundResource(imageDown)
            rlAccountList.visibility = View.GONE
        }
    }

    private fun initAccountList() {
        llAccountList.removeAllViews()
        //单行高
        var height = 100
        etAccount.apply {
            if (layoutParams != null) {
                height = layoutParams!!.height
            }
        }
        //ScrollView最大高度
        var maxHeight = 0
        userLists?.apply {
            if (size > 0) {
                maxHeight = if (size < 5) {
                    size * height
                } else {
                    (size - 1) * height
                }
            }
        }
        //循环添加列
        userLists?.apply {
            for (i in this.indices) {
                val session = this[i]
                //添加线
                val line = View(requireActivity())
                line.layoutParams =
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2)
                line.setBackgroundColor(Color.parseColor("#4D505050"))
                llAccountList.apply {
                    if (childCount > 0) {
                        addView(line)
                    }
                }

                //添加账号
                val item = mInflater.inflate(
                    ResUtils.getResId(
                        requireActivity(),
                        "ffg_account_list_item",
                        "layout"
                    ), null
                )
                item?.apply {
                    val tvAccountName = findViewById<TextView>(
                        ResUtils.getResId(
                            requireActivity(),
                            "ffg_tv_name",
                            "id"
                        )
                    )
                    tvAccountName.gravity = Gravity.CENTER_VERTICAL
                    tvAccountName.setPadding(15, 0, 0, 0)
                    tvAccountName.text = session.userName
                    val ivDelete = findViewById<ImageView>(
                        ResUtils.getResId(
                            requireActivity(),
                            "ffg_iv_delete",
                            "id"
                        )
                    )
                    setOnClickListener(AccountItemClick(session))
                    ivDelete.setOnClickListener(AccountDeleteClick(session))
                    layoutParams =
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
                    llAccountList.addView(this, llAccountList.childCount)
                }
            }
        }
        svAccountList.layoutParams =
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, maxHeight)
    }

    fun hideAccountList(): Boolean {
        userLists?.let {
            if (it.size > 0 && rlAccountList.visibility == View.VISIBLE) {
                etAccount.rightImageView.setBackgroundResource(imageDown)
                rlAccountList.visibility = View.GONE
                return true
            }
        }
        return false
    }

    /**
     * 切换勾选/不勾选图片
     */
    private fun changeCheck() {
        if (check) {
            check = false
            ivCheck.setBackgroundResource(imageUnCheck)
        } else {
            check = true
            ivCheck.setBackgroundResource(imageCheck)
        }
    }

    override fun onClick(v: View?) {
        v?.apply {
            when (this.tag as Int) {
                ClickType.ACTION_FORGET -> loginImpl.showForgetView()
                ClickType.ACTION_CLICK_CHECK -> changeCheck()
                ClickType.ACTION_LOGIN -> {
                    val userName = etAccount.editText.text.toString()
                    val pwd = etPassword.editText.text.toString()
                    if (TextUtils.isEmpty(userName)) {
                        Toast.toastInfo(
                            requireActivity(),
                            ResUtils.getResString(requireActivity(), "ffg_tips_empty_account")
                        )
                        return
                    }
                    if (TextUtils.isEmpty(pwd)) {
                        Toast.toastInfo(
                            requireActivity(),
                            ResUtils.getResString(requireActivity(), "ffg_tips_empty_password")
                        )
                        return
                    }
                    loginImpl.signInImpl?.accountLogin(userName, pwd)
                }
                ClickType.ACTION_FACEBOOK_MODE -> loginImpl.signInImpl?.facebookLogin(
                    requireActivity()
                )
                ClickType.ACTION_GOOGLE_MODE -> loginImpl.signInImpl?.googleLogin(requireActivity())
                ClickType.ACTION_GUEST_MODE -> loginImpl.signInImpl?.guestLogin()
                ClickType.ACTION_CLICK_AGREEMENT -> {
                    privacyDialog?.apply {
                        if (isShowing) {
                            dismiss()
                            privacyDialog = null
                        }
                    }

                    if (!TextUtils.isEmpty(SdkBridgeImpl.initBean.initPrivacy.url)) {
                        privacyDialog =
                            PrivacyDialog(requireActivity(), SdkBridgeImpl.initBean.initPrivacy.url)
                        privacyDialog?.show()
                    }
                }
            }
        }
    }


    /**
     * 账号列表item点击事件
     */
    private inner class AccountItemClick(val session: Session) : View.OnClickListener {
        override fun onClick(v: View?) {
            etAccount.apply {
                rightImageView.setBackgroundResource(imageUp)
                hideAccountList()
                editText.setText(session.userName)
                etPassword.editText.setText(session.pwd)
            }
        }
    }

    /**
     * 删除点击事件
     */
    private inner class AccountDeleteClick(val session: Session) : View.OnClickListener {
        override fun onClick(v: View?) {
            SessionUtils.getInstance().deleteUserInfo(requireContext(), session.userId)
            userLists = null
            autoFillUserInfo()
        }
    }
}