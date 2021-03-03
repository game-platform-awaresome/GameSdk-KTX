package cn.flyfun.gamesdk.core.ui.dialog

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import cn.flyfun.support.DensityUtils
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * @author #Suyghur,
 * Created on 2021/3/3
 */
class DotLoadingView : View {

    private var mDuration = 2000L
    private val mDotList: MutableList<Dot> = mutableListOf()
    private var mMaximumCircleRadius = 0f
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mDefaultSize = 0
    private var mCircleColor = Color.WHITE

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        mMaximumCircleRadius = DensityUtils.dip2px(context, 5.0f).toFloat()
        mDefaultSize = DensityUtils.dip2px(context, 50.0f)

        mPaint.color = mCircleColor
        mPaint.style = Paint.Style.FILL

        mDotList.clear()
        for (i in 0 until 5) {
            val time = (500 - i * 100).toLong()
            val dot = if (i == 0) {
                Dot(mMaximumCircleRadius, time)
            } else {
                Dot((mDotList[i - 1].mRadius * sqrt(0.8)).toFloat(), time)
            }
            mDotList.add(dot)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val value = measureMinSize(widthMeasureSpec, heightMeasureSpec, mDefaultSize)
        setMeasuredDimension(value, value)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (dot in mDotList) {
            canvas?.drawCircle(dot.mCenterX, dot.mCenterY, dot.mRadius, mPaint)
        }
        postInvalidateDelayed(10)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        for (dot in mDotList) {
            dot.mValueAnimator.cancel()
            dot.mValueAnimator.start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        for (dot in mDotList) {
            dot.mValueAnimator.cancel()
        }
    }

    private fun measureMinSize(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
        defaultSize: Int
    ): Int {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        if (wMode == MeasureSpec.AT_MOST || wMode == MeasureSpec.UNSPECIFIED) {
            width = defaultSize
        }
        if (hMode == MeasureSpec.AT_MOST || hMode == MeasureSpec.UNSPECIFIED) {
            height = defaultSize
        }
        return min(width, height)
    }

    inner class Dot(var radius: Float, var delay: Long) {
        var mRadius = 0.0f
        var mCenterX = -1000f
        var mCenterY = -1000f
        var mStartProgress = 0.0f
        var endValue = 360.0f + 360.0f - (360.0f / 5.0f)
        val mValueAnimator: ValueAnimator = ValueAnimator.ofFloat(0.0f, endValue)
        private var mCurrentProgress = 0.0f


        init {
            this.mRadius = radius
            mValueAnimator.duration = mDuration - delay
            mValueAnimator.interpolator = AccelerateInterpolator()
            mValueAnimator.startDelay = delay
            mValueAnimator.addUpdateListener {
                mCurrentProgress = mStartProgress + it.animatedValue as Float
                mCenterX =
                    (width / 2.0f + (width / 2.0f - mMaximumCircleRadius) * cos(mCurrentProgress * Math.PI / 180)).toFloat()
                mCenterY =
                    (height / 2.0f + (height / 2.0f - mMaximumCircleRadius) * sin(mCurrentProgress * Math.PI / 180)).toFloat()
            }

            mValueAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    mStartProgress = mCurrentProgress % 360.0f
                    animation?.start()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
        }
    }
}