package com.flyfun.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import cn.flyfun.support.HostModelUtils
import cn.flyfun.support.jarvis.Toast

/**
 * @author #Suyghur.
 * Created on 2020/12/7
 */
class EnvActivity : Activity(), View.OnClickListener {

    companion object {
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, EnvActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val btnDev = Button(this)
        btnDev.text = "01 开发环境"
        btnDev.tag = HostModelUtils.ENV_DEV
        btnDev.setOnClickListener(this)
        layout.addView(btnDev)

        val btnTest = Button(this)
        btnTest.text = "02 测试环境"
        btnTest.tag = HostModelUtils.ENV_TEST
        btnTest.setOnClickListener(this)
        layout.addView(btnTest)

        val btnOnline = Button(this)
        btnOnline.text = "03 线上环境"
        btnOnline.tag = HostModelUtils.ENV_ONLINE
        btnOnline.setOnClickListener(this)
        layout.addView(btnOnline)

        setContentView(layout)
    }

    override fun onClick(v: View?) {
        v?.apply {
            HostModelUtils.setHostModel(this@EnvActivity, tag as Int)
            Toast.toastInfo(this@EnvActivity,"接口环境切换成功，重启应用后生效")
        }
    }
}