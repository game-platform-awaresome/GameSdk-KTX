package cn.flyfun.gamesdk.core.ui

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import cn.flyfun.support.ResUtils


/**
 * @author #Suyghur.
 * Created on 2020/12/9
 */
class EventEditText : ConstraintLayout, View.OnClickListener, TextWatcher,
    View.OnFocusChangeListener {

    lateinit var focusView: View
    lateinit var leftImageView: ImageView private set
    lateinit var rightImageView: ImageView private set
    lateinit var editText: EditText private set

    private var isShowText = false

    var eventEditTextListener: EventEditTextListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        LayoutInflater.from(context)
            .inflate(ResUtils.getResId(context, "ffg_event_edittext", "layout"), this)
        focusView = findViewById(ResUtils.getResId(context, "ffg_eet_focus_view", "id"))
        rightImageView = findViewById(ResUtils.getResId(context, "ffg_eet_iv_right", "id"))
        leftImageView = findViewById(ResUtils.getResId(context, "ffg_eet_iv_left", "id"))
        editText = findViewById(ResUtils.getResId(context, "ffg_eet_input", "id"))
        focusView.visibility = View.GONE

        initListener()
    }

    private fun initListener() {
        rightImageView.setOnClickListener(this)
        editText.onFocusChangeListener = this
        editText.addTextChangedListener(this)
    }

    /**
     * 禁止EditText输入空格和换行符
     */
    fun filterSpace() {
        val filter = InputFilter { source, _, _, _, _, _ ->
            if (source == " " || source.toString().contentEquals("\n")) {
                ""
            } else {
                null
            }
        }
        editText.filters = arrayOf(filter)
    }

    override fun onClick(v: View?) {
        eventEditTextListener?.onViewClick(v)

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        eventEditTextListener?.beforeTextChanged(editText, s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        eventEditTextListener?.onTextChanged(editText, s, start, before, count)
    }

    override fun afterTextChanged(s: Editable?) {
        eventEditTextListener?.afterTextChanged(editText, s)
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        eventEditTextListener?.onFocusChange(v, hasFocus)
        focusView.apply {
            visibility = if (hasFocus) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    interface EventEditTextListener {
        fun beforeTextChanged(v: View, s: CharSequence?, start: Int, count: Int, after: Int)

        fun onTextChanged(v: View, s: CharSequence?, start: Int, before: Int, count: Int)

        fun afterTextChanged(v: View, s: Editable?)

        fun onViewClick(v: View?)

        fun onFocusChange(v: View?, hasFocus: Boolean)
    }
}