package cn.flyfun.gamesdk.core.network

import android.app.Activity
import android.text.TextUtils
import android.webkit.JavascriptInterface
import cn.flyfun.support.JsonUtils
import org.json.JSONObject
import java.lang.Exception

/**
 * @author #Suyghur.
 * Created on 2020/12/2
 */
class SdkJsImpl constructor(val activity: Activity, val callback: IJsCallback) {

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
                callback.onCallback(type, ext)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface IJsCallback {
        fun onCallback(tag: Int, ext: String)
    }
}