package cn.flyfun.ktx.gamesdk.base.inter

import androidx.annotation.NonNull

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
interface ICallback {

    fun onResult(code: Int, @NonNull result: String)
}