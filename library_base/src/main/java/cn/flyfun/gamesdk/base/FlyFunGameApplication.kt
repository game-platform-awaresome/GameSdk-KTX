package cn.flyfun.gamesdk.base

import android.app.Application
import android.content.Context
import cn.flyfun.support.multidex.MultiDex
import cn.flyfun.zap.Zap

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
open class FlyFunGameApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
        FlyFunGame.getInstance().attachBaseContext(this, base)
    }

    override fun onCreate() {
        super.onCreate()
        FlyFunGame.getInstance().initApplication(this)
    }
}