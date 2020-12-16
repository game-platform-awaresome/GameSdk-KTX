package cn.flyfun.ktx.gamesdk.core.ui

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import cn.flyfun.ktx.gamesdk.core.ui.dialog.InitDialog
import cn.flyfun.ktx.gamesdk.core.ui.dialog.TipsDialog
import cn.flyfun.support.ui.circleprogress.CircleProgressLoadingDialog


/**
 * @author #Suyghur.
 * Created on 2020/12/3
 */
object DialogUtils {


    fun newInitNoticeDialog(context: Context, url: String, onClickListener: View.OnClickListener): InitDialog? {
        if (TextUtils.isEmpty(url)) {
            return null
        }
        val dialog = InitDialog(context, url)
        dialog.button.setOnClickListener(onClickListener)
        return dialog
    }


    fun newTipsDialog(context: Context, content: String, leftText: String, rightText: String, leftListener: View.OnClickListener, rightListener: View.OnClickListener): TipsDialog? {
        if (TextUtils.isEmpty(content)) {
            return null
        }
        val tipsDialog = TipsDialog(context, true)
        tipsDialog.content.text = content
        tipsDialog.leftButton.text = leftText
        tipsDialog.rightButton.text = rightText
        tipsDialog.leftButton.setOnClickListener(leftListener)
        tipsDialog.rightButton.setOnClickListener(rightListener)
        return tipsDialog
    }

    fun newExitDialog(context: Context, leftListener: View.OnClickListener, rightListener: View.OnClickListener): TipsDialog {
        val tipsDialog = TipsDialog(context, true)
        tipsDialog.content.text = "您确定立即退出游戏吗？"
        tipsDialog.leftButton.text = "下次再见"
        tipsDialog.rightButton.text = "再玩一下"
        tipsDialog.leftButton.setOnClickListener(leftListener)
        tipsDialog.rightButton.setOnClickListener(rightListener)
        return tipsDialog
    }

    fun showCircleProgressLoadingDialog(context: Context, msg: String): CircleProgressLoadingDialog {
        return if (TextUtils.isEmpty(msg)) {
            CircleProgressLoadingDialog.Builder(context)
                    .hasMessage(false)
                    .build()
        } else {
            CircleProgressLoadingDialog.Builder(context)
                    .setMessage(msg, 15, intArrayOf(255, 255, 255, 255))
                    .hasMessage(true)
                    .build()
        }
    }

}