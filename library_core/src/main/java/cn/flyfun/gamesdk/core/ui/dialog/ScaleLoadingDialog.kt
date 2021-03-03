package cn.flyfun.gamesdk.core.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.TextView
import cn.flyfun.support.ResUtils

/**
 * @author #Suyghur,
 * Created on 2021/3/2
 */
class ScaleLoadingDialog(context: Context) : Dialog(context) {

    private lateinit var content: TextView

    init {
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }
        initView(context)
    }

    private fun initView(context: Context) {
        val view = LayoutInflater.from(context)
            .inflate(ResUtils.getResId(context, "ffg_dialog_scale_loading", "layout"), null)
        setContentView(view)

        content = view.findViewById(ResUtils.getResId(context, "ffg_dialog_content", "id"))
        content.visibility = View.GONE

        val attr = window?.attributes as WindowManager.LayoutParams
        //设置dialog在布局中的位置
        attr.gravity = Gravity.CENTER
    }
}