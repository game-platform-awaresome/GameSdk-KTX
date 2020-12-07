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
import android.widget.TextView;

import cn.flyfun.support.ResUtils;
import cn.flyfun.support.device.DeviceInfoUtils;

public class InitDialog extends Dialog {


    private WebView webView;
    private String url;

    private TextView tvTitle;
    private Button btnConfirm;

    public InitDialog(Context context, String url) {
        super(context);
        this.url = url;
        initView(context);
    }

    private void initView(Context context) {
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(context).inflate(ResUtils.getResId(context, "ffg_init_dialog", "layout"), null);
        setContentView(view);


        WindowManager.LayoutParams attr = getWindow().getAttributes();
        if (attr != null) {
            //设置dialog 在布局中的位置
            attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            attr.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            attr.gravity = Gravity.CENTER;
        }

        tvTitle = findViewById(ResUtils.getResId(context, "ffg_tv_title", "id"));
        btnConfirm = findViewById(ResUtils.getResId(context, "ffg_btn_confirm", "id"));

        initWebView(context);
    }

    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    private void initWebView(Context context) {
        webView = new WebView(context);
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


    public void setOnclickListener(View.OnClickListener listener) {
        if (this.btnConfirm != null) {
            btnConfirm.setOnClickListener(listener);
        }
    }

    public void hideConfirmButton() {
        if (btnConfirm != null) {
            setCancelable(false);
            btnConfirm.setVisibility(View.GONE);
        }
    }

    public void showConfirmButton() {
        if (btnConfirm != null) {
            setCancelable(true);
            btnConfirm.setVisibility(View.VISIBLE);
        }
    }

    public void setButtonText(String text) {
        if (this.btnConfirm != null) {
            btnConfirm.setText(text);
        }
    }

    public void setTitle(String title) {
        if (this.tvTitle != null) {
            tvTitle.setText(title);
        }
    }

}
