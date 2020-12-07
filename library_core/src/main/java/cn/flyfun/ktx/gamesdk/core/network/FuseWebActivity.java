package cn.flyfun.ktx.gamesdk.core.network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import cn.flyfun.ktx.gamesdk.base.utils.Logger;
import cn.flyfun.ktx.gamesdk.core.ui.DialogUtils;
import cn.flyfun.ktx.gamesdk.core.utils.AndroidBug5497Workaround;
import cn.flyfun.support.FullScreenUtils;
import cn.flyfun.support.ResUtils;
import cn.flyfun.support.device.DeviceInfoUtils;
import cn.flyfun.support.jarvis.Toast;
import cn.flyfun.support.ui.circleprogress.CircleProgressLoadingDialog;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

/**
 * 不带x5的webview
 */
public class FuseWebActivity extends Activity {

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;
    //    private CircleProgressLoadingDialog loadingDialog;
    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private CircleProgressLoadingDialog loadingDialog;
    private TextView tvTitle;
    private ImageView ivReturn;
    private WebView webView;
    private View view, root;
    private String url;
    private String lastLoadUrl;
    private Dialog exitDialog;

    public static void start(Activity activity, String url) {
        start(activity, url, null);
    }

    public static void start(Activity activity, String url, Bundle payInfo) {
        Intent intent = new Intent(activity, FuseWebActivity.class);
        intent.putExtra("webview_url", url);
        if (payInfo != null) {
            intent.putExtra("payInfo", payInfo);
        }
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        //初始化dialog
        loadingDialog = createLoadingDialog();
        loadingDialog.show();

        url = getIntent().getStringExtra("webview_url");

        if (TextUtils.isEmpty(url)) {
            Toast.toastInfo(this, "url为空");
            finish();
            return;
        }
        Logger.i("FuseWebActivity load url : " + url);

        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(ResUtils.getResId(this, "ffg_web_view", "layout"));

        AndroidBug5497Workaround.assistActivity(this);
        // 强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        root = findViewById(ResUtils.getResId(this, "ffg_root", "id"));

        ivReturn = findViewById(ResUtils.getResId(this, "ffg_iv_return", "id"));
        ivReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFinish();
            }
        });
        ivReturn.setVisibility(View.GONE);

        tvTitle = findViewById(ResUtils.getResId(this, "ffg_tv_title", "id"));
        ImageView ivClose = findViewById(ResUtils.getResId(this, "ffg_iv_close", "id"));

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvTitle.getText().equals("问题详情") || tvTitle.getText().equals("問題詳情") || tvTitle.getText().equals("Question details")) {
                    if (exitDialog != null) {
                        exitDialog.dismiss();
                        exitDialog = null;
                    }
                    exitDialog = DialogUtils.newTipsDialog(FuseWebActivity.this,
                            ResUtils.getResString(FuseWebActivity.this, "ffg_gm_dialog_tv_content"),
                            ResUtils.getResString(FuseWebActivity.this, "ffg_gm_dialog_tv_not"),
                            ResUtils.getResString(FuseWebActivity.this, "ffg_gm_dialog_tv_yes"),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (exitDialog != null && exitDialog.isShowing()) {
                                        exitDialog.dismiss();
                                    }
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    webView.loadUrl("javascript:clientCallJS()");
                                    if (exitDialog != null && exitDialog.isShowing()) {
                                        exitDialog.dismiss();
                                    }
                                }
                            });
                    exitDialog.show();
                } else {
                    finish();
                }
            }
        });

        view = findViewById(ResUtils.getResId(this, "ffg_empty_view", "id"));
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        if (FullScreenUtils.isNavigationBarShow(this)) {
            params.height = FullScreenUtils.getNavigationBarHeight(this);
            ;
        } else {
            params.height = 0;
        }
        view.setLayoutParams(params);

        initWebView();

        boolean hasStatusBar = (getWindow().getAttributes().flags & FLAG_FULLSCREEN) != FLAG_FULLSCREEN;
        Logger.d("hasStatusBar : " + hasStatusBar);
        getScreenRelatedInformation();
        getRealScreenRelatedInformation();
    }

    private boolean isSoftShowing() {
        //获取当屏幕内容的高度
        int screenHeight = this.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        //DecorView即为activity的顶级view
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断
        return screenHeight * 2 / 3 > rect.bottom;
    }

    /**
     * 显示客服webview，特殊处理（WebView必须在布局里，不然在线客服页面会白屏） 处理好的，渲染正常的webview，热点和攻略也用到这方法
     */
    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    private void initWebView() {
        webView = new WebView(this);
        webView.setVerticalScrollBarEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.addJavascriptInterface(new SdkJsImpl(this), "FlyFunSdk");
        FrameLayout container = findViewById(ResUtils.getResId(this, "ffg_webview_container", "id"));
        container.addView(webView);
        webView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isSoftShowing()) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        });


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
        webSetting.setAppCachePath(getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(getDir("geolocation", 0).getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();

        if (DeviceInfoUtils.isNetworkConnected(this)) {
            webView.loadUrl(url);
//            webView.loadUrl("file:///android_asset/test.html");
//            webView.loadUrl("javascript:callJS()");
        } else {
            //这种写法可以正确解码
            webView.loadData("网络异常,请检查重试", "text/html; charset=UTF-8", null);
        }

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Logger.d("onPageFinished view.canGoBack() = " + view.canGoBack());
                if (view.canGoBack()) {
                    ivReturn.setVisibility(View.VISIBLE);
                } else {
                    ivReturn.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                WebView.HitTestResult hitTestResult = view.getHitTestResult();
                //Logger.d("hitTestResult = " + hitTestResult);
                Logger.d("url = " + url);
                Uri uri = Uri.parse(url);
                lastLoadUrl = url;
                Logger.d("uri scheme : " + uri.getScheme());
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    //解决重定向加载的问题 return false
                    if (!view.canGoBack() || hitTestResult == null) {
                        return false;
                    }
                    //解决重定向加载的问题 return false
                    if (hitTestResult.getType() == WebView.HitTestResult.UNKNOWN_TYPE) {
                        return false;
                    }

                    view.loadUrl(url);
                }
                return false;
            }

            @Override
            public void onReceivedSslError(WebView arg0, SslErrorHandler handler, SslError error) {
                //部分手机浏览器不支持https，所以此处需要接受证书
                handler.proceed();
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //progressWebView.setProgressBarProgress(newProgress);
                if (newProgress == 100 && loadingDialog != null && loadingDialog.isShowing()) {
                    //加载完成,关闭loading
                    loadingDialog.dismiss();
                }
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                openFileChooserImplForAndroid5(filePathCallback);
                return true;
            }

            //            @Override
//            public void openFileChooser(com.tencent.smtt.sdk.ValueCallback<Uri> uploadFile, String acceptType, String captureType) {
//                openFileChooserImpl(uploadFile);
//            }
//
//            // For Android > 5.0
//            @Override
//            public boolean onShowFileChooser(WebView webView, com.tencent.smtt.sdk.ValueCallback<Uri[]> uploadMsg, FileChooserParams params) {
//                openFileChooserImplForAndroid5(uploadMsg);
//                return true;
//            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Logger.d("webview title:" + view.getTitle());
                if (!DeviceInfoUtils.isNetworkConnected(view.getContext())) {
                    tvTitle.setText("网络异常");
                    return;
                }
                if (tvTitle != null && !TextUtils.isEmpty(view.getTitle())) {
                    String viewtitle = view.getTitle();
                    if (viewtitle.length() > 10) {
                        viewtitle = viewtitle.substring(0, 10);
                        viewtitle = viewtitle + "...";
                    }
                    tvTitle.setText(viewtitle);
                }
            }

        });

    }

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        Logger.d("open FileChooser");
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        Logger.d("open FileChooser For Android5");
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("*/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "File Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Logger.d("webview onActivityResult");
        if (webView != null) {
            if (requestCode == FILECHOOSER_RESULTCODE) {
                Logger.d("onActivityResult FILECHOOSER_RESULTCODE");
                if (null == mUploadMessage)
                    return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;

            } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
                Logger.d("onActivityResult FILECHOOSER_RESULTCODE_FOR_ANDROID_5");
                if (null == mUploadMessageForAndroid5)
                    return;
                Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
                if (result != null) {
                    mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
                } else {
                    mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
                }
                mUploadMessageForAndroid5 = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        doFinish();
    }

    private void doFinish() {
        //Logger.d("doFinish view.canGoBack() = " + progressWebView.canGoBack());
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return;
        }
        finish();
    }

//    /**
//     * 创建LoadingDialog
//     *
//     * @return
//     */
//    private CircleProgressLoadingDialog createLoadingDialog() {
//        CircleProgressLoadingDialog.Builder builder = new CircleProgressLoadingDialog.Builder(this);
//        builder.setMessage("正在玩命加载，请稍等...", 15, new int[]{255, 255, 255, 255});
//        builder.hasMessage(true);
//        return builder.build();
//    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            //do something.
//            return true;
//        } else {
//            return super.dispatchKeyEvent(event);
//        }
//    }

//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            //do something.
//            return true;
//        } else {
//            return super.dispatchKeyEvent(event);
//        }
//    }


    @Override
    protected void onResume() {
        super.onResume();
//        hideBottomUIMenu(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            webView.destroy();
        }
    }

//    /**
//     * 180115 隐藏 魅族、Nexus、华为等底部的虚拟导航按键，避免遮挡内容
//     *
//     * @param activity 需要隐藏底部导航按键的Activity
//     */
//    public static void hideBottomUIMenu(Activity activity) {
//        //隐藏虚拟按键，并且全屏
//        if (Build.VERSION.SDK_INT < 19) { // lower api
//            View v = activity.getWindow().getDecorView();
//            v.setSystemUiVisibility(View.GONE);
//        } else {
//            View decorView = activity.getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View
//                    .SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
//        }
//    }

    /**
     * 创建LoadingDialog
     *
     * @return
     */
    private CircleProgressLoadingDialog createLoadingDialog() {
        CircleProgressLoadingDialog.Builder builder = new CircleProgressLoadingDialog.Builder(this);
        builder.setMessage(ResUtils.getResString(this, "ffg_loading_tips"), 15, new int[]{255, 255, 255, 255});
        builder.hasMessage(true);
        return builder.build();
    }

    public void getScreenRelatedInformation() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            int widthPixels = outMetrics.widthPixels;
            int heightPixels = outMetrics.heightPixels;
            int densityDpi = outMetrics.densityDpi;
            float density = outMetrics.density;
            float scaledDensity = outMetrics.scaledDensity;
            //可用显示大小的绝对宽度（以像素为单位）。
            //可用显示大小的绝对高度（以像素为单位）。
            //屏幕密度表示为每英寸点数。
            //显示器的逻辑密度。
            //显示屏上显示的字体缩放系数。
            Logger.d("widthPixels = " + widthPixels + ",heightPixels = " + heightPixels + ",densityDpi = " + densityDpi + ",density = " + density + ",scaledDensity = " + scaledDensity);
        }
    }

    public void getRealScreenRelatedInformation() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
            int widthPixels = outMetrics.widthPixels;
            int heightPixels = outMetrics.heightPixels;
            int densityDpi = outMetrics.densityDpi;
            float density = outMetrics.density;
            float scaledDensity = outMetrics.scaledDensity;
            //可用显示大小的绝对宽度（以像素为单位）。
            //可用显示大小的绝对高度（以像素为单位）。
            //屏幕密度表示为每英寸点数。
            //显示器的逻辑密度。
            //显示屏上显示的字体缩放系数。
            Logger.d("widthPixels = " + widthPixels + ",heightPixels = " + heightPixels + ",densityDpi = " + densityDpi + ",density = " + density + ",scaledDensity = " + scaledDensity);
        }
    }
}
