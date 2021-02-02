package cn.flyfun.gamesdk.core.internal

import cn.flyfun.support.volley.VolleyError

/**
 * @author #Suyghur,
 * Created on 2021/1/22
 */
interface IFileRequestCallback {

    fun onResponse(result: String)

    fun onErrorResponse(error: VolleyError)
}