package cn.flyfun.gamesdk.core.network

import android.content.Context
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.entity.ResultInfo
import cn.flyfun.gamesdk.core.internal.IFileRequestCallback
import cn.flyfun.gamesdk.core.internal.IRequestCallback
import cn.flyfun.support.FileUtils
import cn.flyfun.support.JsonUtils
import cn.flyfun.support.SdkDriveTools
import cn.flyfun.support.encryption.Md5Utils
import cn.flyfun.support.volley.*
import cn.flyfun.support.volley.toolbox.HttpHeaderParser
import cn.flyfun.support.volley.toolbox.JsonObjectRequest
import cn.flyfun.support.volley.toolbox.JsonRequest
import cn.flyfun.support.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException


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
            var requestJson: JSONObject? = null
            try {
                requestJson = JSONObject(SdkDriveTools.invokeFuseJob(context, url, jsonObject.toString()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val request: JsonRequest<JSONObject> = object : JsonObjectRequest(Method.POST, url, requestJson, Response.Listener {
                val resultInfo = ResultInfo()
                resultInfo.code = -1
                resultInfo.msg = "接口请求出错"
                it?.apply {
                    try {
                        resultInfo.code = it.getInt("code")
                        resultInfo.msg = it.getString("message")
                        if (JsonUtils.hasJsonKey(it, "data")) {
                            resultInfo.data = SdkDriveTools.parseFuseJob(context, it.getString("data"))
                        } else {
                            resultInfo.data = ""
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        resultInfo.code = -1
                        resultInfo.msg = "解析数据异常"
                    }
                }
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

    fun downloadImageFile(context: Context, url: String, callback: IFileRequestCallback) {
        val cacheFolder = File(context.getExternalFilesDir(".cache")!!.absolutePath)
        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs()
        }
        val fileName = Md5Utils.encodeByMD5(url) + ".png"
        val filePath = "${context.getExternalFilesDir(".cache")!!.absolutePath}/$fileName"
        if (File(filePath).exists()) {
            callback.onResponse("image has been cached locally")
            return
        }
        val request: Request<ByteArray> = object : Request<ByteArray>(Method.GET, url, Response.ErrorListener {
            callback.onErrorResponse(it)
        }) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<ByteArray> {
                return try {
                    if (response.data == null) {
                        Response.error(ParseError(response))
                    } else {
                        Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response))
                    }
                } catch (e: OutOfMemoryError) {
                    e.printStackTrace()
                    Response.error(ParseError(e))
                }
            }

            override fun deliverResponse(response: ByteArray) {
                Logger.d("volley download image file success, start to save file ...")
                try {
                    FileUtils.saveFile(filePath, response)
                    callback.onResponse("download file success, path: $filePath")
                } catch (e: IOException) {
                    e.printStackTrace()
                    callback.onErrorResponse(ParseError(e))
                }
            }
        }
        VolleySingleton.getInstance(context.applicationContext).addToRequestQueue(request)
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