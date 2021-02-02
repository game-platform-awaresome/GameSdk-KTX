package cn.flyfun.gamesdk.core.internal

import android.content.Context

/**
 * @author #Suyghur.
 * Created on 2020/12/1
 */
interface IEventTrace {

    fun onInitialize(context: Context)

    fun onLogin(context: Context)

    fun onRegister(context: Context)

    fun onCharge(context: Context, eventMap: HashMap<String, Any>)

    fun onRoleCreate(context: Context, eventMap: HashMap<String, Any>)

    fun onRoleLauncher(context: Context, eventMap: HashMap<String, Any>)

    fun onResume(context: Context)

    fun onPause(context: Context)
}