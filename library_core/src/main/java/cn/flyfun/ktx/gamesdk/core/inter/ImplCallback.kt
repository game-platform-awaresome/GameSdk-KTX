package cn.flyfun.ktx.gamesdk.core.inter

import androidx.annotation.NonNull

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
interface ImplCallback {
    fun onSuccess(@NonNull result : String)

    fun onFailed(@NonNull result: String)
}