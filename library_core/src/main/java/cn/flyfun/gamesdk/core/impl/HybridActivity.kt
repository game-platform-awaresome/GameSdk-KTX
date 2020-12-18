package cn.flyfun.gamesdk.core.impl

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.graphics.Rect
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import android.webkit.WebView.HitTestResult
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.entity.ClickType
import cn.flyfun.gamesdk.core.network.SdkJsImpl
import cn.flyfun.gamesdk.core.ui.DialogUtils
import cn.flyfun.gamesdk.core.ui.dialog.TipsDialog
import cn.flyfun.gamesdk.core.utils.AndroidBug5497Workaround
import cn.flyfun.support.FullScreenUtils
import cn.flyfun.support.ResUtils
import cn.flyfun.support.device.DeviceInfoUtils
import cn.flyfun.support.ui.circleprogress.CircleProgressLoadingDialog

/**
 * @author #Suyghur.
 * Created on 2020/12/10
 */
class HybridActivity : Activity() {

    private var uploadMessageForAndroid5: ValueCallback<Array<Uri>>? = null
    private lateinit var tvTitle: TextView
    private lateinit var ivReturn: ImageView
    private lateinit var view: View
    private lateinit var webView: WebView
    private var loadingDialog: CircleProgressLoadingDialog? = null
    private var exitDialog: TipsDialog? = null
    private var returnDialog: TipsDialog? = null
    private val callback = object : SdkJsImpl.IJsCallback {
        override fun onCallback(tag: Int, ext: String) {
            when (tag) {
                ClickType.ACTION_RETURN -> runOnUiThread {
                    webView.goBack()
                }
                ClickType.ACTION_CLOSE -> finish()

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFormat(PixelFormat.TRANSLUCENT)

        //初始化dialog
        createLoadingDialog()

        if (TextUtils.isEmpty(url)) {
            Logger.e("url为空")
            finish()
            return
        }

        Logger.i("HybridActivity load url : $url")
        try {
            window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(ResUtils.getResId(this, "ffg_hybrid", "layout"))
        AndroidBug5497Workaround.assistActivity(this)

        //强制竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        ivReturn = findViewById(ResUtils.getResId(this, "ffg_iv_return", "id"))
        ivReturn.setOnClickListener {
            val uri = Uri.parse(webView.url)
            val state = uri.getQueryParameter("state")
            if (uri.path == "/chat.html" && state == "0") {
                showReturnDialog()
            } else {
                doFinish()
            }
        }
        ivReturn.visibility = View.GONE

        tvTitle = findViewById(ResUtils.getResId(this, "ffg_tv_title", "id"))

        val ivClose = findViewById<ImageView>(ResUtils.getResId(this, "ffg_iv_close", "id"))
        ivClose.setOnClickListener {
            val uri = Uri.parse(webView.url)
            val state = uri.getQueryParameter("state")
            if (uri.path == "/chat.html" && state == "0") {
                showExitDialog()
            } else {
                finish()
            }
        }

        view = findViewById(ResUtils.getResId(this, "ffg_empty_view", "id"))
        val params = view.layoutParams as ConstraintLayout.LayoutParams
        if (FullScreenUtils.isNavigationBarShow(this)) {
            params.height = FullScreenUtils.getNavigationBarHeight(this)
        }
        view.layoutParams = params
        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled", "NewApi")
    private fun initWebView() {
        webView = WebView(this)
        webView.isVerticalScrollBarEnabled = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.addJavascriptInterface(SdkJsImpl(this, callback), "FlyFunSdk")
        val container = findViewById<FrameLayout>(ResUtils.getResId(this, "ffg_webview_container", "id"))
        container.addView(webView)
        webView.viewTreeObserver.addOnGlobalLayoutListener {
            if (isSoftShowing()) {
                view.visibility = View.GONE
            } else {
                view.visibility = View.VISIBLE
            }
        }

        val webSetting = webView.settings
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.saveFormData = true
        webSetting.savePassword = true
        webSetting.builtInZoomControls = false
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(false)
        webSetting.setAppCacheEnabled(false)
        webSetting.domStorageEnabled = true
        webSetting.javaScriptEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE)
        webSetting.setAppCachePath(getDir("appcache", 0).path)
        webSetting.databasePath = getDir("databases", 0).path
        webSetting.setGeolocationDatabasePath(getDir("geolocation", 0).path)
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        CookieSyncManager.createInstance(this)
        CookieSyncManager.getInstance().sync()

        if (DeviceInfoUtils.isNetworkConnected(this)) {
            webView.loadUrl(url)
            //webView.loadUrl("file:///android_asset/test.html");
        } else {
            //这种写法可以正确解码
            webView.loadData("网络异常,请检查重试", "text/html; charset=UTF-8", null)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (view.canGoBack() && Uri.parse(url).path != "/index.html") {
                    ivReturn.visibility = View.VISIBLE
                } else {
                    ivReturn.visibility = View.GONE
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val hitTestResult = view.hitTestResult
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    //解决重定向加载的问题 return false
                    if (!view.canGoBack() || hitTestResult == null) {
                        return false
                    }
                    //解决重定向加载的问题 return false
                    if (hitTestResult.type == HitTestResult.UNKNOWN_TYPE) {
                        return false
                    }
                    view.loadUrl(url)
                }
                return false
            }

            override fun onReceivedSslError(arg0: WebView, handler: SslErrorHandler, error: SslError) {
                //部分手机浏览器不支持https，所以此处需要接受证书
                handler.proceed()
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                //progressWebView.setProgressBarProgress(newProgress);
                if (newProgress == 100 && loadingDialog != null && loadingDialog!!.isShowing) {
                    //加载完成,关闭loading
                    loadingDialog!!.dismiss()
                }
            }

            //For Android > 5.0
            override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {
                openFileChooserImplForAndroid5(filePathCallback)
                return true
            }

            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)
                if (!DeviceInfoUtils.isNetworkConnected(view.context)) {
                    tvTitle.text = "网络异常"
                    return
                }
                if (!TextUtils.isEmpty(view.title)) {
                    var viewtitle = view.title
                    if (viewtitle.length > 10) {
                        viewtitle = viewtitle.substring(0, 10)
                        viewtitle = "$viewtitle..."
                    }
                    tvTitle.text = viewtitle
                }
            }
        }

    }

    private fun isSoftShowing(): Boolean {
        //获取当屏幕内容的高度
        val screenHeight = this.window.decorView.height
        //获取View可见区域的bottom
        val rect = Rect()
        //DecorView即为activity的顶级view
        this.window.decorView.getWindowVisibleDisplayFrame(rect)
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断
        return screenHeight * 2 / 3 > rect.bottom
    }

    private fun openFileChooserImplForAndroid5(uploadMsg: ValueCallback<Array<Uri>>) {
        Logger.i("Open FileChooser For Higher than Android5")
        uploadMessageForAndroid5 = uploadMsg
        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
        contentSelectionIntent.type = "*/*"

        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "File Chooser")

        startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5)
    }

    private fun createLoadingDialog() {
        loadingDialog?.apply {
            if (isShowing) {
                dismiss()
                loadingDialog = null
            }
        }
        val builder = CircleProgressLoadingDialog.Builder(this)
        builder.setMessage(ResUtils.getResString(this, "ffg_loading_tips"), 15, intArrayOf(255, 255, 255, 255))
        builder.hasMessage(true)
        loadingDialog = builder.build()
        loadingDialog?.show()
    }


    private fun showReturnDialog() {
        if (returnDialog != null) {
            returnDialog!!.dismiss()
            returnDialog = null
        }

        returnDialog = DialogUtils.newTipsDialog(
                this,
                ResUtils.getResString(this, "ffg_gm_dialog_tv_content"),
                ResUtils.getResString(this, "ffg_gm_dialog_tv_not"),
                ResUtils.getResString(this, "ffg_gm_dialog_tv_yes"),
                {
                    if (returnDialog != null && returnDialog!!.isShowing) {
                        returnDialog!!.dismiss()
                        if (webView.canGoBack()) {
                            webView.goBack()
                        }
                    }
                },
                {
                    webView.loadUrl("javascript:clientCallJs(1000)")
                    if (returnDialog != null && returnDialog!!.isShowing) {
                        returnDialog!!.dismiss()
                    }
                }
        )
        returnDialog?.show()
    }

    private fun showExitDialog() {
        if (exitDialog != null) {
            exitDialog!!.dismiss()
            exitDialog = null
        }

        exitDialog = DialogUtils.newTipsDialog(
                this,
                ResUtils.getResString(this, "ffg_gm_dialog_tv_content"),
                ResUtils.getResString(this, "ffg_gm_dialog_tv_not"),
                ResUtils.getResString(this, "ffg_gm_dialog_tv_yes"),
                {
                    if (exitDialog != null && exitDialog!!.isShowing) {
                        exitDialog!!.dismiss()
                        finish()
                    }
                },
                {
                    webView.loadUrl("javascript:clientCallJs(1001)")
                    if (exitDialog != null && exitDialog!!.isShowing) {
                        exitDialog!!.dismiss()
                    }
                }
        )
        exitDialog?.show()
    }

    private fun doFinish() {
        if (webView.canGoBack()) {
            val uri = Uri.parse(webView.url)
            if (uri.path == "/index.html") {
                finish()
                return
            }
            if (uri.path == "/chat.html") {
                val state = uri.getQueryParameter("state")
                if (state == "0") {
                    showReturnDialog()
                    return
                }
            }
            webView.goBack()
            return
        }
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5) {
            Logger.d("onActivityResult FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5")
            if (uploadMessageForAndroid5 == null) {
                Logger.d("mUploadMessageForAndroid5 == null")
                return
            }
            val result = if (intent == null || resultCode != RESULT_OK) null else intent.data
            if (result == null) {
                uploadMessageForAndroid5!!.onReceiveValue(arrayOf())
            }
            result?.apply {
                uploadMessageForAndroid5!!.onReceiveValue(arrayOf(this))
            }
            uploadMessageForAndroid5 = null

        }
    }

    override fun onBackPressed() {
        doFinish()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        val viewGroup = window.decorView as ViewGroup
        viewGroup.removeAllViews()
        webView.destroy()
    }

    companion object {
        private const val FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5 = 2

        private var url = ""

        fun start(activity: Activity, url: String) {
            Companion.url = url
            activity.startActivity(Intent(activity, HybridActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}