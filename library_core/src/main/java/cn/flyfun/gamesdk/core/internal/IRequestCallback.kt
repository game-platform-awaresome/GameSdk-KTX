package cn.flyfun.gamesdk.core.internal

import cn.flyfun.gamesdk.core.entity.ResultInfo

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
interface IRequestCallback {

    fun onResponse(resultInfo: ResultInfo)
}