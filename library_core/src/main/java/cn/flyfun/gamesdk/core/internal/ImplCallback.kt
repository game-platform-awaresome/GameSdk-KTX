package cn.flyfun.gamesdk.core.internal


/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
interface ImplCallback {
    fun onSuccess(result: String)

    fun onFailed(result: String)
}