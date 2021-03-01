package com.flyfun.demo

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import cn.flyfun.gamesdk.base.utils.Logger
import cn.flyfun.support.ResUtils

/**
 * @author #Suyghur.
 * Created on 2020/12/7
 */
class WelcomeActivity : Activity() {

    companion object {
        private const val CODE_GO_INIT = 0x000003E8
        private const val CODE_GO_GAME_ACTIVITY = 0x000003E9
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CODE_GO_INIT -> goInit()
                CODE_GO_GAME_ACTIVITY -> goGameActivity()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler.sendEmptyMessageDelayed(CODE_GO_INIT, 400)
    }

    private fun goInit() {
        val animation = AlphaAnimation(0.3f, 1.0f)
        animation.duration = 1500
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                handler.sendEmptyMessage(CODE_GO_GAME_ACTIVITY)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        setView(animation)
    }

    private fun goGameActivity() {
        Logger.i("goGameActivity -> action = ${this.packageName}")
        startActivity(Intent(this.packageName))
        finish()
    }


    private fun setView(animation: Animation) {
        val orientation = resources.configuration.orientation
        val id = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ResUtils.getResId(this, "flyfun_welcome_land", "drawable")
        } else {
            ResUtils.getResId(this, "flyfun_welcome", "drawable")
        }
        if (id == 0) {
            handler.sendEmptyMessage(CODE_GO_GAME_ACTIVITY)
            return
        }

        val layout = LinearLayout(this)
        layout.setBackgroundResource(id)
        layout.animation = animation
        setContentView(layout)

    }
}