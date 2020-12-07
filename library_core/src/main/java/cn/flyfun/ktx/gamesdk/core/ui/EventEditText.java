package cn.flyfun.ktx.gamesdk.core.ui;


import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import cn.flyfun.support.ResUtils;

/**
 * @author #Suyghur.
 * Created on 2020/8/10
 */
public class EventEditText extends ConstraintLayout implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener {

    private View focusView;
    private ImageView ivLeft;
    private ImageView ivRight;
    private EditText etInput;
    private boolean isShowText;
    private EventEditTextListener listener;


    public EventEditText(Context context) {
        this(context, null);
    }

    public EventEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(ResUtils.getResId(context, "ffg_event_edittext", "layout"), this);
        focusView = findViewById(ResUtils.getResId(context, "ffg_eet_focus_view", "id"));
        ivRight = findViewById(ResUtils.getResId(context, "ffg_eet_iv_right", "id"));
        ivLeft = findViewById(ResUtils.getResId(context, "ffg_eet_iv_left", "id"));
        etInput = findViewById(ResUtils.getResId(context, "ffg_eet_input", "id"));
        focusView.setVisibility(GONE);

        setListener();
    }

    private void setListener() {
        this.ivRight.setOnClickListener(this);
        this.etInput.setOnFocusChangeListener(this);
        this.etInput.addTextChangedListener(this);
    }

    public ImageView getRightImageView() {
        return this.ivRight;
    }

    public ImageView getLeftImageView() {
        return this.ivLeft;
    }

//    public TextView getTextView() {
//        return this.tvTips;
//    }

    public EditText getEditText() {
        return this.etInput;
    }

    public void setEventEditTextListener(EventEditTextListener listener) {
        this.listener = listener;
    }


    public View getFocusView() {
        return this.focusView;
    }

//    /**
//     * 改变输入框状态，明文/密文
//     *
//     * @param drEyeOpen
//     * @param drEyeClose
//     */
//    public void changeInputModel(Drawable drEyeOpen, Drawable drEyeClose) {
//        if (!isShowText) {
//            isShowText = true;
//            ivRight.(drEyeOpen);
//            inputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//        } else {
//            isShowText = false;
//            rightReetImg.setImageDrawable(drEyeClose);
//            inputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
//        }
//
//        inputEditText.setSelection(inputEditText.length());
//    }
//
//    /**
//     * 设置身份证格式
//     */
//    public void setIdCardFormat() {
//        inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
//        String digits = "0123456789xyzXYZ";
//        inputEditText.setKeyListener(DigitsKeyListener.getInstance(digits));
//    }

    /**
     * 禁止EditText输入空格和换行符
     */
    public void filterSpace() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ") || source.toString().contentEquals("\n")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        etInput.setFilters(new InputFilter[]{filter});
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (listener != null) {
            listener.beforeTextChanged(etInput, s, start, count, after);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (listener != null)
            listener.onTextChanged(etInput, s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (listener != null) {
            listener.afterTextChanged(etInput, s);
        }
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onViewClick(v);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (listener != null) {
            listener.onFocusChange(v, hasFocus);
        }
        if (hasFocus) {
            focusView.setVisibility(VISIBLE);
        } else {
            focusView.setVisibility(GONE);
        }
    }


    public interface EventEditTextListener {
        void beforeTextChanged(View v, CharSequence s, int start, int count, int after);

        void onTextChanged(View v, CharSequence s, int start, int before, int count);

        void afterTextChanged(View v, Editable s);

        void onViewClick(View v);

        void onFocusChange(View v, boolean hasFocus);
    }
}
