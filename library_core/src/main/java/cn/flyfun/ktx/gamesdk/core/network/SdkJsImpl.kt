package cn.flyfun.ktx.gamesdk.core.network

import android.app.Activity
import android.text.TextUtils
import android.webkit.JavascriptInterface
import cn.flyfun.ktx.gamesdk.base.utils.Logger
import cn.flyfun.support.JsonUtils
import org.json.JSONObject
import java.lang.Exception

/**
 * @author #Suyghur.
 * Created on 2020/12/2
 */
class SdkJsImpl constructor(val activity: Activity) {

    private val CLICK_CLOSE = 1000

    @JavascriptInterface
    fun jsCallback(result: String) {
        try {
            var type = -1
            var ext = ""
            if (!TextUtils.isEmpty(result)) {
                val jsonObject = JSONObject(result)
                if (JsonUtils.hasJsonKey(jsonObject, "type")) {
                    type = jsonObject.getInt("type")
                }
                if (JsonUtils.hasJsonKey(jsonObject, "ext")) {
                    ext = jsonObject.getString("ext")
                }
                jsCallbackImpl(type, ext)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun jsCallbackImpl(type: Int, ext: String) {
        when (type) {
            CLICK_CLOSE -> {
                Logger.d("aaaa")
                if (!activity.isFinishing) {
                    activity.finish()
                }
            }
        }
    }
}