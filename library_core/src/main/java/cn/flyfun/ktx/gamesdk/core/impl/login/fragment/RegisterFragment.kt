package cn.flyfun.ktx.gamesdk.core.impl.login.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import cn.flyfun.ktx.gamesdk.core.impl.login.LoginActivity
import cn.flyfun.ktx.gamesdk.core.ui.EventEditText
import cn.flyfun.support.EditTextUtils
import cn.flyfun.support.ResUtils
import cn.flyfun.support.jarvis.Toast

/**
 * @author #Suyghur.
 * Created on 2020/12/8
 */
class RegisterFragment : Fragment() {

    private var loginImpl: LoginActivity? = null
    private var mView: View? = null
    private var etAccount: EventEditText? = null
    private var etPassword: EventEditText? = null
    private var isShowText: Boolean = false
    private var imageShow = 0
    private var imageHide = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        loginImpl = requireActivity() as LoginActivity
        if (mView == null) {
            mView = inflater.inflate(ResUtils.getResId(requireActivity(), "ffg_login_register", "layout"), container, false)
            initView(mView!!)
        }
        return mView
    }

    private fun initView(view: View) {
        imageShow = ResUtils.getResId(requireActivity(), "ffg_show_img", "drawable")
        imageHide = ResUtils.getResId(requireActivity(), "ffg_hide_img", "drawable")

        etAccount = view.findViewById(ResUtils.getResId(requireActivity(), "ffg_et_account", "id"))
        etAccount?.apply {
            leftImageView.setBackgroundResource(ResUtils.getResId(requireActivity(), "ffg_account_img", "drawable"))
            editText.hint = ResUtils.getResString(requireActivity(), "ffg_login_tv_email_hint")
        }

        etPassword = view.findViewById(ResUtils.getResId(requireActivity(), "ffg_et_pwd", "id"))
        etPassword?.apply {
            leftImageView.setBackgroundResource(ResUtils.getResId(requireActivity(), "ffg_password_img", "drawable"))
            rightImageView.setBackgroundResource(imageShow)
            editText.hint = ResUtils.getResString(requireActivity(), "ffg_login_password_hint")
            editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
            eventEditTextListener = object : EventEditText.EventEditTextListener {
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
                                etPassword?.rightImageView?.setBackgroundResource(imageHide)
                                etPassword?.editText?.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            } else {
                                isShowText = false
                                etPassword?.rightImageView?.setBackgroundResource(imageShow)
                                etPassword?.editText?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                            }
                            etPassword?.editText?.setSelection(etPassword!!.editText.length())
                        }
                    }
                }

                override fun onFocusChange(v: View?, hasFocus: Boolean) {
                }
            }

            val btnRegister = view.findViewById<Button>(ResUtils.getResId(requireActivity(), "ffg_btn_register", "id"))
            btnRegister.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    v?.apply {
                        loginImpl?.showLoadingDialog()
                        val userName = etAccount?.editText?.text.toString()
                        val pwd = etPassword?.editText?.text.toString()
                        if (TextUtils.isEmpty(userName)) {
                            Toast.toastInfo(requireActivity(), resources.getString(ResUtils.getResId(requireActivity(), "ffg_tips_empty_email", "string")))
                            return
                        }
                        if (TextUtils.isEmpty(pwd)) {
                            Toast.toastInfo(requireActivity(), resources.getString(ResUtils.getResId(requireActivity(), "ffg_tips_empty_password", "string")))
                            return
                        }
                        if (!EditTextUtils.filterEmail(userName)) {
                            Toast.toastInfo(requireActivity(), resources.getString(ResUtils.getResId(requireActivity(), "ffg_tips_email_format_error", "string")))
                            return
                        }
                        if (pwd.length <= 5) {
                            Toast.toastInfo(requireActivity(), resources.getString(ResUtils.getResId(requireActivity(), "ffg_tips_password_format_error", "string")))
                            return
                        }
                        loginImpl?.userRegister(userName, pwd)
                    }
                }
            })
        }
    }
}