package cn.flyfun.ktx.gamesdk.core.inter

import androidx.annotation.NonNull
import cn.flyfun.ktx.gamesdk.core.entity.ResultInfo

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
interface IRequestCallback {

    fun onResponse(@NonNull resultInfo: ResultInfo)
}