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
import android.widget.ImageView
import cn.flyfun.ktx.gamesdk.core.entity.ClickType
import cn.flyfun.support.ResUtils
import cn.flyfun.support.device.DeviceInfoUtils


/**
 * @author #Suyghur.
 * Created on 2020/12/10
 */
class PrivacyDialog constructor(context: Context, val url: String) : Dialog(context), View.OnClickListener {

    private lateinit var ivReturn: ImageView
    private lateinit var btnConfirm: Button

    init {
        initView()
    }

    private fun initView() {
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val view = LayoutInflater.from(context).inflate(ResUtils.getResId(context, "ffg_privacy_dialog", "layout"), null)
        setContentView(view)

        val attr = window?.attributes as WindowManager.LayoutParams
        attr.height = ViewGroup.LayoutParams.WRAP_CONTENT
        attr.width = ViewGroup.LayoutParams.WRAP_CONTENT
        attr.gravity = Gravity.CENTER

        ivReturn = findViewById(ResUtils.getResId(context, "ffg_iv_return", "id"))
        btnConfirm = findViewById(ResUtils.getResId(context, "ffg_btn_confirm", "id"))

        initWebView()
        initViewAction()
        initViewListener()
    }

    @SuppressLint("SetJavaScriptEnabled", "NewApi")
    private fun initWebView() {
        val webView = WebView(context)
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

    private fun initViewAction() {
        ivReturn.tag = ClickType.ACTION_RETURN
        btnConfirm.tag = ClickType.ACTION_CONFIRM
    }

    private fun initViewListener() {
        ivReturn.setOnClickListener(this)
        btnConfirm.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.apply {
            when (tag as Int) {
                ClickType.ACTION_RETURN, ClickType.ACTION_CONFIRM -> dismiss()
            }
        }
    }
}