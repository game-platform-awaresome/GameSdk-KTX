package cn.flyfun.ktx.gamesdk.core.impl.login.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import cn.flyfun.ktx.gamesdk.base.utils.Logger;
import cn.flyfun.ktx.gamesdk.core.entity.ClickType;
import cn.flyfun.ktx.gamesdk.core.entity.Session;
import cn.flyfun.ktx.gamesdk.core.impl.SdkBridgeImpl;
import cn.flyfun.ktx.gamesdk.core.impl.login.LoginActivity;
import cn.flyfun.ktx.gamesdk.core.ui.EventEditText;
import cn.flyfun.ktx.gamesdk.core.ui.dialog.PrivacyDialog;
import cn.flyfun.ktx.gamesdk.core.utils.SessionUtils;
import cn.flyfun.support.ResUtils;
import cn.flyfun.support.jarvis.Toast;

/**
 * @author #Suyghur.
 * Created on 10/27/20
 */
public class ChooseFragment extends Fragment implements View.OnClickListener {

    private LayoutInflater layoutInflater;
    private LoginActivity loginImpl;
    private View view;
    private ConstraintLayout clAgreement;
    private LinearLayout llAccountList;
    private RelativeLayout rlAccountList;
    private ScrollView svAccountList;
    private ImageView ivCheck, ivGuest, ivGoogle, ivFacebook;
    private EventEditText etAccount, etPassword;
    private TextView tvForget;
    private Button btnLogin;
    private int imageCheck, imageUnCheck, imageUp, imageDown, imageShow, imageHide;
    private ArrayList<Session> userLists;
    private boolean check = true;
    private boolean isShowText = false;
    private Dialog privacyDialog;


    private final EventEditText.EventEditTextListener mEventEditTextListener = new EventEditText.EventEditTextListener() {
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
            if (v == etAccount.getRightImageView()) {
                changeAccountList();
            } else if (v == etPassword.getRightImageView()) {
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
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        loginImpl = (LoginActivity) getActivity();
        layoutInflater = inflater;
        if (view == null) {
            view = inflater.inflate(ResUtils.getResId(getActivity(), "ffg_login_choose", "layout"), container, false);
            initView(view);
        }
        return view;
    }

    private void initView(View view) {

        ivCheck = view.findViewById(ResUtils.getResId(getActivity(), "ffg_iv_check", "id"));
        ivGoogle = view.findViewById(ResUtils.getResId(getActivity(), "ffg_iv_google", "id"));
        ivGuest = view.findViewById(ResUtils.getResId(getActivity(), "ffg_iv_guest", "id"));
        ivFacebook = view.findViewById(ResUtils.getResId(getActivity(), "ffg_iv_facebook", "id"));

        imageUp = ResUtils.getResId(getActivity(), "ffg_pack_up_img", "drawable");
        imageDown = ResUtils.getResId(getActivity(), "ffg_pack_down_img", "drawable");
        imageShow = ResUtils.getResId(getActivity(), "ffg_show_img", "drawable");
        imageHide = ResUtils.getResId(getActivity(), "ffg_hide_img", "drawable");
        imageCheck = ResUtils.getResId(getActivity(), "ffg_checked_img", "drawable");
        imageUnCheck = ResUtils.getResId(getActivity(), "ffg_check_img", "drawable");

        etAccount = view.findViewById(ResUtils.getResId(getActivity(), "ffg_et_account", "id"));
        etAccount.getLeftImageView().setBackgroundResource(ResUtils.getResId(getActivity(), "ffg_account_img", "drawable"));
        etAccount.getRightImageView().setBackgroundResource(imageDown);
        etAccount.getEditText().setHint(ResUtils.getResId(getActivity(), "ffg_login_account_hint", "string"));

        etPassword = view.findViewById(ResUtils.getResId(getActivity(), "ffg_et_pwd", "id"));
        etPassword.getLeftImageView().setBackgroundResource(ResUtils.getResId(getActivity(), "ffg_password_img", "drawable"));
        etPassword.getRightImageView().setBackgroundResource(imageShow);
        etPassword.getEditText().setHint(ResUtils.getResId(getActivity(), "ffg_login_password1_hint", "string"));
        etPassword.getEditText().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);

        tvForget = view.findViewById(ResUtils.getResId(getActivity(), "ffg_tv_forget", "id"));
        clAgreement = view.findViewById(ResUtils.getResId(getActivity(), "ffg_cl_agreement", "id"));
        btnLogin = view.findViewById(ResUtils.getResId(getActivity(), "ffg_btn_login", "id"));

        rlAccountList = view.findViewById(ResUtils.getResId(getActivity(), "ffg_rl_account_list", "id"));
        svAccountList = view.findViewById(ResUtils.getResId(getActivity(), "ffg_sv_account_list", "id"));
        llAccountList = view.findViewById(ResUtils.getResId(getActivity(), "ffg_ll_account_list", "id"));
        rlAccountList.setVisibility(View.GONE);

        if (SdkBridgeImpl.initBean != null && SdkBridgeImpl.initBean.initPrivacy.privacySwitch == 0) {
            ivCheck.setVisibility(View.GONE);
            clAgreement.setVisibility(View.GONE);
        }

        initViewAction();
        initViewListener();
        autoFillUserInfo();
    }

    private void initViewAction() {
        ivCheck.setTag(ClickType.ACTION_CLICK_CHECK);
        btnLogin.setTag(ClickType.ACTION_LOGIN);
        tvForget.setTag(ClickType.ACTION_FORGET);
        clAgreement.setTag(ClickType.ACTION_CLICK_AGREEMENT);
        ivGoogle.setTag(ClickType.ACTION_GOOGLE_MODE);
        ivGuest.setTag(ClickType.ACTION_GUEST_MODE);
        ivFacebook.setTag(ClickType.ACTION_FACEBOOK_MODE);
    }

    private void initViewListener() {
        btnLogin.setOnClickListener(this);
        ivCheck.setOnClickListener(this);
        ivGoogle.setOnClickListener(this);
        ivGuest.setOnClickListener(this);
        ivFacebook.setOnClickListener(this);
        tvForget.setOnClickListener(this);
        etAccount.setEventEditTextListener(mEventEditTextListener);
        etPassword.setEventEditTextListener(mEventEditTextListener);
        clAgreement.setOnClickListener(this);
    }

    private void autoFillUserInfo() {
        ArrayList<Session> temp = SessionUtils.getInstance().getLocalSessionLimit5(getActivity());
        if (temp == null || temp.size() == 0) {
            etAccount.getRightImageView().setVisibility(View.GONE);
            return;
        }
        for (Session session : temp) {
            if (session.getLoginType() != 0) {
                continue;
            }
            if (userLists == null) {
                userLists = new ArrayList<>();
            }
            Logger.d(session.toString());
            userLists.add(session);
        }
        if (userLists != null && userLists.size() > 0) {
            Session session = userLists.get(0);
            if (session != null) {
                etAccount.getEditText().setText(session.getUserName());
                etPassword.getEditText().setText(session.getPwd());
            }
            if (userLists.size() > 1) {
                initAccountList();
                etAccount.getRightImageView().setVisibility(View.VISIBLE);
            } else {
                etAccount.getRightImageView().setVisibility(View.GONE);
                rlAccountList.setVisibility(View.GONE);
            }
        } else {
            etAccount.getRightImageView().setVisibility(View.GONE);
        }
    }


    public void changeAccountList() {
        if (rlAccountList.getVisibility() == View.GONE) {
            etAccount.getRightImageView().setBackgroundResource(imageUp);
            rlAccountList.setVisibility(View.VISIBLE);
        } else {
            etAccount.getRightImageView().setBackgroundResource(imageDown);
            rlAccountList.setVisibility(View.GONE);
        }
    }


    private void initAccountList() {
        llAccountList.removeAllViews();
        //单行高
        int height = 100;
        if (etAccount != null && etAccount.getLayoutParams() != null) {
            if (etAccount.getLayoutParams().height != -1 || etAccount.getLayoutParams().height != 0) {
                height = etAccount.getLayoutParams().height;
            }
        }
        //ScrollView最大高度
        int maxHeight = 0;
        if (userLists != null && userLists.size() > 0) {
            if (userLists.size() < 5) {
                maxHeight = userLists.size() * height;
            } else {
                maxHeight = (userLists.size() - 1) * height;
            }
        }
        //循环添加列
        for (int i = 0; i < userLists.size(); i++) {
            Session session = userLists.get(i);
            //添加列“线”
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
            View line = new View(getActivity());
            line.setLayoutParams(lineParams);
            line.setBackgroundColor(Color.parseColor("#4D505050"));
            if (llAccountList.getChildCount() > 0) {
                llAccountList.addView(line);
            }
            //添加列“账号”
            final View view = layoutInflater.inflate(ResUtils.getResId(getActivity(), "ffg_account_list_item", "layout"), null);
            TextView tvAccountName = view.findViewById(ResUtils.getResId(getActivity(), "ffg_tv_name", "id"));
            tvAccountName.setGravity(Gravity.CENTER_VERTICAL);
            tvAccountName.setPadding(15, 0, 0, 0);
            String userName = session.getUserName();

//            if (userName.length() > 15) {
//                userName = userName.substring(0, 15) + "...";
//            }
            tvAccountName.setText(userName);
            ImageView ivDelete = view.findViewById(ResUtils.getResId(getActivity(), "ffg_iv_delete", "id"));

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llAccountList.addView(view);
                }
            });
            view.setOnClickListener(new AccountItemClick(session));
            ivDelete.setOnClickListener(new AccountDeleteClick(session));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            view.setLayoutParams(layoutParams);
            llAccountList.addView(view, llAccountList.getChildCount());
        }
//        svAccountList.addView(llAccountList);
        RelativeLayout.LayoutParams scrollViewParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, maxHeight);
        svAccountList.setLayoutParams(scrollViewParams);
    }


    /**
     * 切换勾选/不勾选图片
     */
    private void changeCheck() {
        if (check) {
            check = false;
            ivCheck.setBackgroundResource(imageUnCheck);
        } else {
            check = true;
            ivCheck.setBackgroundResource(imageCheck);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            switch ((Integer) v.getTag()) {
                case ClickType.ACTION_FORGET:
                    loginImpl.showForgetView();
                    break;
                case ClickType.ACTION_CLICK_CHECK:
                    changeCheck();
                    break;
                case ClickType.ACTION_LOGIN:
                    String userName = etAccount.getEditText().getText().toString();
                    String pwd = etPassword.getEditText().getText().toString();
                    if (TextUtils.isEmpty(userName)) {
                        Toast.toastInfo(getActivity(), getResources().getString(ResUtils.getResId(getActivity(), "ffg_tips_empty_account", "string")));
                        return;
                    }
                    if (TextUtils.isEmpty(pwd)) {
                        Toast.toastInfo(getActivity(), getResources().getString(ResUtils.getResId(getActivity(), "ffg_tips_empty_password", "string")));
                        return;
                    }
                    loginImpl.getSignInImpl().accountLogin(userName, pwd);
                    break;
                case ClickType.ACTION_FACEBOOK_MODE:
                    loginImpl.getSignInImpl().fbLogin(getActivity());
                    break;
                case ClickType.ACTION_GOOGLE_MODE:
                    loginImpl.getSignInImpl().googleLogin(getActivity());
                    break;
                case ClickType.ACTION_GUEST_MODE:
                    loginImpl.getSignInImpl().guestLogin();
                    break;
                case ClickType.ACTION_CLICK_AGREEMENT:
                    if (privacyDialog != null && privacyDialog.isShowing()) {
                        privacyDialog.dismiss();
                        privacyDialog = null;
                    }
                    if (SdkBridgeImpl.initBean != null && !TextUtils.isEmpty(SdkBridgeImpl.initBean.initPrivacy.url)) {
                        privacyDialog = new PrivacyDialog(getActivity(), SdkBridgeImpl.initBean.initPrivacy.url);
                        privacyDialog.show();
                    }
                    break;
            }
        }
    }

    /**
     * 隐藏账号历史列表
     *
     * @return
     */
    public boolean hideAccountList() {
        if (userLists != null && userLists.size() > 0 && rlAccountList.getVisibility() == View.VISIBLE) {
            etAccount.getRightImageView().setBackgroundResource(imageDown);
            rlAccountList.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    /**
     * 账号列表item点击事件
     */
    class AccountItemClick implements View.OnClickListener {
        Session session;

        public AccountItemClick(Session session) {
            this.session = session;
        }

        @Override
        public void onClick(View v) {
            etAccount.getRightImageView().setBackgroundResource(imageUp);
            hideAccountList();
            etAccount.getEditText().setText(session.getUserName());
            etPassword.getEditText().setText(session.getPwd());
        }
    }

    /**
     * 删除点击事件
     */
    class AccountDeleteClick implements View.OnClickListener {

        public Session session;

        public AccountDeleteClick(Session session) {
            this.session = session;
        }

        @Override
        public void onClick(View v) {
            SessionUtils.getInstance().deleteUserInfo(getContext(), this.session.getUserId());
            userLists = null;
            autoFillUserInfo();
//            hideAccountList();
        }
    }
}

