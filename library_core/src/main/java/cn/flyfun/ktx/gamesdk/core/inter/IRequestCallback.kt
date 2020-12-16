package cn.flyfun.ktx.gamesdk.core.inter

import cn.flyfun.ktx.gamesdk.core.entity.ResultInfo

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
interface IRequestCallback {

    fun onResponse(resultInfo: ResultInfo)
}