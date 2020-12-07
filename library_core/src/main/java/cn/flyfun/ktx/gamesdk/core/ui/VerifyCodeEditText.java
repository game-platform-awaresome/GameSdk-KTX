package cn.flyfun.ktx.gamesdk.core.ui;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import cn.flyfun.support.ResUtils;

public class VerifyCodeEditText extends ConstraintLayout implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener {

    private View focusView;
    private ImageView ivLeft;
    private EditText etInput;
    private TextView tvSend;
    private VerifyCodeEditTextListener listener;

    public VerifyCodeEditText(Context context) {
        this(context, null);
    }

    public VerifyCodeEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyCodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(ResUtils.getResId(context, "ffg_verify_code_edittext", "layout"), this);
        focusView = findViewById(ResUtils.getResId(context, "ffg_vcet_focus_view", "id"));
        ivLeft = findViewById(ResUtils.getResId(context, "ffg_vcet_left", "id"));
        etInput = findViewById(ResUtils.getResId(context, "ffg_vcet_input", "id"));
        tvSend = findViewById(ResUtils.getResId(context, "ffg_vcet_send", "id"));
        focusView.setVisibility(GONE);
    }

    public void setVerifyCodeEditTextListener(VerifyCodeEditTextListener listener) {
        this.listener = listener;
    }

    private void setListener() {
        this.etInput.setOnFocusChangeListener(this);
        this.etInput.addTextChangedListener(this);
    }

    public ImageView getLeftImageView() {
        return this.ivLeft;
    }

    public EditText getEditText() {
        return this.etInput;
    }

    public TextView getTextView() {
        return this.tvSend;
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


    public interface VerifyCodeEditTextListener {
        void beforeTextChanged(View v, CharSequence s, int start, int count, int after);

        void onTextChanged(View v, CharSequence s, int start, int before, int count);

        void afterTextChanged(View v, Editable s);

        void onViewClick(View v);

        void onFocusChange(View v, boolean hasFocus);
    }
}
