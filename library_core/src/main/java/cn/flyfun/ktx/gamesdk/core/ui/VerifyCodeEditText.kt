package cn.flyfun.ktx.gamesdk.core.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cn.flyfun.support.ResUtils


/**
 * @author #Suyghur.
 * Created on 2020/12/10
 */
class VerifyCodeEditText : ConstraintLayout, View.OnClickListener, TextWatcher, View.OnFocusChangeListener {


    lateinit var focusView: View private set
    lateinit var leftImageView: ImageView private set

    lateinit var editText: EditText private set

    lateinit var textView: TextView private set

    var verifyCodeEditTextListener: VerifyCodeEditTextListener? = null


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    fun initView(context: Context) {
        LayoutInflater.from(context).inflate(ResUtils.getResId(context, "ffg_verify_code_edittext", "layout"), this)
        focusView = findViewById(ResUtils.getResId(context, "ffg_vcet_focus_view", "id"))
        leftImageView = findViewById(ResUtils.getResId(context, "ffg_vcet_left", "id"))
        editText = findViewById(ResUtils.getResId(context, "ffg_vcet_input", "id"))
        textView = findViewById(ResUtils.getResId(context, "ffg_vcet_send", "id"))
        focusView.visibility = View.GONE

        initListener()
    }

    private fun initListener() {
        this.editText.onFocusChangeListener = this
        this.editText.addTextChangedListener(this)
    }


    override fun onClick(v: View?) {
        verifyCodeEditTextListener?.onViewClick(v)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        verifyCodeEditTextListener?.beforeTextChanged(editText, s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        verifyCodeEditTextListener?.onTextChanged(editText, s, start, before, count)
    }

    override fun afterTextChanged(s: Editable?) {
        verifyCodeEditTextListener?.afterTextChanged(editText, s)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        verifyCodeEditTextListener?.onFocusChange(editText, hasFocus)
        focusView.apply {
            visibility = if (hasFocus) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    interface VerifyCodeEditTextListener {
        fun beforeTextChanged(v: View, s: CharSequence?, start: Int, count: Int, after: Int)

        fun onTextChanged(v: View, s: CharSequence?, start: Int, before: Int, count: Int)

        fun afterTextChanged(v: View, s: Editable?)

        fun onViewClick(v: View?)

        fun onFocusChange(v: View?, hasFocus: Boolean)
    }
}