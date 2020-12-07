package cn.flyfun.ktx.gamesdk.core.network

import android.content.Context
import cn.flyfun.ktx.gamesdk.base.utils.Logger
import cn.flyfun.ktx.gamesdk.core.entity.ResultInfo
import cn.flyfun.ktx.gamesdk.core.inter.IRequestCallback
import cn.flyfun.support.JsonUtils
import cn.flyfun.support.StrUtils
import cn.flyfun.support.encryption.aes.AesUtils
import cn.flyfun.support.encryption.rsa.RsaUtils
import cn.flyfun.support.volley.DefaultRetryPolicy
import cn.flyfun.support.volley.Response
import cn.flyfun.support.volley.VolleyError
import cn.flyfun.support.volley.toolbox.JsonObjectRequest
import cn.flyfun.support.volley.toolbox.JsonRequest
import cn.flyfun.support.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder


/**
 * @author #Suyghur.
 * Created on 2020/12/2
 */
object VolleyRequest {

    private const val MAX_TIMEOUT = 10 * 1000

    fun post(context: Context, url: String, jsonObject: JSONObject, callback: IRequestCallback) {
        try {
            //A. 随机产生一个128位(16字节)的AES钥匙，使用AES对数据进行加密得到加密的数据。
            //B. 使用RSA公钥对上面的随机AES钥匙进行加密，得到加密后的AES钥匙。
            //val aesKey = "1234567890abcdef"
            val time = (System.currentTimeMillis() / 1000).toString()
            val aesKey = StrUtils.getRandomString(16)
            val content = jsonObject.toString()

            val p = URLEncoder.encode(AesUtils.encrypt(aesKey, content), "UTF-8")
            val ts = URLEncoder.encode(RsaUtils.encryptByPublicKey(aesKey), "UTF-8")

            val logTag = "logTag $time : "
            Logger.logHandler("请求地址 : $url \n")
            Logger.logHandler("请求参数 : $content \n")
            Logger.d("请求参数 : $content")
            Logger.d("$logTag$url?p=$p&ts=$ts")

            val requestJson = JSONObject()
            requestJson.put("p", p)
            requestJson.put("ts", ts)
            postByVolley(context, url, requestJson, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun get(context: Context, url: String, callback: IRequestCallback) {
        Logger.logHandler("请求地址 : $url \n")
        val request: StringRequest = object : StringRequest(Method.GET, url, Response.Listener {
            val resultInfo = ResultInfo()
            resultInfo.code = 0
            resultInfo.msg = ""
            it?.apply {
                resultInfo.data = it
                callback.onResponse(resultInfo)
                Logger.logHandler("返回内容 : $resultInfo \n")
            }
        }, Response.ErrorListener {
            it?.apply {
                callback.onResponse(getErrorResultInfo(it))
            }
        }) {}
        //关闭缓存策略
        request.setShouldCache(false)
        //设置超时时间
        request.retryPolicy = DefaultRetryPolicy(MAX_TIMEOUT, 1, 1.0f)
        VolleySingleton.getInstance(context.applicationContext).addToRequestQueue(request)
    }

    private fun postByVolley(context: Context, url: String, params: JSONObject, callback: IRequestCallback) {
        val request: JsonRequest<JSONObject> = object : JsonObjectRequest(Method.POST, url, params, Response.Listener {
            val resultInfo = ResultInfo()
            resultInfo.code = -1
            resultInfo.msg = "接口请求出错"
            it?.apply {
                try {
                    resultInfo.code = it.getInt("code")
                    resultInfo.msg = it.getString("message")
                    if (JsonUtils.hasJsonKey(it, "data")) {
                        resultInfo.data = decodeResult(it.getJSONObject("data"))
                    } else {
                        resultInfo.data = ""
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    resultInfo.code = -1
                    resultInfo.msg = "解析数据异常"
                }
            }
            Logger.logHandler("返回内容 : $resultInfo")
            Logger.d("postByVolley : $resultInfo")
            callback.onResponse(resultInfo)
        }, Response.ErrorListener {
            it?.apply {
                Logger.e("postByVolley onErrorResponse : $it")
                callback.onResponse(getErrorResultInfo(it))
            }
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers: HashMap<String, String> = HashMap()
                headers["Accept"] = "application/json"
                headers["Content-Type"] = "application/json;charset=UTF-8"
                return headers
            }
        }
        //设置超时时间
        request.retryPolicy = DefaultRetryPolicy(MAX_TIMEOUT, 1, 1.0f)
        VolleySingleton.getInstance(context.applicationContext).addToRequestQueue(request)
    }

    @Throws(Exception::class)
    private fun decodeResult(jsonObject: JSONObject): String {
        val p = URLDecoder.decode(jsonObject.getString("p"), "UTF-8")
        val ts = URLDecoder.decode(jsonObject.getString("ts"), "UTF-8")
        val aesKey = RsaUtils.decryptByPublicKey(ts)
        return AesUtils.decrypt(aesKey, p)
    }


    private fun getErrorResultInfo(volleyError: VolleyError): ResultInfo {
        val resultInfo = ResultInfo()
        resultInfo.code = 400
        resultInfo.msg = "网络异常"
        volleyError.networkResponse?.apply {
            resultInfo.code = statusCode
            resultInfo.msg = volleyError.message.toString()
        }

        resultInfo.data = ""
        return resultInfo
    }
}