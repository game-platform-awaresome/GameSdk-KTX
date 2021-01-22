package cn.flyfun.gamesdk.core.network

import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.gamesdk.core.entity.FileEntity
import cn.flyfun.support.volley.*
import cn.flyfun.support.volley.toolbox.HttpHeaderParser
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashMap

/**
 * @author #Suyghur,
 * Created on 2021/1/22
 */
class MultipartRequest : Request<String> {

    private val multipartFromData = "multipart/form-data"
    private val boundary = "---------${UUID.randomUUID()}"
    private val newLine = "\r\n"

    private var mParams: Map<String, Any>? = null
    private var mFileEntityList: List<FileEntity>? = null
    private var mFileEntity: FileEntity? = null
    private var mListener: Response.Listener<String>? = null
    private var mCharSet = Charset.defaultCharset()

    private var isSingleFile = false

    constructor(url: String, params: HashMap<String, Any>, fileEntityList: List<FileEntity>, listener: Response.Listener<String>, errorListener: Response.ErrorListener) : this(url, params, Charset.defaultCharset(), fileEntityList, listener, errorListener)

    constructor(url: String, params: HashMap<String, Any>, charSet: Charset, fileEntityList: List<FileEntity>, listener: Response.Listener<String>, errorListener: Response.ErrorListener) : super(Method.POST, url, errorListener) {
        mParams = params
        mCharSet = charSet
        mFileEntityList = fileEntityList
        mListener = listener
        isSingleFile = false
    }

    constructor(url: String, params: HashMap<String, Any>, fileEntity: FileEntity, listener: Response.Listener<String>, errorListener: Response.ErrorListener) : this(url, params, Charset.defaultCharset(), fileEntity, listener, errorListener)

    constructor(url: String, params: HashMap<String, Any>, charSet: Charset, fileEntity: FileEntity, listener: Response.Listener<String>, errorListener: Response.ErrorListener) : super(Method.POST, url, errorListener) {
        mParams = params
        mCharSet = charSet
        mFileEntity = fileEntity
        mListener = listener
        isSingleFile = true
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
        return try {
            Logger.d("MultipartRequest : ${response?.data}")
            Response.success(response?.data.toString(), HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            e.localizedMessage?.apply {
                Logger.e(this)
            }
            e.printStackTrace()
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: String?) {
        mListener?.onResponse(response)
    }

    @Throws(AuthFailureError::class)
    override fun getBody(): ByteArray {
        return if (isSingleFile) {
            singleFileUp()
        } else {
            multipleFileUp()
        }
    }

    override fun getBodyContentType(): String {
        return "$multipartFromData;boundary=$boundary"
    }

    @Throws(AuthFailureError::class)
    private fun multipleFileUp(): ByteArray {
        return if (mParams.isNullOrEmpty() && mFileEntityList.isNullOrEmpty()) {
            super.getBody()
        } else {
            val bos = ByteArrayOutputStream()
            if (!mParams.isNullOrEmpty()) {
                paramsFormat(bos)
            }
            if (!mFileEntityList.isNullOrEmpty()) {
                for (fileEntity in mFileEntityList!!) {
                    fileFormat(bos, fileEntity)
                }
            }
            endLine(bos)
            bos.toByteArray()
        }
    }

    @Throws(AuthFailureError::class)
    private fun singleFileUp(): ByteArray {
        return if (mParams.isNullOrEmpty() && mFileEntity == null) {
            super.getBody()
        } else {
            val bos = ByteArrayOutputStream()
            if (!mParams.isNullOrEmpty()) {
                paramsFormat(bos)
            }
            if (mFileEntity != null) {
                fileFormat(bos, mFileEntity!!)
            }
            endLine(bos)
            bos.toByteArray()
        }
    }

    private fun endLine(bos: ByteArrayOutputStream) {
        val line = "--$boundary--$newLine"
        try {
            bos.write(line.toByteArray(mCharSet))
        } catch (e: IOException) {
            e.localizedMessage?.apply {
                Logger.e(this)
            }
            e.printStackTrace()
        }
    }

    private fun fileFormat(bos: ByteArrayOutputStream, fileEntity: FileEntity) {
        val sb = StringBuilder()
        sb.append("--").append(boundary).append(newLine)
        sb.append("Content-Disposition: form-data; name=\"").append(fileEntity.name).append("\"").append(";filename=\"").append(fileEntity.fileName).append("\"").append(newLine)
        sb.append("Content-Type: ").append(fileEntity.mime).append(";charset=").append(mCharSet).append(newLine)
        sb.append(newLine)
        try {
            bos.write(sb.toString().toByteArray(mCharSet))
            bos.write(fileEntity.getFileBytes())
            bos.write(newLine.toByteArray(mCharSet))
        } catch (e: IOException) {
            e.localizedMessage?.apply {
                Logger.e(this)
            }
            e.printStackTrace()
        }
    }

    private fun paramsFormat(bos: ByteArrayOutputStream) {
        val sb = StringBuilder()
        mParams?.apply {

            for (key in this.keys) {
                val value = this[key]
                sb.append("--").append(boundary).append(newLine)
                sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(newLine)
                sb.append(newLine)
                sb.append(value).append(newLine)
            }
            try {
                bos.write(sb.toString().toByteArray(mCharSet))
            } catch (e: IOException) {
                e.localizedMessage?.apply {
                    Logger.e(this)
                }
                e.printStackTrace()
            }

        }
    }
}