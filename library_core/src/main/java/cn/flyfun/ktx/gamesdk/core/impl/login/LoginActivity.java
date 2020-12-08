package cn.flyfun.ktx.gamesdk.core.impl.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import cn.flyfun.ktx.gamesdk.base.utils.Logger;
import cn.flyfun.ktx.gamesdk.core.entity.LoginType;
import cn.flyfun.ktx.gamesdk.core.entity.ResultInfo;
import cn.flyfun.ktx.gamesdk.core.entity.SdkBackLoginInfo;
import cn.flyfun.ktx.gamesdk.core.entity.Session;
import cn.flyfun.ktx.gamesdk.core.impl.SdkBridgeImpl;
import cn.flyfun.ktx.gamesdk.core.impl.login.fragment.ChooseFragment;
import cn.flyfun.ktx.gamesdk.core.inter.IRequestCallback;
import cn.flyfun.ktx.gamesdk.core.inter.ImplCallback;
import cn.flyfun.ktx.gamesdk.core.network.SdkRequest;
import cn.flyfun.ktx.gamesdk.core.ui.DialogUtils;
import cn.flyfun.ktx.gamesdk.core.ui.EventEditText;
import cn.flyfun.ktx.gamesdk.core.ui.VerifyCodeEditText;
import cn.flyfun.ktx.gamesdk.core.utils.AndroidBug5497Workaround;
import cn.flyfun.ktx.gamesdk.core.utils.SessionUtils;
import cn.flyfun.ktx.gamesdk.core.utils.TimeDownUtils;
import cn.flyfun.support.EditTextUtils;
import cn.flyfun.support.ResUtils;
import cn.flyfun.support.jarvis.Toast;
import cn.flyfun.support.ui.NoScrollViewPager;

/**
 * @author #Suyghur.
 * Created on 10/21/20
 */
public class LoginActivity extends FragmentActivity {

    public static ImplCallback implCallback = null;
    private SignInImpl signInImpl;

    private TabLayout tabLayout;
    private NoScrollViewPager viewPager;
    private ConstraintLayout rootContainer, autoLoginContainer, loginContainer, forgetContainer;
    private TextView tvTips;
    private ImageView ivReturn;
    private VerifyCodeEditText vcSend;
    private EventEditText etAccount, etPassword, etPassword2;
    private int leftSelect;
    private int leftSelected;
    private int rightSelect;
    private int rightSelected;

    private Dialog loginLoadingDialog;


    private static Session session = null;
    private static boolean isAutoLogin = false;
    private static boolean isCancelLogin = false;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 101) {
                //倒计时
                changeTimeView((Integer) msg.obj);
            } else if (msg.what == 200) {
                //自动登陆
                if (isCancelLogin) {
                    return;
                }
                if (session != null) {
                    if (loginLoadingDialog != null && loginLoadingDialog.isShowing()) {
                        loginLoadingDialog.dismiss();
                        loginLoadingDialog = null;
                    }

                    loginLoadingDialog = DialogUtils.showCircleProgressLoadingDialog(LoginActivity.this, "");
                    loginLoadingDialog.show();

                    userAutoLogin();
                }
            }
        }
    };

    private void userAutoLogin() {
        switch (session.getLoginType()) {
            case LoginType.TYPE_ACCOUNT_LOGIN:
                signInImpl.accountLogin(session.getUserName(), session.getPwd());
                break;
            case LoginType.TYPE_FACEBOOK_LOGIN:
                signInImpl.facebookLogin(this);
                break;
            case LoginType.TYPE_GUEST_LOGIN:
                signInImpl.guestLogin();
                break;
            case LoginType.TYPE_GOOGLE_LOGIN:
                signInImpl.googleLogin(this);
                break;
        }
    }

    public static void login(Activity activity, boolean isAuto, ImplCallback callback) {
        isAutoLogin = isAuto;
        isCancelLogin = false;
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        implCallback = callback;
    }

    public void userLoginVerify(final JSONObject loginParams) {
        SdkRequest.getInstance().userLoginVerify(this, loginParams, new IRequestCallback() {
            @Override
            public void onResponse(@NonNull ResultInfo resultInfo) {

                if (loginLoadingDialog != null && loginLoadingDialog.isShowing()) {
                    loginLoadingDialog.dismiss();
                }

                if (resultInfo.code == 0 && !TextUtils.isEmpty(resultInfo.data)) {
                    // TODO 解析请求接口返回
                    try {
                        JSONObject jsonObject = new JSONObject(resultInfo.data);
                        SdkBackLoginInfo.getInstance().userId = jsonObject.getString("user_id");
                        SdkBackLoginInfo.getInstance().timestamp = jsonObject.getString("timestamp");
                        SdkBackLoginInfo.getInstance().isRegUser = jsonObject.getInt("is_reg_user");
                        SdkBackLoginInfo.getInstance().isBindPlatform = jsonObject.getInt("is_bind_platform");
                        SdkBackLoginInfo.getInstance().cpSign = jsonObject.getString("cp_sign");
                        SdkBackLoginInfo.getInstance().loginType = loginParams.getInt("login_type");

                        if (LoginActivity.session != null) {
                            LoginActivity.session.reset();
                        }
                        if (LoginActivity.session == null) {
                            LoginActivity.session = new Session();
                        }
                        LoginActivity.session.setUserId(SdkBackLoginInfo.getInstance().userId);
                        LoginActivity.session.setUserName(loginParams.getString("user_name"));
                        LoginActivity.session.setPwd(loginParams.getString("pwd"));
                        LoginActivity.session.setLoginType(loginParams.getInt("login_type"));
                        SessionUtils.getInstance().saveSession(LoginActivity.this, LoginActivity.session);
                        implCallback.onSuccess(SdkBackLoginInfo.getInstance().toJsonString());
                        LoginActivity.this.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        implCallback.onFailed("登录校验异常");
                    }
                } else {
                    if (!TextUtils.isEmpty(resultInfo.msg)) {
                        Toast.toastInfo(LoginActivity.this, resultInfo.msg);
                    }
                    implCallback.onFailed("登录校验异常");
                }
            }
        });
    }


    public void userRegister(final String userName, final String pwd) {
        SdkRequest.getInstance().userRegister(this, userName, pwd, new IRequestCallback() {
            @Override
            public void onResponse(@NonNull ResultInfo resultInfo) {
                if (resultInfo.code == 0 && !TextUtils.isEmpty(resultInfo.data)) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultInfo.data);
                        SdkBackLoginInfo.getInstance().userId = jsonObject.getString("user_id");
                        SdkBackLoginInfo.getInstance().timestamp = jsonObject.getString("timestamp");
                        SdkBackLoginInfo.getInstance().isRegUser = jsonObject.getInt("is_reg_user");
                        SdkBackLoginInfo.getInstance().isBindPlatform = jsonObject.getInt("is_bind_platform");
                        SdkBackLoginInfo.getInstance().cpSign = jsonObject.getString("cp_sign");
                        SdkBackLoginInfo.getInstance().loginType = 0;

                        if (LoginActivity.session != null) {
                            LoginActivity.session.reset();
                        }
                        if (LoginActivity.session == null) {
                            LoginActivity.session = new Session();
                        }
                        LoginActivity.session.setUserId(SdkBackLoginInfo.getInstance().userId);
                        LoginActivity.session.setUserName(userName);
                        LoginActivity.session.setPwd(pwd);
                        LoginActivity.session.setLoginType(0);
                        SessionUtils.getInstance().saveSession(LoginActivity.this, LoginActivity.session);
                        implCallback.onSuccess(SdkBackLoginInfo.getInstance().toJsonString());
                        LoginActivity.this.finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.toastInfo(LoginActivity.this, ResUtils.getResString(LoginActivity.this, "ffg_login_register_error"));
                        implCallback.onFailed("账号注册异常");
                    }
                } else {
                    if (!TextUtils.isEmpty(resultInfo.msg)) {
                        Toast.toastInfo(LoginActivity.this, resultInfo.msg);
                    } else {
                        Toast.toastInfo(LoginActivity.this, ResUtils.getResString(LoginActivity.this, "ffg_login_register_error"));
                    }
                    implCallback.onFailed("账号注册异常");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInImpl = new SignInImpl(this, new SignInImpl.ISignInCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    userLoginVerify(new JSONObject(result));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String result) {
                if (loginLoadingDialog != null && loginLoadingDialog.isShowing()) {
                    loginLoadingDialog.dismiss();
                }
                implCallback.onFailed(result);
            }
        });
        initView();
        showAutoLoginView();
    }

    private void initView() {
        if (SdkBridgeImpl.isLandscape) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        setContentView(ResUtils.getResId(this, "ffg_login", "layout"));

        if (!SdkBridgeImpl.isLandscape) {
            AndroidBug5497Workaround.assistActivity(this);
        }

        rootContainer = findViewById(ResUtils.getResId(this, "ffg_login_container", "id"));

        autoLoginContainer = findViewById(ResUtils.getResId(this, "ffg_cl_auto", "id"));
        tvTips = findViewById(ResUtils.getResId(this, "ffg_tv_tips", "id"));
        Button btnChange = findViewById(ResUtils.getResId(this, "ffg_btn_change", "id"));
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autoLoginContainer.getVisibility() == View.VISIBLE) {
                    autoLoginContainer.setVisibility(View.GONE);
                    loginContainer.setVisibility(View.VISIBLE);
                    isCancelLogin = true;
                }
            }
        });

        autoLoginContainer.setVisibility(View.GONE);
        forgetContainer = findViewById(ResUtils.getResId(this, "ffg_cl_forget", "id"));
        loginContainer = findViewById(ResUtils.getResId(this, "ffg_cl_login", "id"));
        loginContainer.setVisibility(View.VISIBLE);

        ivReturn = findViewById(ResUtils.getResId(this, "ffg_iv_return", "id"));
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideForgetView();
            }
        });

        ImageView ivLogo = findViewById(ResUtils.getResId(this, "ffg_iv_login_logo", "id"));

        int logoId = ResUtils.getResId(this, "ffg_login_logo_img", "drawable");
        if (logoId != 0) {
            ivLogo.setBackgroundResource(logoId);
        }

        etAccount = findViewById(ResUtils.getResId(this, "ffg_et_forget_account", "id"));
        etAccount.getLeftImageView().setBackgroundResource(ResUtils.getResId(this, "ffg_account_img", "drawable"));
        etAccount.getEditText().setHint(ResUtils.getResId(this, "ffg_login_account_hint", "string"));

        etPassword = findViewById(ResUtils.getResId(this, "ffg_et_forget_pwd1", "id"));
        etPassword.getLeftImageView().setBackgroundResource(ResUtils.getResId(this, "ffg_password_img", "drawable"));
        etPassword.getEditText().setHint(ResUtils.getResId(this, "ffg_login_password1_hint", "string"));

        etPassword2 = findViewById(ResUtils.getResId(this, "ffg_et_forget_pwd2", "id"));
        etPassword2.getLeftImageView().setBackgroundResource(ResUtils.getResId(this, "ffg_password_img", "drawable"));
        etPassword2.getEditText().setHint(ResUtils.getResId(this, "ffg_login_password2_hint", "string"));

        vcSend = findViewById(ResUtils.getResId(this, "ffg_et_forget_code", "id"));
        vcSend.getLeftImageView().setBackgroundResource(ResUtils.getResId(this, "ffg_email_img", "drawable"));
        vcSend.getEditText().setHint(ResUtils.getResId(this, "ffg_login_input_code_hint", "string"));
        vcSend.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etAccount.getEditText().getText())) {
                    getCaptchaCode();
                } else {
                    String tips = LoginActivity.this.getResources().getString(ResUtils.getResId(LoginActivity.this, "ffg_tips_empty_account", "string"));
                    Toast.toastInfo(LoginActivity.this, tips);
                }
            }
        });
        Button btnConfirm = findViewById(ResUtils.getResId(this, "ffg_btn_confirm", "id"));
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doForgetPassword();
            }
        });

        tabLayout = findViewById(ResUtils.getResId(this, "ffg_tl", "id"));
        viewPager = findViewById(ResUtils.getResId(this, "ffg_vp", "id"));
        FragmentManager fragmentManager = getSupportFragmentManager();
        //使用适配器将ViewPager与Fragment绑定在一起

        String[] tabs = this.getResources().getStringArray(ResUtils.getResId(this, "ffg_login_tab", "array"));
        final LoginFragmentPagerAdapter pagerAdapter = new LoginFragmentPagerAdapter(tabs, fragmentManager);
        viewPager.setAdapter(pagerAdapter);
        //将TabLayout与ViewPager绑定在一起
        tabLayout.setupWithViewPager(viewPager);

        leftSelect = ResUtils.getResId(this, "ffg_tab_left_select", "drawable");
        leftSelected = ResUtils.getResId(this, "ffg_tab_left_selected", "drawable");
        rightSelect = ResUtils.getResId(this, "ffg_tab_right_select", "drawable");
        rightSelected = ResUtils.getResId(this, "ffg_tab_right_selected", "drawable");
        setTabBackground(leftSelected, rightSelect);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        setTabBackground(leftSelected, rightSelect);
                        break;
                    case 1:
                        setTabBackground(leftSelect, rightSelected);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (pagerAdapter.getCurrentFragment() instanceof ChooseFragment) {
                    ((ChooseFragment) pagerAdapter.getCurrentFragment()).hideAccountList();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void showAutoLoginView() {
        session = SessionUtils.getInstance().getLocalLastSession(this);
        if (session != null && isAutoLogin) {
            loginContainer.setVisibility(View.GONE);
            autoLoginContainer.setVisibility(View.VISIBLE);
            String tips = "";
            switch (session.getLoginType()) {
                case LoginType.TYPE_ACCOUNT_LOGIN:
                    String userName = session.getUserName();
                    if (userName.length() > 15) {
                        userName = userName.substring(0, 15) + "...";
                    }
                    tips = userName + ResUtils.getResString(this, "ffg_login_tv_login_account");
                    break;
                case LoginType.TYPE_FACEBOOK_LOGIN:
                    tips = ResUtils.getResString(this, "ffg_login_tv_login_facebook");
                    break;
                case LoginType.TYPE_GUEST_LOGIN:
                    tips = ResUtils.getResString(this, "ffg_login_tv_login_guest");
                    break;
                case LoginType.TYPE_GOOGLE_LOGIN:
                    tips = ResUtils.getResString(this, "ffg_login_tv_login_google");
                    break;

            }
            tvTips.setText(tips);

            //延迟n秒发起登录请求
            Logger.d("延迟1.5秒发起登录请求");
            mHandler.sendEmptyMessageDelayed(200, 1500);
        } else {
            loginContainer.setVisibility(View.VISIBLE);
            autoLoginContainer.setVisibility(View.GONE);
        }
    }

    private void changeTimeView(int time) {
        if (vcSend.getTextView() == null) {
            return;
        }
        if (time <= 0) {
            vcSend.getTextView().setText(ResUtils.getResId(this, "ffg_login_btn_get_captcha", "string"));
            vcSend.getTextView().setEnabled(true);

        } else {
            vcSend.getTextView().setText(time + "s");
            vcSend.getTextView().setEnabled(false);
        }
    }

    private void setTabBackground(int left, int right) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ViewGroup tabStrip = (ViewGroup) tabLayout.getChildAt(0);
            View tabView1 = tabStrip.getChildAt(0);
            View tabView2 = tabStrip.getChildAt(1);
            if (tabView1 != null) {
                int paddingStart = tabView1.getPaddingStart();
                int paddingTop = tabView1.getPaddingTop();
                int paddingEnd = tabView1.getPaddingEnd();
                int paddingBottom = tabView1.getPaddingBottom();
                ViewCompat.setBackground(tabView1, ContextCompat.getDrawable(tabView1.getContext(), left));
                ViewCompat.setPaddingRelative(tabView1, paddingStart, paddingTop, paddingEnd, paddingBottom);
            }

            if (tabView2 != null) {
                int paddingStart = tabView2.getPaddingStart();
                int paddingTop = tabView2.getPaddingTop();
                int paddingEnd = tabView2.getPaddingEnd();
                int paddingBottom = tabView2.getPaddingBottom();
                ViewCompat.setBackground(tabView2, ContextCompat.getDrawable(tabView2.getContext(), right));
                ViewCompat.setPaddingRelative(tabView2, paddingStart, paddingTop, paddingEnd, paddingBottom);
            }
        }
    }

    private void getCaptchaCode() {
        SdkRequest.getInstance().getCaptcha(this, etAccount.getEditText().getText().toString(), new IRequestCallback() {
            @Override
            public void onResponse(@NonNull ResultInfo resultInfo) {
                if (resultInfo.code == 0) {
                    if (TextUtils.isEmpty(resultInfo.msg)) {
                        Toast.toastInfo(LoginActivity.this, "驗證碼已發送，請注意查收");
                    } else {
                        Toast.toastInfo(LoginActivity.this, resultInfo.msg);
                    }
                    changeTimeNum();
                } else {
                    if (TextUtils.isEmpty(resultInfo.msg)) {
                        Toast.toastInfo(LoginActivity.this, "驗證碼發送异常，請稍後再試");
                    } else {
                        Toast.toastInfo(LoginActivity.this, resultInfo.msg);
                    }
                }
            }
        });
    }

    private void changeTimeNum() {
        if (!TimeDownUtils.isRunning()) {
            TimeDownUtils.start(new TimeDownUtils.TimeCallback() {
                @Override
                public void onTime(int time) {
                    Message msg = new Message();
                    msg.what = 101;
                    msg.obj = time;
                    mHandler.sendMessage(msg);
                }
            });
        } else {
            TimeDownUtils.resetCallback(new TimeDownUtils.TimeCallback() {
                @Override
                public void onTime(int time) {
                    Message msg = new Message();
                    msg.what = 101;
                    msg.obj = time;
                    mHandler.sendMessage(msg);
                }
            });
        }
    }


    private void doForgetPassword() {
        final String userName = etAccount.getEditText().getText().toString();
        final String pwd = etPassword.getEditText().getText().toString();
        String pwd2 = etPassword.getEditText().getText().toString();
        final String code = vcSend.getEditText().getText().toString();
        if (TextUtils.isEmpty(userName)) {
            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_empty_account"));
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_empty_password"));
            return;
        }
        if (TextUtils.isEmpty(pwd2)) {
            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_empty_password"));
            return;
        }
        if (TextUtils.isEmpty(code)) {
            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_empty_code"));
            return;
        }

        if (!EditTextUtils.filterEmail(userName)) {
            Toast.toastInfo(this, ResUtils.getResString(this, "ffg_tips_email_format_error"));
            return;
        }
        SdkRequest.getInstance().forgetPassword(this, userName, pwd, code, new IRequestCallback() {
            @Override
            public void onResponse(ResultInfo resultInfo) {
                if (resultInfo.code == 0) {
                    hideForgetView();
                    if (TextUtils.isEmpty(resultInfo.msg)) {
                        return;
                    } else {
                        Toast.toastInfo(LoginActivity.this, resultInfo.msg);
                    }
                    signInImpl.accountLogin(userName, pwd);
                } else {
                    if (TextUtils.isEmpty(resultInfo.msg)) {
                        return;
                    } else {
                        Toast.toastInfo(LoginActivity.this, resultInfo.msg);
                    }
                }
            }
        });
    }


    public void showForgetView() {
        this.tabLayout.setVisibility(View.GONE);
        this.viewPager.setVisibility(View.GONE);
        this.forgetContainer.setVisibility(View.VISIBLE);
        this.ivReturn.setVisibility(View.VISIBLE);
    }

    public void hideForgetView() {
        this.forgetContainer.setVisibility(View.GONE);
        this.ivReturn.setVisibility(View.GONE);
        this.tabLayout.setVisibility(View.VISIBLE);
        this.viewPager.setVisibility(View.VISIBLE);
        etAccount.getEditText().setText("");
        etPassword.getEditText().setText("");
        etPassword2.getEditText().setText("");
        vcSend.getEditText().setText("");
        if (TimeDownUtils.isRunning()) {
            TimeDownUtils.cancel();
            vcSend.getTextView().setText(ResUtils.getResId(this, "ffg_login_btn_get_captcha", "string"));
            vcSend.getTextView().setEnabled(true);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public SignInImpl getSignInImpl() {
        return signInImpl;
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            // 点击的是输入框区域，保留点击EditText的事件
            return !(event.getX() > left) || !(event.getX() < right) || !(event.getY() > top) || !(event.getY() < bottom);
        }
        return false;
    }

    public void hideBar() {
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (!isImmersiveModeEnabled) {
//            Log.i(TAG, "Turning immersive mode mode on. ");
            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if (Build.VERSION.SDK_INT >= 16) {
                newUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            if (Build.VERSION.SDK_INT >= 18) {
                newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!SdkBridgeImpl.isLandscape && rootContainer != null) {
            rootContainer.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBar();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (signInImpl != null) {
            signInImpl.onActivityResult(requestCode, resultCode, data);
        }
    }


}
