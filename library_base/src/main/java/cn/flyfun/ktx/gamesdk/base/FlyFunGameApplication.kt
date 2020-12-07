package cn.flyfun.ktx.gamesdk.base

import android.app.Application
import android.content.Context

/**
 * @author #Suyghur.
 * Created on 2020/11/30
 */
open class FlyFunGameApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        FlyFunGame.getInstance().attachBaseContext(this, base)
    }

    override fun onCreate() {
        super.onCreate()
        FlyFunGame.getInstance().initApplication(this)
    }
}