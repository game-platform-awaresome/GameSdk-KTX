package cn.flyfun.ktx.gamesdk.core.impl.login.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import cn.flyfun.ktx.gamesdk.core.impl.login.LoginActivity;
import cn.flyfun.ktx.gamesdk.core.ui.EventEditText;
import cn.flyfun.support.EditTextUtils;
import cn.flyfun.support.ResUtils;
import cn.flyfun.support.jarvis.Toast;

/**
 * @author #Suyghur.
 * Created on 10/27/20
 */
public class RegisterFragment extends Fragment {

    private LoginActivity loginImpl;
    private View view;
    private EventEditText etAccount, etPassword;
    private int imageShow, imageHide;
    private boolean isShowText = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        loginImpl = (LoginActivity) getActivity();
        if (view == null) {
            view = inflater.inflate(ResUtils.getResId(getActivity(), "ffg_login_register", "layout"), container, false);
            initView(view);
        }
        return view;
    }

    private void initView(View view) {

        imageShow = ResUtils.getResId(getActivity(), "ffg_show_img", "drawable");
        imageHide = ResUtils.getResId(getActivity(), "ffg_hide_img", "drawable");

        etAccount = view.findViewById(ResUtils.getResId(getActivity(), "ffg_et_account", "id"));
        etAccount.getLeftImageView().setBackgroundResource(ResUtils.getResId(getActivity(), "ffg_account_img", "drawable"));
        etAccount.getEditText().setHint(ResUtils.getResId(getActivity(), "ffg_login_tv_email_hint", "string"));

        etPassword = view.findViewById(ResUtils.getResId(getActivity(), "ffg_et_pwd", "id"));
        etPassword.getLeftImageView().setBackgroundResource(ResUtils.getResId(getActivity(), "ffg_password_img", "drawable"));
        etPassword.getRightImageView().setBackgroundResource(imageShow);
        etPassword.getEditText().setHint(ResUtils.getResId(getActivity(), "ffg_login_password_hint", "string"));
        etPassword.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);

        etPassword.setEventEditTextListener(new EventEditText.EventEditTextListener() {
            @Override
            public void beforeTextChanged(View v, CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(View v, CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(View v, Editable s) {

            }

            @Override
            public void onViewClick(View v) {
                if (v == etPassword.getRightImageView()) {
                    if (!isShowText) {
                        isShowText = true;
                        etPassword.getRightImageView().setBackgroundResource(imageHide);
                        etPassword.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        isShowText = false;
                        etPassword.getRightImageView().setBackgroundResource(imageShow);
                        etPassword.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    }
                    etPassword.getEditText().setSelection(etPassword.getEditText().length());
                }
            }

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        Button btnRegister = view.findViewById(ResUtils.getResId(getActivity(), "ffg_btn_register", "id"));
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = etAccount.getEditText().getText().toString();
                String pwd = etPassword.getEditText().getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    Toast.toastInfo(getActivity(), getResources().getString(ResUtils.getResId(getActivity(), "ffg_tips_empty_email", "string")));
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    Toast.toastInfo(getActivity(), getResources().getString(ResUtils.getResId(getActivity(), "ffg_tips_empty_password", "string")));
                    return;
                }
                if (!EditTextUtils.filterEmail(userName)) {
                    Toast.toastInfo(getActivity(), getResources().getString(ResUtils.getResId(getActivity(), "ffg_tips_email_format_error", "string")));
                    return;
                }
                if (pwd.length() <= 5) {
                    Toast.toastInfo(getActivity(), getResources().getString(ResUtils.getResId(getActivity(), "ffg_tips_password_format_error", "string")));
                    return;
                }
                loginImpl.userRegister(userName, pwd);
            }
        });
    }
}
