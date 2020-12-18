package cn.flyfun.gamesdk.core.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.Button
import android.widget.TextView
import cn.flyfun.support.ResUtils

/**
 * 提示框dialog
 * 样式如下：
 * -------------------------
 * |                       |
 * |                       |
 * ｜         内容          ｜
 * ｜                      ｜
 * -------------------------
 * ｜    取消    ｜   确定   ｜
 * -------------------------
 * @author #Suyghur.
 * Created on 2020/12/10
 */
class TipsDialog(context: Context, doubleButton: Boolean) : Dialog(context) {

    lateinit var content: TextView private set
    lateinit var leftButton: Button private set
    lateinit var rightButton: Button private set

    init {
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        initView(context, doubleButton)
    }

    private fun initView(context: Context, doubleButton: Boolean) {
        val view = LayoutInflater.from(context).inflate(ResUtils.getResId(context, "ffg_dialog_tips", "layout"), null)
        setContentView(view)

        val attr = window?.attributes as WindowManager.LayoutParams
        //设置dialog在布局中的位置
        attr.gravity = Gravity.CENTER

        content = view.findViewById(ResUtils.getResId(context, "ffg_dialog_content", "id"))

        leftButton = view.findViewById(ResUtils.getResId(context, "ffg_dialog_left", "id"))
        rightButton = view.findViewById(ResUtils.getResId(context, "ffg_dialog_right", "id"))

        if (doubleButton) {
            rightButton.visibility = View.VISIBLE
        } else {
            rightButton.visibility = View.GONE
        }
    }


}