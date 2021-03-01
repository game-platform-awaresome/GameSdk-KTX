package cn.flyfun.gamesdk.core.fama

import android.content.Context
import cn.flyfun.gamesdk.core.fama.channel.adjust.AdjustImpl
import cn.flyfun.gamesdk.core.fama.channel.firebase.FirebaseImpl
import cn.flyfun.gamesdk.core.internal.IEventObserver
import java.util.HashMap


/**
 * @author #Suyghur.
 * Created on 2020/12/1
 */
class EventSubject {

    private val observers: MutableList<IEventObserver> = mutableListOf()

    fun attach(ob: IEventObserver) {
        observers.add(ob)
    }

    fun removeEventObserver(ob: IEventObserver) {
        if (observers.contains(ob)) {
            observers.remove(ob)
        }
    }

    fun clear() {
        if (!observers.isNullOrEmpty()) {
            observers.clear()
        }
    }

    fun onInitialize(context: Context) {
        for (ob in observers) {
            ob.onInitialize(context)
        }
    }

    fun onLogin(context: Context) {
        for (ob in observers) {
            ob.onLogin(context)
        }
    }

    fun onRegister(context: Context) {
        for (ob in observers) {
            ob.onRegister(context)
        }
    }

    fun onCharge(context: Context, eventMap: HashMap<String, Any>) {
        for (ob in observers) {
            ob.onCharge(context, eventMap)
        }
    }

    fun onRoleCreate(context: Context, eventMap: HashMap<String, Any>) {
        for (ob in observers) {
            ob.onRoleCreate(context, eventMap)
        }
    }

    fun onRoleLauncher(context: Context, eventMap: HashMap<String, Any>) {
        for (ob in observers) {
            ob.onRoleLauncher(context, eventMap)
        }
    }

    fun onResume(context: Context) {
        for (ob in observers) {
            ob.onResume(context)
        }
    }

    fun onPause(context: Context) {
        for (ob in observers) {
            ob.onPause(context)
        }
    }
}