package cn.flyfun.gamesdk.core.utils

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.widget.FrameLayout


/**
 * @author #Suyghur.
 * Created on 2020/12/10
 */
class AndroidBug5497Workaround private constructor(private val activity: Activity) {

    private var mChildOfContent: View
    private var frameLayoutParams: FrameLayout.LayoutParams
    private var usableHeightPrevious: Int = 0

    init {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        mChildOfContent = content.getChildAt(0)
        mChildOfContent.viewTreeObserver.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent()
        }
        frameLayoutParams = mChildOfContent.layoutParams as FrameLayout.LayoutParams
    }

    private fun possiblyResizeChildOfContent() {
        val usableHeightNow: Int = computeUsableHeight()
        if (usableHeightNow != usableHeightPrevious) {
            var usableHeightSansKeyboard = mChildOfContent.rootView.height

            //这个判断是为了解决19之前的版本不支持沉浸式状态栏导致布局显示不完全的问题
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                val frame = Rect()
                activity.window.decorView.getWindowVisibleDisplayFrame(frame)
                val statusBarHeight = frame.top
                usableHeightSansKeyboard -= statusBarHeight
            }
            val heightDifference = usableHeightSansKeyboard - usableHeightNow
            if (heightDifference > usableHeightSansKeyboard / 4) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard
            }
            mChildOfContent.requestLayout()
            usableHeightPrevious = usableHeightNow
        }
    }

    private fun computeUsableHeight(): Int {
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top
        val r = Rect()
        mChildOfContent.getWindowVisibleDisplayFrame(r)
        //这个判断是为了解决19之后的版本在弹出软键盘时，键盘和推上去的布局（adjustResize）之间有黑色区域的问题
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            r.bottom - r.top + statusBarHeight
        } else r.bottom - r.top
    }


    companion object {
        fun assistActivity(activity: Activity) {
            AndroidBug5497Workaround(activity)
        }
    }

}