package cn.flyfun.gamesdk.core.internal

import cn.flyfun.support.volley.VolleyError

/**
 * @author #Suyghur,
 * Created on 2021/2/19
 */
interface IFileRequestCallback {

    fun onResponse(result: String)

    fun onErrorResponse(error: VolleyError)
}