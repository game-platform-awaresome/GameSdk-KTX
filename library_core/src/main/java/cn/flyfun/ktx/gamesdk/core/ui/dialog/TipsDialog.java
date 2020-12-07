package cn.flyfun.ktx.gamesdk.core.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import cn.flyfun.support.ResUtils;


/**
 * 提示框dialog
 * 样式如下：
 * -------------------------
 * |                       X
 * |                       |
 * ｜         内容          ｜
 * ｜                      ｜
 * -------------------------
 * ｜    取消    ｜   确定   ｜
 * -------------------------
 *
 * @author #Suyghur.
 * Created on 2020/7/30
 */
public class TipsDialog extends Dialog {

    private ImageView close;
    private TextView title;
    private TextView content;
    private TextView right;
    private TextView left;
    private int colorP, colorN;

    public TipsDialog(Context context, Boolean showClose, Boolean doubleButton) {
        super(context);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView(context, showClose, doubleButton);
    }

    public TipsDialog(Context context, Boolean showClose, Boolean doubleButton, int themId) {
        super(context, themId);
        setCanceledOnTouchOutside(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView(context, showClose, doubleButton);
    }


    private void initView(Context context, boolean showClose, boolean doubleButton) {
        View view = LayoutInflater.from(context).inflate(ResUtils.getResId(context, "ffg_dialog_tips", "layout"), null);
        setContentView(view);


        WindowManager.LayoutParams attr = getWindow().getAttributes();
        if (attr != null) {
            //设置dialog 在布局中的位置
            attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            attr.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            attr.gravity = Gravity.CENTER;
        }

        colorP = context.getResources().getColor(ResUtils.getResId(context, "ffg_color_white", "color"));
        colorN = context.getResources().getColor(ResUtils.getResId(context, "ffg_color_gray_50", "color"));
        close = view.findViewById(ResUtils.getResId(context, "ffg_dialog_close", "id"));
        title = view.findViewById(ResUtils.getResId(context, "ffg_dialog_title", "id"));
        content = view.findViewById(ResUtils.getResId(context, "ffg_dialog_content", "id"));

        left = view.findViewById(ResUtils.getResId(context, "ffg_dialog_left", "id"));
        View line = view.findViewById(ResUtils.getResId(context, "ffg_dialog_action_line", "id"));
        right = view.findViewById(ResUtils.getResId(context, "ffg_dialog_right", "id"));
        if (!showClose) {
            close.setVisibility(View.GONE);
        }

        if (!doubleButton) {
            right.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            left.setTextColor(colorP);
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    cancel();
                }
            }
        });
    }

    public void setLeftText(String s) {
        if (!TextUtils.isEmpty(s) && left != null) {
            left.setText(s);
        }
    }

    public void setRightText(String s) {
        if (!TextUtils.isEmpty(s) && right != null) {
            right.setText(s);
        }
    }

    public void setTitleText(String text) {
        if (title == null) {
            return;
        }
        if (!TextUtils.isEmpty(text)) {
            title.setText(text);
            title.setVisibility(View.VISIBLE);
        } else {
            title.setVisibility(View.GONE);
        }
    }

    public void changeRightTextP() {
        left.setTextColor(colorN);
        right.setTextColor(colorP);
    }

    public void setContentText(String text) {
        if (!TextUtils.isEmpty(text) && content != null) {
            content.setText(Html.fromHtml(text));
        }
    }

    public void setLeftListener(View.OnClickListener listener) {
        if (left != null) {
            left.setOnClickListener(listener);
        }
    }

    public void setRightListener(View.OnClickListener listener) {
        if (right != null) {
            right.setOnClickListener(listener);
        }
    }

    public void setOnCloseListener(View.OnClickListener listener) {
        if (close != null)
            close.setOnClickListener(listener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 处理返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
