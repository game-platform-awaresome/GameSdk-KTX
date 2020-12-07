package cn.flyfun.ktx.gamesdk.core.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.flyfun.support.ResUtils;
import cn.flyfun.support.device.DeviceInfoUtils;

public class PrivacyDialog extends Dialog implements View.OnClickListener {


    private static final int ACTION_RETURN = 1000;
    private static final int ACTION_CONFIRM = 1001;

    private String url;

    private ImageView ivReturn;
    private Button btnConfirm;

    public PrivacyDialog(Context context, String url) {
        super(context);
        this.url = url;
        initView(context);
    }

    private void initView(Context context) {
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(context).inflate(ResUtils.getResId(context, "ffg_privacy_dialog", "layout"), null);
        setContentView(view);

        WindowManager.LayoutParams attr = getWindow().getAttributes();
        if (attr != null) {
            //设置dialog 在布局中的位置
            attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            attr.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            attr.gravity = Gravity.CENTER;
        }

        ivReturn = findViewById(ResUtils.getResId(context, "ffg_iv_return", "id"));
        btnConfirm = findViewById(ResUtils.getResId(context, "ffg_btn_confirm", "id"));

        initWebView(context);

        initViewAction();
        initViewListener();
    }

    private void initViewAction() {
        ivReturn.setTag(ACTION_RETURN);
        btnConfirm.setTag(ACTION_CONFIRM);

    }

    private void initViewListener() {
        ivReturn.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    private void initWebView(Context context) {
        WebView webView = new WebView(context);
        webView.setVerticalScrollBarEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        FrameLayout webViewContainer = findViewById(ResUtils.getResId(context, "ffg_webview_container", "id"));
        webViewContainer.addView(webView);

        WebSettings webSetting = webView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setSaveFormData(true);
        webSetting.setSavePassword(true);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(context.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(context.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(context.getDir("geolocation", 0).getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance().sync();

        if (DeviceInfoUtils.isNetworkConnected(context)) {
            webView.loadUrl(url);
        } else {
            //这种写法可以正确解码
            webView.loadData("网络异常,请检查重试", "text/html; charset=UTF-8", null);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            switch ((Integer) v.getTag()) {
                case ACTION_RETURN:
                case ACTION_CONFIRM:
                    dismiss();
                    break;
            }
        }
    }
}
