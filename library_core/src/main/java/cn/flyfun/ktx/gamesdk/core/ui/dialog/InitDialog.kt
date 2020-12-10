package cn.flyfun.ktx.gamesdk.core.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.webkit.CookieSyncManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.FrameLayout
import cn.flyfun.support.ResUtils
import cn.flyfun.support.device.DeviceInfoUtils


/**
 * @author #Suyghur.
 * Created on 2020/12/10
 */
class InitDialog constructor(context: Context, val url: String) : Dialog(context) {

    //    private lateinit var tvTitle: TextView
    lateinit var button: Button private set
    private lateinit var webView: WebView

    init {
        initView()
    }

    private fun initView() {
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view: View = LayoutInflater.from(context).inflate(ResUtils.getResId(context, "ffg_init_dialog", "layout"), null)
        setContentView(view)

        val attr = window?.attributes as WindowManager.LayoutParams
        //设置dialog 在布局中的位置
        attr.height = ViewGroup.LayoutParams.WRAP_CONTENT
        attr.width = ViewGroup.LayoutParams.WRAP_CONTENT
        attr.gravity = Gravity.CENTER

//        tvTitle = findViewById(ResUtils.getResId(context, "ffg_tv_title", "id"))
        button = findViewById(ResUtils.getResId(context, "ffg_btn_confirm", "id"))

        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled", "NewApi")
    private fun initWebView() {
        webView = WebView(context)
        webView.isVerticalScrollBarEnabled = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        val webViewContainer = findViewById<FrameLayout>(ResUtils.getResId(context, "ffg_webview_container", "id"))
        webViewContainer.addView(webView)

        val webSetting = webView.settings
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.saveFormData = true
        webSetting.savePassword = true
        webSetting.builtInZoomControls = false
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(false)
        webSetting.setAppCacheEnabled(true)
        webSetting.domStorageEnabled = true
        webSetting.javaScriptEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE)
        webSetting.setAppCachePath(context.getDir("appcache", 0).path)
        webSetting.databasePath = context.getDir("databases", 0).path
        webSetting.setGeolocationDatabasePath(context.getDir("geolocation", 0).path)
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        CookieSyncManager.createInstance(context)
        CookieSyncManager.getInstance().sync()

        if (DeviceInfoUtils.isNetworkConnected(context)) {
            webView.loadUrl(url)
        } else {
            //这种写法可以正确解码
            webView.loadData("网络异常,请检查重试", "text/html; charset=UTF-8", null)
        }
    }

}