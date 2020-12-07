package cn.flyfun.ktx.gamesdk.core.inter

import android.content.Context

/**
 * @author #Suyghur.
 * Created on 2020/12/1
 */
interface IEventTrace {

    fun onInitialize(context: Context)

    fun onLogin()

    fun onRegister()

    fun onCharge(eventMap: HashMap<String, Any>)

    fun onRoleCreate(eventMap: HashMap<String, Any>)

    fun onRoleLauncher(eventMap: HashMap<String, Any>)

    fun onResume()

    fun onPause()
}